package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.surfspots.R
import com.example.surfspots.Spot


class SpotsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots)

       // Toast.makeText(this, "Page des spots affichée", Toast.LENGTH_SHORT).show()

        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener {
            finish() // Retour à l'accueil
        }

        val listView: ListView = findViewById(R.id.listView)

        val spots = listOf(
            Spot("Plage des Catalans", "Marseille", R.drawable.plage_catalans, "Beach Break"),
            Spot("Dune du Pilat", "Arcachon", R.drawable.dune_pilat, "Sand Break"),
            Spot("Mont Saint-Michel", "Normandie", R.drawable.mont_saint_michel, "Tidal Break")
        )

        val adapter = SpotAdapter(this, spots)
        listView.adapter = adapter

        // ✅ Clic sur un spot → passe l'objet Spot (Parcelable) à l'activité de détail
        listView.setOnItemClickListener { _, _, position, _ ->
            val spot = spots[position]
            val intent = Intent(this, SpotDetailActivity::class.java)
            intent.putExtra("spot", spot)
            startActivity(intent)
        }
    }
}
