package com.example.surfspotsxml

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfspots.R
import com.example.surfspots.Spot

class SpotDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_detail)
        val buttonRetour = findViewById<Button>(R.id.buttonRetourList)
        buttonRetour.setOnClickListener {
            finish() // Retour à l'accueil
        }
        val spot = intent.getParcelableExtra<Spot>("spot")

        if (spot != null) {
            val imageView = findViewById<ImageView>(R.id.detailImage)
            val nameView = findViewById<TextView>(R.id.detailName)
            val locationView = findViewById<TextView>(R.id.detailLocation)
            val surfBreakView = findViewById<TextView>(R.id.detailSurfBreak)

            imageView.setImageResource(spot.imageResId)
            nameView.text = spot.name
            locationView.text = spot.location
            surfBreakView.text = "Type de vague : ${spot.surfBreak}"
        } else {
            finish() // Si l’objet n’est pas passé, on ferme la page proprement
        }
    }
}

