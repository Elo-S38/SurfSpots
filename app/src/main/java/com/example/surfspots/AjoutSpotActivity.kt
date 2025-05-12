package com.example.surfspotsxml

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
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
import com.example.surfspots.Spot
import org.json.JSONArray
import org.json.JSONObject
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

        val editName = findViewById<EditText>(R.id.nameEditText)
        val editLocation = findViewById<EditText>(R.id.locationEditText)
        val editDifficulty = findViewById<EditText>(R.id.difficultyEditText)
        val editSeasonStart = findViewById<EditText>(R.id.seasonStartEditText)
        val editSeasonEnd = findViewById<EditText>(R.id.seasonEndEditText)
        val urlEditText = findViewById<EditText>(R.id.imageUrlEditText)

        val cb1 = findViewById<CheckBox>(R.id.surfBreakOption1)
        val cb2 = findViewById<CheckBox>(R.id.surfBreakOption2)
        val cb3 = findViewById<CheckBox>(R.id.surfBreakOption3)
        val cb4 = findViewById<CheckBox>(R.id.surfBreakOption4)

        imageView = findViewById(R.id.imageView)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)

        // 🔄 Désactiver le bouton galerie si une URL est tapée
        urlEditText.setOnFocusChangeListener { _, _ ->
            selectImageButton.isEnabled = urlEditText.text.isBlank()
        }

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

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val name = editName.text.toString()
            val location = editLocation.text.toString()
            val difficulty = editDifficulty.text.toString().toIntOrNull() ?: 1
            val seasonStart = editSeasonStart.text.toString()
            val seasonEnd = editSeasonEnd.text.toString()
            val manualUrl = urlEditText.text.toString()

            val surfBreaks = mutableListOf<String>()
            if (cb1.isChecked) surfBreaks.add(cb1.text.toString())
            if (cb2.isChecked) surfBreaks.add(cb2.text.toString())
            if (cb3.isChecked) surfBreaks.add(cb3.text.toString())
            if (cb4.isChecked) surfBreaks.add(cb4.text.toString())

            // 🟢 1. URL manuelle prioritaire
            if (manualUrl.isNotBlank()) {
                val spot = Spot(
                    name = name,
                    location = location,
                    imageUrlOrPath = manualUrl,
                    surfBreak = surfBreaks.joinToString(", "),
                    difficulty = difficulty,
                    seasonStart = seasonStart,
                    seasonEnd = seasonEnd,
                    address = location
                )
                sendSpotToAirtable(spot)
            }
            // 🟡 2. Galerie si pas d’URL
            else if (selectedImageUri != null) {
                uploadToCloudinary(selectedImageUri!!) { imageUrl ->
                    if (imageUrl != null) {
                        val spot = Spot(
                            name = name,
                            location = location,
                            imageUrlOrPath = imageUrl,
                            surfBreak = surfBreaks.joinToString(", "),
                            difficulty = difficulty,
                            seasonStart = seasonStart,
                            seasonEnd = seasonEnd,
                            address = location
                        )
                        sendSpotToAirtable(spot)
                    } else {
                        Toast.makeText(this, "Erreur d'upload de l'image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            // 🔴 Aucun visuel
            else {
                Toast.makeText(this, "Ajoutez une image : galerie ou URL", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonRetourAccueil = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetourAccueil.setOnClickListener { finish() }

        editSeasonStart.setOnClickListener { showDatePicker { editSeasonStart.setText(formatDate(it)) } }
        editSeasonEnd.setOnClickListener { showDatePicker { editSeasonEnd.setText(formatDate(it)) } }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d -> calendar.set(y, m, d); onDateSelected(calendar.time) },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
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
                return mapOf("file" to DataPart("image.jpg", imageBytes))
            }

            override fun getParams(): Map<String, String> {
                return mapOf("upload_preset" to "unsigned_preset")
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun sendSpotToAirtable(spot: Spot) {
        val fields = JSONObject().apply {
            put("Destination", spot.name)
            put("Destination State/Country", spot.location)
            put("Surf Break", JSONArray(spot.surfBreak.split(", ")))
            put("Difficulty Level", spot.difficulty)
            put("Peak Surf Season Begins", spot.seasonStart)
            put("Peak Surf Season Ends", spot.seasonEnd)

            val photoArray = JSONArray()
            photoArray.put(JSONObject().put("url", spot.imageUrlOrPath))
            put("Photos", photoArray)
        }

        val body = JSONObject().put("fields", fields)
        val url = "https://api.airtable.com/v0/appjGkyY19YTjz5DF/Surf%20Destinations"

        val request = object : JsonObjectRequest(Request.Method.POST, url, body,
            { Toast.makeText(this, "Spot ajouté avec succès !", Toast.LENGTH_SHORT).show(); finish() },
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
}
