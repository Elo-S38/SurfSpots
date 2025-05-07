package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import com.example.surfspots.Spot
import com.example.surfspots.SpotAdapter
import org.json.JSONArray

class SpotsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots)

        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener { finish() }

        val listView: ListView = findViewById(R.id.listView)
        val spots = mutableListOf<Spot>()
        val queue = Volley.newRequestQueue(this)

        val url = "https://api.airtable.com/v0/appjGkyY19YTjz5DF/Surf%20Destinations"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val spotsArray: JSONArray = response.getJSONArray("records")
                Log.d("Volley", "Nombre de spots récupérés : ${spotsArray.length()}")

                for (i in 0 until spotsArray.length()) {
                    val item = spotsArray.getJSONObject(i).getJSONObject("fields")

                    val name = item.optString("Destination", "Inconnu")
                    val location = item.optString("Destination State/Country", "Inconnu")
                    val surfBreak = item.optJSONArray("Surf Break")?.optString(0) ?: "Non spécifié"
                    val difficulty = item.optInt("Difficulty Level", 0)
                    val seasonStart = item.optString("Peak Surf Season Begins", "N/A")
                    val seasonEnd = item.optString("Peak Surf Season Ends", "N/A")
                    val address = item.optString("Address", "N/A")

                    // 🔍 Image depuis Cloudinary (nouveaux spots)
                    val photoArray = item.optJSONArray("Photos")
                    val imageUrl = if (photoArray != null && photoArray.length() > 0) {
                        photoArray.getJSONObject(0).optString("url", null)
                    } else null

                    // 🖼️ Image locale pour anciens spots
                    val imageName = name.lowercase().replace(" ", "_").replace("-", "_")
                    val imageResId = if (imageUrl == null) {
                        val resId = resources.getIdentifier(imageName, "drawable", packageName)
                        if (resId != 0) resId else R.drawable.placeholder
                    } else {
                        R.drawable.placeholder // utilisé seulement comme placeholder dans Glide
                    }

                    val spot = Spot(
                        name = name,
                        location = location,
                        imageResId = imageResId,
                        imageUrl = imageUrl,
                        surfBreak = surfBreak,
                        difficulty = difficulty,
                        seasonStart = seasonStart,
                        seasonEnd = seasonEnd,
                        address = address
                    )

                    spots.add(spot)
                }

                val adapter = SpotAdapter(this, spots)
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val intent = Intent(this, SpotDetailActivity::class.java)
                    intent.putExtra("spot", spots[position])
                    startActivity(intent)
                }
            },
            { error ->
                Log.e("Volley", "Erreur : $error")
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Authorization" to "Bearer patWOrIcEukfsAxwi.56bcfa626a3ad0e0e6a4e8290d4f6f44c8a2e259e91bb93121a8c0197494b980"
                )
            }
        }

        queue.add(jsonObjectRequest)
    }
}
