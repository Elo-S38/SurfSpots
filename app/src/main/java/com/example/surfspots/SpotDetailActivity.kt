package com.example.surfspotsxml

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.surfspots.R
import org.json.JSONObject
import java.io.File
import com.google.android.material.snackbar.Snackbar

class SpotDetailActivity : AppCompatActivity() {

    private var spotId: Int = -1 // ID du spot à afficher et noter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_detail) // Associe l'activité à son layout XML

        val buttonRetour = findViewById<Button>(R.id.buttonRetourList) // 🔙 Bouton retour
        buttonRetour.setOnClickListener { finish() } // Ferme l'activité

        // 🔍 Récupère l’ID du spot envoyé depuis l'activité précédente
        spotId = intent.getIntExtra("spot_id", -1)

        // ✅ Si l'ID est valide, on charge les détails du spot
        if (spotId != -1) {
            fetchSpotDetails(spotId)
        } else {
            Toast.makeText(this, "Spot non trouvé", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 📝 Champs pour saisir la note et envoyer
        val editRating = findViewById<EditText>(R.id.editRating)
        val buttonEnvoyer = findViewById<Button>(R.id.buttonEnvoyerNote)

        // 🚀 Envoie de la note à l'API Go quand on clique
        buttonEnvoyer.setOnClickListener {
            val note = editRating.text.toString().toIntOrNull()

            // ❌ Vérifie que la note est valide
            if (note == null || note !in 0..5) {
                Toast.makeText(this, "Note invalide (0 à 5)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://10.0.2.2:8080/api/spots/$spotId" // URL vers l'API Go
            val jsonBody = JSONObject().put("rating", note)   // Corps JSON envoyé : { "rating": 4 }

            // 📦 Création de la requête PUT
            val request = JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonBody,
                {
                    // ✅ Affiche une confirmation visuelle avec Snackbar
                    Snackbar.make(findViewById(android.R.id.content), "Note enregistrée !", Snackbar.LENGTH_LONG).show()
                },
                { error ->
                    Log.e("RATING_PUT", "Erreur API : ${error.message}")
                    Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show()
                }
            )

            Volley.newRequestQueue(this).add(request) // 📡 Envoie la requête au serveur
        }
    }

    // 📲 Fonction pour récupérer les détails du spot depuis l’API Go
    private fun fetchSpotDetails(id: Int) {
        val url = "http://10.0.2.2:8080/api/spots/$id" // URL pour GET /api/spots/{id}
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // 🔄 On récupère tous les champs utiles du JSON
                val name = response.optString("name", "Inconnu")
                val location = response.optString("address", "Inconnu")
                val surfBreak = response.optString("surfBreak", "N/A")
                val difficulty = response.optInt("difficulty", 0)
                val seasonStart = response.optString("seasonStart", "N/A")
                val seasonEnd = response.optString("seasonEnd", "N/A")
                val rating = response.optInt("rating", 0)
                val imageUrlOrPath = response.optString("photo", "")

                // 📌 On associe chaque champ à sa vue
                val imageView = findViewById<ImageView>(R.id.detailImage)
                val nameView = findViewById<TextView>(R.id.detailName)
                val locationView = findViewById<TextView>(R.id.detailLocation)
                val surfBreakView = findViewById<TextView>(R.id.detailSurfBreak)
                val difficultyView = findViewById<TextView>(R.id.detailDifficulty)
                val seasonView = findViewById<TextView>(R.id.detailSeason)
                val addressView = findViewById<TextView>(R.id.detailAddress)
                val ratingView = findViewById<TextView>(R.id.detailRating)

                // 🖼️ Chargement de l’image selon sa source
                when {
                    imageUrlOrPath.startsWith("http") -> {
                        Glide.with(this).load(imageUrlOrPath)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(imageView)
                    }
                    File(imageUrlOrPath).exists() -> {
                        Glide.with(this).load(File(imageUrlOrPath))
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(imageView)
                    }
                    imageUrlOrPath.startsWith("content://") -> {
                        imageView.setImageURI(Uri.parse(imageUrlOrPath))
                    }
                    else -> {
                        imageView.setImageResource(R.drawable.placeholder)
                    }
                }

                // 🧾 Remplit les champs textes avec les données reçues
                nameView.text = name
                locationView.text = location
                surfBreakView.text = surfBreak
                difficultyView.text = "$difficulty/5"
                seasonView.text = "$seasonStart → $seasonEnd"
                addressView.text = location
                ratingView.text = "Note : $rating / 5"
            },
            { error ->
                Log.e("Volley", "Erreur API : ${error.message}")
                Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        queue.add(request) // ➕ Envoie la requête GET
    }
}
