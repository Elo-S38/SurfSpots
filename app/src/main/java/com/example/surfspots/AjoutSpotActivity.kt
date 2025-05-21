//  Déclaration du package
package com.example.surfspotsxml

//  Imports nécessaires
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
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AjoutSpotActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView // 🖼 Image sélectionnée
    private var selectedImageUri: Uri? = null //  URI de l’image locale

    //  Lance la sélection d'image
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            imageView.setImageURI(selectedImageUri) // Affiche l’image sélectionnée
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_spot)

        //  Champs de formulaire
        val editName = findViewById<EditText>(R.id.nameEditText)
        val editLocation = findViewById<EditText>(R.id.locationEditText)
        val editDifficulty = findViewById<EditText>(R.id.difficultyEditText)
        val editSeasonStart = findViewById<EditText>(R.id.seasonStartEditText)
        val editSeasonEnd = findViewById<EditText>(R.id.seasonEndEditText)
        val urlEditText = findViewById<EditText>(R.id.imageUrlEditText)

        //  Checkboxes pour le type de vague
        val cb1 = findViewById<CheckBox>(R.id.surfBreakOption1)
        val cb2 = findViewById<CheckBox>(R.id.surfBreakOption2)
        val cb3 = findViewById<CheckBox>(R.id.surfBreakOption3)
        val cb4 = findViewById<CheckBox>(R.id.surfBreakOption4)

        // 🖼 Bouton + image
        imageView = findViewById(R.id.imageView)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)

        //  Désactive le bouton si une URL est tapée
        urlEditText.setOnFocusChangeListener { _, _ ->
            selectImageButton.isEnabled = urlEditText.text.isBlank()
        }

        //  Bouton pour ouvrir la galerie
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

        //  Bouton d'ajout du spot
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            //  Récupération des valeurs saisies
            val name = editName.text.toString()
            val location = editLocation.text.toString()
            val difficulty = editDifficulty.text.toString().toIntOrNull() ?: 1
            val seasonStart = editSeasonStart.text.toString()
            val seasonEnd = editSeasonEnd.text.toString()
            val manualUrl = urlEditText.text.toString()

            //  Récupération des vagues cochées
            val surfBreaks = mutableListOf<String>()
            if (cb1.isChecked) surfBreaks.add(cb1.text.toString())
            if (cb2.isChecked) surfBreaks.add(cb2.text.toString())
            if (cb3.isChecked) surfBreaks.add(cb3.text.toString())
            if (cb4.isChecked) surfBreaks.add(cb4.text.toString())

            //  Cas 1 : URL image directe
            if (manualUrl.isNotBlank()) {
                val spot = Spot(0, name, location, manualUrl, surfBreaks.joinToString(", "), difficulty, seasonStart, seasonEnd, location, 0)
                sendSpotToGoApi(spot)
            }
            //  Cas 2 : image locale → envoi vers Cloudinary
            else if (selectedImageUri != null) {
                uploadToCloudinary(selectedImageUri!!) { imageUrl ->
                    val finalUrl = imageUrl ?: ""
                    val spot = Spot(0, name, location, finalUrl, surfBreaks.joinToString(", "), difficulty, seasonStart, seasonEnd, location, 0)
                    sendSpotToGoApi(spot)
                }
            }
            //  Cas 3 : aucune image
            else {
                val spot = Spot(0, name, location, "", surfBreaks.joinToString(", "), difficulty, seasonStart, seasonEnd, location, 0)
                sendSpotToGoApi(spot)
            }
        }

        //  Retour à l'accueil
        val buttonRetourAccueil = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetourAccueil.setOnClickListener { finish() }

        // Choix de la saison avec DatePicker
        editSeasonStart.setOnClickListener { showDatePicker { editSeasonStart.setText(formatDate(it)) } }
        editSeasonEnd.setOnClickListener { showDatePicker { editSeasonEnd.setText(formatDate(it)) } }
    }

    //  Ouvre la galerie Android
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    // 🗓 Format de la date
    private fun formatDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    //  Affiche le calendrier Android
    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d -> calendar.set(y, m, d); onDateSelected(calendar.time) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    //  Upload vers Cloudinary
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

    //  Envoie les données du spot à l’API Go en POST
    private fun sendSpotToGoApi(spot: Spot) {
        val jsonBody = JSONObject().apply {
            put("name", spot.name)
            put("surfBreak", spot.surfBreak)
            put("photo", spot.imageUrlOrPath)
            put("address", spot.address)
            put("difficulty", spot.difficulty)
            put("seasonStart", spot.seasonStart)
            put("seasonEnd", spot.seasonEnd)
            put("rating", spot.rating)
        }

        val url = "http://192.168.75.45:8080/api/spots"



        val request = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            {
                Toast.makeText(this@AjoutSpotActivity, "✅ Spot ajouté via Go !", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Log.e("POST", "Erreur Go API : ${error.message}")
                Toast.makeText(this@AjoutSpotActivity, "❌ Erreur Go API", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf("Content-Type" to "application/json")
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
