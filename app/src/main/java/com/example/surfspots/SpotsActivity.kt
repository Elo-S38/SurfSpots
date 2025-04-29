package com.example.surfspotsxml

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.surfspots.R

class SpotsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots)

        Toast.makeText(this, "Page des spots affichée", Toast.LENGTH_SHORT).show()

        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener {
            finish()
        }
    }
}

