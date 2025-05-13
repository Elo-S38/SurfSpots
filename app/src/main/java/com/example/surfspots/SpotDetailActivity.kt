package com.example.surfspotsxml

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.surfspots.R
import java.io.File

class SpotDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_detail)

        val buttonRetour = findViewById<Button>(R.id.buttonRetourList)
        buttonRetour.setOnClickListener { finish() }

        val spotId = intent.getIntExtra("spot_id", -1)

        if (spotId != -1) {
            fetchSpotDetails(spotId)
        } else {
            Toast.makeText(this, "Spot non trouvé", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchSpotDetails(id: Int) {
        val url = "http://10.0.2.2:8080/api/spots/$id"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val name = response.optString("name", "Inconnu")
                val location = response.optString("address", "Inconnu")
                val surfBreak = response.optString("surfBreak", "N/A")
                val difficulty = response.optInt("difficulty", 0)
                val seasonStart = response.optString("seasonStart", "N/A")
                val seasonEnd = response.optString("seasonEnd", "N/A")
                val imageUrlOrPath = response.optString("photo", "")

                // Affichage dans les vues
                val imageView = findViewById<ImageView>(R.id.detailImage)
                val nameView = findViewById<TextView>(R.id.detailName)
                val locationView = findViewById<TextView>(R.id.detailLocation)
                val surfBreakView = findViewById<TextView>(R.id.detailSurfBreak)
                val difficultyView = findViewById<TextView>(R.id.detailDifficulty)
                val seasonView = findViewById<TextView>(R.id.detailSeason)
                val addressView = findViewById<TextView>(R.id.detailAddress)

                Log.d("DEBUG_IMAGE", "DetailActivity: $imageUrlOrPath")
                Log.d("DEBUG_IMAGE", "Exists = ${File(imageUrlOrPath).exists()}")

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

                nameView.text = name
                locationView.text = location
                surfBreakView.text = surfBreak
                difficultyView.text = "$difficulty/5"
                seasonView.text = "$seasonStart → $seasonEnd"
                addressView.text = location
            },
            { error ->
                Log.e("Volley", "Erreur API : ${error.message}")
                Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        queue.add(request)
    }
}
