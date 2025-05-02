package com.example.surfspotsxml

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.surfspots.R
import com.example.surfspots.Spot

class SpotDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_detail)

        val buttonRetour = findViewById<Button>(R.id.buttonRetourList)
        buttonRetour.setOnClickListener {
            finish()
        }

        val spot = intent.getParcelableExtra<Spot>("spot")

        if (spot != null) {
            val imageView = findViewById<ImageView>(R.id.detailImage)
            val nameView = findViewById<TextView>(R.id.detailName)
            val locationView = findViewById<TextView>(R.id.detailLocation)
            val surfBreakView = findViewById<TextView>(R.id.detailSurfBreak)

            // ✅ Chargement de l'image via Glide
            Glide.with(this)
                .load(spot.imageResId)
                .placeholder(R.drawable.placeholder) // image de secours
                .error(R.drawable.placeholder)       // si l’image ne charge pas
                .into(imageView)

            nameView.text = spot.name
            locationView.text = spot.location
            surfBreakView.text = "Type de vague : ${spot.surfBreak}"
        } else {
            finish()
        }
    }
}
