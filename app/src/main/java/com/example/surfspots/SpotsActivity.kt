package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import com.example.surfspots.Spot
import com.example.surfspots.SpotAdapter
import org.json.JSONArray

class SpotsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 📄 On associe cette activité à son layout XML
        setContentView(R.layout.activity_spots)

        // 🔙 Bouton pour revenir à la page d'accueil
        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener { finish() }

        // 🧾 La ListView qui va afficher la liste des spots
        val listView: ListView = findViewById(R.id.listView)

        // 🧪 Liste vide à remplir avec les objets Spot
        val spots = mutableListOf<Spot>()

        // Créer une queue Volley pour gérer les requêtes réseau
        val queue = Volley.newRequestQueue(this)

        // URL de l'API Airtable
        val url = "https://api.airtable.com/v0/appjGkyY19YTjz5DF/Surf%20Destinations"

        // Créer une requête GET avec Volley
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                // Log de la réponse
                Log.d("Volley", "Réponse : $response")

                // Récupérer le tableau "records" de la réponse
                val spotsArray: JSONArray = response.getJSONArray("records")
                Log.d("Volley", "Nombre de spots récupérés : ${spotsArray.length()}")  // Log du nombre de spots récupérés

                // 🔁 Boucle sur chaque spot dans le JSON
                for (i in 0 until spotsArray.length()) {
                    val item = spotsArray.getJSONObject(i).getJSONObject("fields")

                    // On extrait les champs qu'on veut
                    val name = item.getString("Destination")
                    val location = item.getString("Destination State/Country")
                    val surfBreak = item.getJSONArray("Surf Break").getString(0)
                    val difficulty = item.optInt("Difficulty Level", 0)
                    val seasonStart = item.optString("Peak Surf Season Begins", "N/A")
                    val seasonEnd = item.optString("Peak Surf Season Ends", "N/A")
                    val address = item.optString("Address", "N/A")

                    // 🖼️ On transforme le nom en nom d’image (ex : bali_beach)
                    val imageName = name
                        .lowercase()
                        .replace(" ", "_")
                        .replace("-", "_")

                    // 🔍 On récupère l'identifiant de l'image dans drawable
                    val imageResId = resources.getIdentifier(imageName, "drawable", packageName)

                    // 🛟 Si l’image n’est pas trouvée, on utilise une image par défaut
                    val finalImageResId = if (imageResId != 0) imageResId else R.drawable.placeholder

                    // 📦 On crée un objet Spot avec les infos du JSON
                    val spot = Spot(name, location, finalImageResId, surfBreak, difficulty, seasonStart, seasonEnd, address)

                    // ➕ On ajoute le Spot à la liste
                    spots.add(spot)

                    // Log pour vérifier chaque spot ajouté
                    Log.d("Volley", "Spot ajouté : $name, $location")
                }

                // 📋 On crée un adapter pour afficher la liste dans la ListView
                val adapter = SpotAdapter(this, spots)
                listView.adapter = adapter

                // Log pour vérifier que l'adaptateur a bien été défini
                Log.d("Volley", "Adapter configuré avec ${spots.size} spots")

                // 👆 Quand on clique sur un spot, on ouvre la page de détail
                listView.setOnItemClickListener { _, _, position, _ ->
                    val intent = Intent(this, SpotDetailActivity::class.java)
                    intent.putExtra("spot", spots[position]) // On envoie le Spot cliqué
                    startActivity(intent)
                }
            },
            Response.ErrorListener { error ->
                // Gérer l'erreur
                Log.e("Volley", "Erreur : $error")
            }
        ) {
            // Ajouter les headers pour l'authentification avec ton token Bearer
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer patWOrIcEukfsAxwi.56bcfa626a3ad0e0e6a4e8290d4f6f44c8a2e259e91bb93121a8c0197494b980"
                return headers
            }
        }

        // Ajouter la requête à la queue de Volley
        queue.add(jsonObjectRequest)
    }
}
