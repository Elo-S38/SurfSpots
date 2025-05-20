package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import com.example.surfspots.Spot
import com.example.surfspots.SpotAdapter

class SpotsActivity : AppCompatActivity() {

    private val spots = mutableListOf<Spot>()
    private lateinit var listView: ListView
    private lateinit var adapter: SpotAdapter

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var nextPageButton: Button
    private lateinit var prevPageButton: Button

    private var currentPage = 1
    private val spotsPerPage = 5
    private var currentLocationFilter: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots)

        listView = findViewById(R.id.listView)
        adapter = SpotAdapter(this, spots)
        listView.adapter = adapter

        searchEditText = findViewById(R.id.editSearchLocation)
        searchButton = findViewById(R.id.buttonSearch)
        nextPageButton = findViewById(R.id.buttonNextPage)
        prevPageButton = findViewById(R.id.buttonPrevPage)

        findViewById<Button>(R.id.buttonRetourAccueil).setOnClickListener {
            finish()
        }

        // 🔍 Lancer une recherche par lieu
        searchButton.setOnClickListener {
            currentLocationFilter = searchEditText.text.toString()
            currentPage = 1
            fetchSpotsFromApi()
        }

        // ⏩ Page suivante
        nextPageButton.setOnClickListener {
            currentPage++
            fetchSpotsFromApi()
        }

        // ⏪ Page précédente
        prevPageButton.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                fetchSpotsFromApi()
            }
        }

        // 📲 Aller aux détails d’un spot
        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, SpotDetailActivity::class.java)
            intent.putExtra("spot_id", spots[position].id)
            startActivity(intent)
        }

        fetchSpotsFromApi()
    }

    private fun fetchSpotsFromApi() {
        val queue = Volley.newRequestQueue(this)

        val baseUrl = "http://192.168.75.45:8080/api/spots"
        val locationParam = if (currentLocationFilter.isNotBlank()) "&location=${currentLocationFilter}" else ""
        val url = "$baseUrl?page=$currentPage&limit=$spotsPerPage$locationParam"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val dataArray = response.getJSONArray("data")
                spots.clear()

                for (i in 0 until dataArray.length()) {
                    val item = dataArray.getJSONObject(i)
                    val spot = Spot(
                        id = item.getInt("id"),
                        name = item.optString("name", "Inconnu"),
                        location = item.optString("address", "Adresse inconnue"),
                        imageUrlOrPath = item.optString("photo", ""),
                        surfBreak = item.optString("surfBreak", "N/A"),
                        difficulty = item.optInt("difficulty", 0),
                        seasonStart = item.optString("seasonStart", ""),
                        seasonEnd = item.optString("seasonEnd", ""),
                        address = item.optString("address", ""),
                        rating = item.optInt("rating", 0)
                    )
                    spots.add(spot)
                }

                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("Volley", "Erreur réseau : ${error.message}")
                Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(request)
    }
}
