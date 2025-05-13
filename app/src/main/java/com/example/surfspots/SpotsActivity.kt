package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import com.example.surfspots.Spot
import com.example.surfspots.SpotAdapter

class SpotsActivity : AppCompatActivity() {

    // 🟡 Liste qui contiendra les spots reçus de l'API
    private val spots = mutableListOf<Spot>()

    private lateinit var listView: ListView
    private lateinit var adapter: SpotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots)

        // 🔙 Bouton de retour
        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener { finish() }

        // 🔃 Initialisation de la liste
        listView = findViewById(R.id.listView)
        adapter = SpotAdapter(this, spots)
        listView.adapter = adapter

        // 📲 Ouvre les détails du spot au clic
        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, SpotDetailActivity::class.java)
            intent.putExtra("spot", spots[position])
            startActivity(intent)
        }

        // 🌐 Appelle l’API Go pour charger les spots
        fetchSpotsFromApi()
    }

    private fun fetchSpotsFromApi() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:8080/api/spots" // ✅ URL de ton backend Go local

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                Log.d("Volley", "Spots reçus : ${response.length()}")
                spots.clear() // Vide la liste avant d’ajouter les nouveaux spots

                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)

                    // ✅ Récupération des 4 champs JSON envoyés par l’API Go
                    val name = item.optString("name", "Inconnu")
                    val surfBreak = item.optString("surfBreak", "Inconnu")
                    val photo = item.optString("photo", "")
                    val address = item.optString("address", "Adresse inconnue")

                    // 🔁 Création de l’objet Spot (adapté à la structure Kotlin)
                    val spot = Spot(
                        name = name,
                        location = address,
                        imageUrlOrPath = photo,
                        surfBreak = surfBreak,
                        difficulty = 0,
                        seasonStart = "N/A",
                        seasonEnd = "N/A",
                        address = address
                    )

                    spots.add(spot)
                }

                adapter.notifyDataSetChanged() // ✅ Rafraîchit la ListView
            },
            { error ->
                Log.e("Volley", "Erreur réseau : ${error.message}")
            }
        )

        queue.add(request)
    }
}
