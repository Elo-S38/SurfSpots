package com.example.surfspotsxml

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

        Toast.makeText(this, "Page des spots affichée", Toast.LENGTH_SHORT).show()

        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener {
            finish() // ferme l'activité et revient à MainActivity
        }

        // Gestion de la liste des spots
        val listView: ListView = findViewById(R.id.listView)

        val spots = listOf(
            Spot("Plage des Catalans", "Marseille", R.drawable.plage_catalans),
            Spot("Dune du Pilat", "Arcachon", R.drawable.dune_pilat),
            Spot("Mont Saint-Michel", "Normandie", R.drawable.mont_saint_michel)
        )

        val adapter = SpotAdapter(this, spots)
        listView.adapter = adapter
    }
}


