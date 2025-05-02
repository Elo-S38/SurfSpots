package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfspots.R
import com.example.surfspots.Spot
import com.example.surfspots.lireJsonDepuisRaw
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

        // 📥 Lecture du JSON
        val json = lireJsonDepuisRaw(this, R.raw.spots)
        val spotsArray: JSONArray = json?.getJSONArray("records") ?: JSONArray()

        for (i in 0 until spotsArray.length()) {
            val item = spotsArray.getJSONObject(i).getJSONObject("fields")
            val name = item.getString("Destination")
            val location = item.getString("Destination State/Country")
            val surfBreak = item.getJSONArray("Surf Break").getString(0)

            // 🖼️ Nom de l'image sans extension
            val imageName = name
                .lowercase()
                .replace(" ", "_")
                .replace("-", "_")

            val imageResId = resources.getIdentifier(imageName, "drawable", packageName)

            // 🔁 Si l'image est introuvable, utilise une image par défaut
            val finalImageResId = if (imageResId != 0) imageResId else R.drawable.placeholder

            val spot = Spot(name, location, finalImageResId, surfBreak)
            spots.add(spot)
        }

        val adapter = SpotAdapter(this, spots)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, SpotDetailActivity::class.java)
            intent.putExtra("spot", spots[position])
            startActivity(intent)
        }
    }
}
