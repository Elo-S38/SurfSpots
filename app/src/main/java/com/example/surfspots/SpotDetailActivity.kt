//  Package de l’application
package com.example.surfspotsxml

// 📚Imports nécessaires
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest // Pour requêtes PUT simples sans réponse JSON
import com.android.volley.toolbox.JsonObjectRequest // Pour GET avec réponse JSON
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide // Pour afficher les images
import com.example.surfspots.R
import org.json.JSONObject
import java.io.File
import com.google.android.material.snackbar.Snackbar // Pour feedback utilisateur visuel



class SpotDetailActivity : AppCompatActivity() {

    //  Variable pour stocker l’ID du spot reçu depuis l’intent
    private var spotId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_detail) // 🔗 Associe l'activité à son fichier XML

        // Bouton de retour à l’activité précédente
        val buttonRetour = findViewById<Button>(R.id.buttonRetourList)
        buttonRetour.setOnClickListener { finish() }

        //  Récupération de l’ID du spot depuis l’intent
        spotId = intent.getIntExtra("spot_id", -1)

        // Si l’ID est valide, on charge les détails du spot
        if (spotId != -1) {
            fetchSpotDetails(spotId)
        } else {
            Toast.makeText(this, "Spot non trouvé", Toast.LENGTH_SHORT).show()
            finish()
        }

        //  Champs pour saisir la note et bouton pour envoyer
        val editRating = findViewById<EditText>(R.id.editRating)
        val buttonEnvoyer = findViewById<Button>(R.id.buttonEnvoyerNote)

        //  Lorsqu'on clique sur le bouton, on envoie la note au backend
        buttonEnvoyer.setOnClickListener {
            // On récupère la note saisie
            val note = editRating.text.toString().toIntOrNull()

            //  Si la note est invalide (pas un nombre ou pas entre 0 et 5), on stoppe
            if (note == null || note !in 0..5) {
                Toast.makeText(this, "Note invalide (0 à 5)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://192.168.75.45:8080/api/spots/$spotId"

            val jsonBody = JSONObject().put("rating", note) //  Préparation du corps JSON

            Log.d("RATING_PUT", "Envoi JSON : $jsonBody") //  Log debug

            //  Requête PUT avec StringRequest (car aucune réponse JSON attendue)
            val request = object : StringRequest(
                Method.PUT,
                url,
                {
                    //  Affiche un message de confirmation si la note est bien enregistrée
                    Snackbar.make(findViewById(android.R.id.content), "Note enregistrée !", Snackbar.LENGTH_LONG).show()
                },
                { error ->
                    //  Affiche un message en cas d’erreur
                    Log.e("RATING_PUT", "Erreur API : ${error.message}")
                    Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show()
                }
            ) {
                //  Spécifie que le corps de la requête est du JSON
                override fun getBodyContentType(): String = "application/json"

                //  Convertit le JSON en tableau de bytes pour l’envoi
                override fun getBody(): ByteArray = jsonBody.toString().toByteArray(Charsets.UTF_8)
            }

            //  Envoie la requête via Volley
            Volley.newRequestQueue(this).add(request)
        }
    }

    //  Fonction qui récupère les détails d’un spot depuis l’API
    private fun fetchSpotDetails(id: Int) {
        val url = "http://192.168.75.45:8080/api/spots/$spotId"

        val queue = Volley.newRequestQueue(this) // 📡 File d’attente Volley

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null, // Pas de corps pour une requête GET
            { response ->
                //  On lit les champs JSON reçus
                val name = response.optString("name", "Inconnu")
                val location = response.optString("address", "Inconnu")
                val surfBreak = response.optString("surfBreak", "N/A")
                val difficulty = response.optInt("difficulty", 0)
                val seasonStart = response.optString("seasonStart", "N/A")
                val seasonEnd = response.optString("seasonEnd", "N/A")
                val rating = response.optInt("rating", 0)
                val imageUrlOrPath = response.optString("photo", "")

                //  Références aux vues dans le layout
                val imageView = findViewById<ImageView>(R.id.detailImage)
                val nameView = findViewById<TextView>(R.id.detailName)
                val locationView = findViewById<TextView>(R.id.detailLocation)
                val surfBreakView = findViewById<TextView>(R.id.detailSurfBreak)
                val difficultyView = findViewById<TextView>(R.id.detailDifficulty)
                val seasonView = findViewById<TextView>(R.id.detailSeason)
                val addressView = findViewById<TextView>(R.id.detailAddress)
                val ratingView = findViewById<TextView>(R.id.detailRating)

                // Chargement de l’image en fonction de son type (URL, fichier local ou content URI)
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

                //  Remplit les vues texte avec les infos reçues
                nameView.text = name
                locationView.text = location
                surfBreakView.text = surfBreak
                difficultyView.text = "$difficulty/5"
                seasonView.text = "$seasonStart → $seasonEnd"
                addressView.text = location
                ratingView.text = "Note : $rating / 5"
            },
            { error ->
                //  Si l’API renvoie une erreur
                Log.e("Volley", "Erreur API : ${error.message}")
                Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        //  Exécute la requête GET
        queue.add(request)
    }
}
