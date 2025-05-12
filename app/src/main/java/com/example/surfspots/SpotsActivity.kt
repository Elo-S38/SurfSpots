package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import com.example.surfspots.Spot
import com.example.surfspots.SpotAdapter
import org.json.JSONArray

class SpotsActivity : AppCompatActivity() {

    private val spots = mutableListOf<Spot>()
    private lateinit var listView: ListView
    private lateinit var adapter: SpotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots)

        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener { finish() }

        listView = findViewById(R.id.listView)
        adapter = SpotAdapter(this, spots)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, SpotDetailActivity::class.java)
            intent.putExtra("spot", spots[position])
            startActivity(intent)
        }

        // 👉 Lance AjoutSpotActivity et récupère le résultat
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val newSpot = result.data?.getParcelableExtra<Spot>("newSpot")
                if (newSpot != null) {
                    spots.add(newSpot)
                    adapter.notifyDataSetChanged()
                }
            }
        }



        // 🔁 Récupération des spots distants
        fetchSpotsFromAirtable()
    }

    private fun fetchSpotsFromAirtable() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.airtable.com/v0/appjGkyY19YTjz5DF/Surf%20Destinations"

        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val records = response.getJSONArray("records")
                for (i in 0 until records.length()) {
                    val fields = records.getJSONObject(i).getJSONObject("fields")

                    val name = fields.optString("Destination", "Inconnu")
                    val location = fields.optString("Destination State/Country", "Inconnu")
                    val surfBreak = fields.optJSONArray("Surf Break")?.optString(0) ?: "Non spécifié"
                    val difficulty = fields.optInt("Difficulty Level", 0)
                    val seasonStart = fields.optString("Peak Surf Season Begins", "N/A")
                    val seasonEnd = fields.optString("Peak Surf Season Ends", "N/A")
                    val address = fields.optString("Address", location)

                    val photoArray = fields.optJSONArray("Photos")
                    val imageUrlOrPath = if (photoArray != null && photoArray.length() > 0) {
                        photoArray.getJSONObject(0).optString("url", "")
                    } else {
                        // Tentative de chargement d’image locale
                        val imageName = name.lowercase().replace(" ", "_").replace("-", "_")
                        val resId = resources.getIdentifier(imageName, "drawable", packageName)
                        if (resId != 0)
                            "android.resource://$packageName/drawable/$imageName"
                        else
                            ""
                    }

                    val spot = Spot(
                        name = name,
                        location = location,
                        imageUrlOrPath = imageUrlOrPath,
                        surfBreak = surfBreak,
                        difficulty = difficulty,
                        seasonStart = seasonStart,
                        seasonEnd = seasonEnd,
                        address = address
                    )

                    spots.add(spot)
                }

                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("Volley", "Erreur : ${error.message}")
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Authorization" to "Bearer patWOrIcEukfsAxwi.56bcfa626a3ad0e0e6a4e8290d4f6f44c8a2e259e91bb93121a8c0197494b980"
                )
            }
        }

        queue.add(request)
    }
}
