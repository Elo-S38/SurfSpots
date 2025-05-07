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
        buttonRetour.setOnClickListener { finish() }

        val spot = intent.getParcelableExtra<Spot>("spot")

        if (spot != null) {
            val imageView = findViewById<ImageView>(R.id.detailImage)
            val nameView = findViewById<TextView>(R.id.detailName)
            val locationView = findViewById<TextView>(R.id.detailLocation)
            val surfBreakView = findViewById<TextView>(R.id.detailSurfBreak)
            val difficultyView = findViewById<TextView>(R.id.detailDifficulty)
            val seasonView = findViewById<TextView>(R.id.detailSeason)
            val addressView = findViewById<TextView>(R.id.detailAddress)

            // Image Cloudinary si dispo, sinon image drawable
            if (!spot.imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(spot.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView)
            } else {
                imageView.setImageResource(spot.imageResId)
            }

            nameView.text = spot.name
            locationView.text = spot.location
            surfBreakView.text = spot.surfBreak
            difficultyView.text = "${spot.difficulty}/5"
            seasonView.text = "${spot.seasonStart} → ${spot.seasonEnd}"
            addressView.text = spot.address
        } else {
            finish()
        }
    }
}
