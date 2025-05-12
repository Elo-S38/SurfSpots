package com.example.surfspotsxml

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.surfspots.R
import com.example.surfspots.Spot
import java.io.File

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

            val imagePath = spot.imageUrlOrPath
            Log.d("DEBUG_IMAGE", "DetailActivity: ${spot.imageUrlOrPath}")
            Log.d("DEBUG_IMAGE", "Exists = ${File(spot.imageUrlOrPath).exists()}")

            when {
                imagePath.startsWith("http") -> {
                    Glide.with(this).load(imagePath)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(imageView)
                }
                File(imagePath).exists() -> {
                    Glide.with(this).load(File(imagePath))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(imageView)
                }
                imagePath.startsWith("content://") -> {
                    imageView.setImageURI(Uri.parse(imagePath))
                }
                else -> {
                    imageView.setImageResource(R.drawable.placeholder)
                }
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
