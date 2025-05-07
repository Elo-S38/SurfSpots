package com.example.surfspotsxml

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AjoutSpotActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            imageView.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_spot)

        // 🔄 Forcer l'indexation des images manuelles dans /sdcard/Download
        scanDownloadImages()

        // Champs de saisie
        val editName = findViewById<EditText>(R.id.nameEditText)
        val editLocation = findViewById<EditText>(R.id.locationEditText)
        val editDifficulty = findViewById<EditText>(R.id.difficultyEditText)
        val editSeasonStart = findViewById<EditText>(R.id.seasonStartEditText)
        val editSeasonEnd = findViewById<EditText>(R.id.seasonEndEditText)

        // DatePicker
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        editSeasonStart.setOnClickListener {
            showDatePicker { date -> editSeasonStart.setText(dateFormat.format(date)) }
        }
        editSeasonEnd.setOnClickListener {
            showDatePicker { date -> editSeasonEnd.setText(dateFormat.format(date)) }
        }

        // Checkboxes
        val cb1 = findViewById<CheckBox>(R.id.surfBreakOption1)
        val cb2 = findViewById<CheckBox>(R.id.surfBreakOption2)
        val cb3 = findViewById<CheckBox>(R.id.surfBreakOption3)
        val cb4 = findViewById<CheckBox>(R.id.surfBreakOption4)

        // Image
        imageView = findViewById(R.id.imageView)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else Manifest.permission.READ_EXTERNAL_STORAGE

            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
            } else {
                openGallery()
            }
        }

        // Ajout du spot
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val name = editName.text.toString()
            val location = editLocation.text.toString()
            val difficulty = editDifficulty.text.toString().toIntOrNull() ?: 1
            val seasonStart = editSeasonStart.text.toString()
            val seasonEnd = editSeasonEnd.text.toString()

            val surfBreaks = mutableListOf<String>()
            if (cb1.isChecked) surfBreaks.add(cb1.text.toString())
            if (cb2.isChecked) surfBreaks.add(cb2.text.toString())
            if (cb3.isChecked) surfBreaks.add(cb3.text.toString())
            if (cb4.isChecked) surfBreaks.add(cb4.text.toString())

            if (selectedImageUri != null) {
                uploadToCloudinary(selectedImageUri!!) { imageUrl ->
                    if (imageUrl != null) {
                        sendSpotToAirtable(name, location, surfBreaks, difficulty, seasonStart, seasonEnd, imageUrl)
                    } else {
                        Toast.makeText(this, "Erreur d'upload de l'image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                sendSpotToAirtable(name, location, surfBreaks, difficulty, seasonStart, seasonEnd, null)
            }
        }

        // Retour
        val buttonRetourAccueil = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetourAccueil.setOnClickListener { finish() }
    }

    private fun openGallery() {
        Log.d("AjoutSpot", "openGallery called")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun uploadToCloudinary(imageUri: Uri, onResult: (String?) -> Unit) {
        val inputStream = contentResolver.openInputStream(imageUri) ?: return onResult(null)
        val imageBytes = inputStream.readBytes()
        inputStream.close()

        val url = "https://api.cloudinary.com/v1_1/dsrlf52bb/image/upload"
        val request = object : VolleyMultipartRequest(Request.Method.POST, url,
            Response.Listener { response ->
                val json = JSONObject(String(response.data))
                val imageUrl = json.getString("secure_url")
                onResult(imageUrl)
            },
            Response.ErrorListener {
                Log.e("Cloudinary", "Erreur : $it")
                onResult(null)
            }
        ) {
            override fun getByteData(): Map<String, DataPart> {
                val data = HashMap<String, DataPart>()
                data["file"] = DataPart("image.jpg", imageBytes)
                return data
            }

            override fun getParams(): Map<String, String> {
                return mapOf("upload_preset" to "unsigned_preset")
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun sendSpotToAirtable(
        name: String,
        location: String,
        surfBreaks: List<String>,
        difficulty: Int,
        seasonStart: String,
        seasonEnd: String,
        imageUrl: String?
    ) {
        val fields = JSONObject().apply {
            put("Destination", name)
            put("Destination State/Country", location)
            put("Surf Break", JSONArray(surfBreaks))
            put("Difficulty Level", difficulty)
            put("Peak Surf Season Begins", seasonStart)
            put("Peak Surf Season Ends", seasonEnd)

            if (imageUrl != null) {
                val photoArray = JSONArray()
                photoArray.put(JSONObject().put("url", imageUrl))
                put("Photos", photoArray)
            }
        }

        val body = JSONObject().put("fields", fields)
        val url = "https://api.airtable.com/v0/appjGkyY19YTjz5DF/Surf%20Destinations"

        val request = object : JsonObjectRequest(Request.Method.POST, url, body,
            { response ->
                Toast.makeText(this, "Spot ajouté avec succès !", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                val err = error.networkResponse?.data?.let { String(it) }
                Log.e("POST", "Erreur Airtable : $err")
                Toast.makeText(this, "Erreur Airtable", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Authorization" to "Bearer patl1Jtlrfu0kyTgA.35ff8d849025a763a04a5121e7b50d5ecb08245375b77186b3ba5fcfd1b02f05",
                    "Content-Type" to "application/json"
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    // 🔍 Fonction pour forcer l'indexation des images dans /sdcard/Download
    private fun scanDownloadImages() {
        val downloadDir = File("/sdcard/Download")
        if (downloadDir.exists() && downloadDir.isDirectory) {
            val imageFiles = downloadDir.listFiles { file ->
                file.extension.lowercase() in listOf("jpg", "jpeg", "png", "webp")
            } ?: return

            for (file in imageFiles) {
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(file.absolutePath),
                    null
                ) { _, uri -> Log.d("SCAN", "Image indexée : $uri") }
            }
        } else {
            Log.e("SCAN", "Le dossier /sdcard/Download n'existe pas")
        }
    }
}
