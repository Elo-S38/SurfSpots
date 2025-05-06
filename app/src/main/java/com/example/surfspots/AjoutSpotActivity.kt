package com.example.surfspotsxml

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import org.json.JSONObject
import java.io.InputStream

class AjoutSpotActivity : AppCompatActivity() {

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_spot)

        // Récupérer les éléments de l'UI
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val locationEditText = findViewById<EditText>(R.id.locationEditText)
        val surfBreakEditText = findViewById<EditText>(R.id.surfBreakEditText)
        val difficultyEditText = findViewById<EditText>(R.id.difficultyEditText)
        val seasonStartEditText = findViewById<EditText>(R.id.seasonStartEditText)
        val seasonEndEditText = findViewById<EditText>(R.id.seasonEndEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val addButton = findViewById<Button>(R.id.addButton)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)  // Bouton pour choisir une image
        val imageView = findViewById<ImageView>(R.id.imageView)  // ImageView pour afficher l'image sélectionnée

        // Ouvrir la galerie pour sélectionner une image
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Créer une instance de la queue Volley
        val queue = Volley.newRequestQueue(this)

        // Logique de bouton pour ajouter un spot
        addButton.setOnClickListener {
            // Récupérer les valeurs saisies
            val name = nameEditText.text.toString()
            val location = locationEditText.text.toString()
            val surfBreak = surfBreakEditText.text.toString()
            val difficulty = difficultyEditText.text.toString()
            val seasonStart = seasonStartEditText.text.toString()
            val seasonEnd = seasonEndEditText.text.toString()
            val address = addressEditText.text.toString()

            // Validation de base
            if (name.isEmpty() || location.isEmpty() || surfBreak.isEmpty() || difficulty.isEmpty() || seasonStart.isEmpty() || seasonEnd.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show()
            } else {
                // Créer un objet Spot avec les données saisies
                val newSpot = JSONObject()
                newSpot.put("name", name)
                newSpot.put("location", location)
                newSpot.put("surfBreak", surfBreak)
                newSpot.put("difficulty", difficulty.toInt())
                newSpot.put("seasonStart", seasonStart)
                newSpot.put("seasonEnd", seasonEnd)
                newSpot.put("address", address)

                // Si une image a été choisie, l'ajouter
                imageUri?.let {
                    // Ici, nous n'envoyons pas l'image en Base64. À la place, nous enverrons l'URI ou une autre méthode si nécessaire.
                    newSpot.put("imageUri", it.toString())  // Envoyer l'URI sous forme de chaîne
                }

                // L'URL de l'API à laquelle on veut envoyer la requête POST
                val url = getString(R.string.api_url)

                // Créer une requête POST avec Volley
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, newSpot,
                    Response.Listener { response ->
                        // Lorsque la requête réussit, afficher une notification
                        Toast.makeText(this, "Spot ajouté : $name à $location", Toast.LENGTH_SHORT).show()

                        // Réinitialiser les champs
                        nameEditText.text.clear()
                        locationEditText.text.clear()
                        surfBreakEditText.text.clear()
                        difficultyEditText.text.clear()
                        seasonStartEditText.text.clear()
                        seasonEndEditText.text.clear()
                        addressEditText.text.clear()
                        imageView.setImageResource(R.drawable.placeholder)  // Réinitialiser l'image
                    },
                    Response.ErrorListener { error ->
                        // Gérer l'erreur si la requête échoue
                        Toast.makeText(this, "Erreur d'ajout : ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )

                // Ajouter la requête à la queue Volley
                queue.add(jsonObjectRequest)
            }
        }
    }

    // Gérer la réponse de la galerie
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    imageUri = data.data
                    imageUri?.let {
                        val imageView = findViewById<ImageView>(R.id.imageView)
                        imageView.setImageURI(it)  // Afficher l'image sélectionnée
                    }
                }
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
