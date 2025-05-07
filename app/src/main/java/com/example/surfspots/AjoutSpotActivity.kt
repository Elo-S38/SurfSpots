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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AjoutSpotActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var photoUrlEditText: EditText  // à créer dans le layout si utilisé en POST
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

        // Champs de saisie
        val editName = findViewById<EditText>(R.id.nameEditText)
        val editLocation = findViewById<EditText>(R.id.locationEditText)
        val editDifficulty = findViewById<EditText>(R.id.difficultyEditText)
        val editSeasonStart = findViewById<EditText>(R.id.seasonStartEditText)
        val editSeasonEnd = findViewById<EditText>(R.id.seasonEndEditText)

        // DatePicker
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        editSeasonStart.setOnClickListener {
            showDatePicker { date ->
                editSeasonStart.setText(dateFormat.format(date))
            }
        }

        editSeasonEnd.setOnClickListener {
            showDatePicker { date ->
                editSeasonEnd.setText(dateFormat.format(date))
            }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            } else {
                openGallery()
            }
        }

        // Ajout du spot (POST)
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

            // Construction de l'objet JSON
            val fields = JSONObject().apply {
                put("Destination", name)
                put("Destination State/Country", location)
                put("Surf Break", JSONArray(surfBreaks))
                put("Difficulty Level", difficulty)
                put("Peak Surf Season Begins", seasonStart)
                put("Peak Surf Season Ends", seasonEnd)
            }

            val body = JSONObject().put("fields", fields)
            val url = "https://api.airtable.com/v0/appjGkyY19YTjz5DF/Surf%20Destinations"
            val queue = Volley.newRequestQueue(this)

            val request = object : JsonObjectRequest(Request.Method.POST, url, body,
                { response ->
                    Toast.makeText(this, "Spot ajouté !", Toast.LENGTH_SHORT).show()
                    finish()
                },
                { error ->
                    Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                    val errorMsg = error.networkResponse?.data?.let { String(it, Charsets.UTF_8) }
                    Log.e("POST", "Erreur détaillée : $errorMsg")
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer patl1Jtlrfu0kyTgA.35ff8d849025a763a04a5121e7b50d5ecb08245375b77186b3ba5fcfd1b02f05"
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            queue.add(request)
        }

        // Retour
        val buttonRetourAccueil = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetourAccueil.setOnClickListener { finish() }
    }

    private fun openGallery() {
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
}
