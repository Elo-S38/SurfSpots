package com.example.surfspotsxml

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.surfspots.R
import org.json.JSONArray
import org.json.JSONObject

class AjoutSpotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_spot)

        // Initialisation des champs
        val editDestination = findViewById<EditText>(R.id.nameEditText)
        val editLocation = findViewById<EditText>(R.id.locationEditText)
        val editSurfBreak = findViewById<EditText>(R.id.surfBreakEditText)  // Changer si vous utilisez une liste de choix
        val editDifficulty = findViewById<EditText>(R.id.difficultyEditText)
        val editSeasonStart = findViewById<EditText>(R.id.seasonStartEditText)
        val editSeasonEnd = findViewById<EditText>(R.id.seasonEndEditText)
        val editMagicSeaweed = findViewById<EditText>(R.id.magicSeaweedEditText)
        val editPhotoUrl = findViewById<EditText>(R.id.photoUrlEditText)

        val buttonSubmit = findViewById<Button>(R.id.addButton)
        buttonSubmit.setOnClickListener {

            // Récupération des données utilisateur
            val destination = editDestination.text.toString()
            val location = editLocation.text.toString()
            val surfBreak = editSurfBreak.text.toString()  // Exemple : "Beach Break"
            val difficulty = editDifficulty.text.toString().toIntOrNull() ?: 1  // 1 minimum
            val seasonStart = editSeasonStart.text.toString()
            val seasonEnd = editSeasonEnd.text.toString()
            val magicSeaweed = editMagicSeaweed.text.toString()
            val photoUrl = editPhotoUrl.text.toString()

            // Construction de l'objet JSON à envoyer à Airtable
            val fields = JSONObject().apply {
                put("Destination", destination)
                put("Destination State/Country", location)
                put("Surf Break", JSONArray().put(surfBreak)) // Tableau de chaînes
                put("Difficulty Level", difficulty)
                put("Peak Surf Season Begins", seasonStart)
                put("Peak Surf Season Ends", seasonEnd)
                put("Magic Seaweed Link", magicSeaweed)
                // Photos : tableau d'objets { "url": "..." }
                if (photoUrl.isNotBlank()) {
                    val photoArray = JSONArray()
                    val photoObj = JSONObject().put("url", photoUrl)
                    photoArray.put(photoObj)
                    put("Photos", photoArray)
                }
            }

            val body = JSONObject().put("fields", fields)
//            val body = JSONObject().put("records", JSONArray().put(record))

            // Utilise l'ID de la table ou le nom (encodé) - les deux sont acceptés par Airtable
            val url = "https://api.airtable.com/v0/appjGkyY19YTjz5DF/Surf%20Destinations"

            val queue = Volley.newRequestQueue(this)

            val request = object : JsonObjectRequest(
                Request.Method.POST, url, body,
                { response ->
                    val records = response.optJSONArray("records")
                    if (records != null && records.length() > 0) {
                        val record = records.getJSONObject(0)
                        val recordId = record.getString("id")
                        val createdTime = record.getString("createdTime")
                        Toast.makeText(this, "Spot ajouté avec succès ! ID: $recordId", Toast.LENGTH_LONG).show()
                    }
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
                    headers["Authorization"] = "Bearer patWOrIcEukfsAxwi.56bcfa626a3ad0e0e6a4e8290d4f6f44c8a2e259e91bb93121a8c0197494b980"
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            queue.add(request)
        }
    }
}
