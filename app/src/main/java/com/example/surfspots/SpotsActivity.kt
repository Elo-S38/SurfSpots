package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfspots.R
import com.example.surfspots.Spot

import com.example.surfspots.lireJsonDepuisRaw
import org.json.JSONArray

class SpotsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots)

        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener { finish() }

        val listView: ListView = findViewById(R.id.listView)

        val spots = mutableListOf<Spot>()

        // 📥 On charge le JSON depuis res/raw/spots.json
        val json = lireJsonDepuisRaw(this, R.raw.spots)
        val spotsArray: JSONArray = json?.getJSONArray("spots") ?: JSONArray()

        for (i in 0 until spotsArray.length()) {
            val item = spotsArray.getJSONObject(i)
            val name = item.getString("name")
            val location = item.getString("location")
            val surfBreak = item.getString("surfBreak")

            // 🖼️ On récupère l’image par son nom dans drawable
            val imageResId = resources.getIdentifier(item.getString("image"), "drawable", packageName)

            val spot = Spot(name, location, imageResId, surfBreak)
            spots.add(spot)
        }

        val adapter = SpotAdapter(this, spots)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val spot = spots[position]
            val intent = Intent(this, SpotDetailActivity::class.java)
            intent.putExtra("spot", spot)
            startActivity(intent)
        }
    }
}
