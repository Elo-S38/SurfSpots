package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.surfspots.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.buttonVoirSpots)
        Toast.makeText(this, "Clic détecté !", Toast.LENGTH_LONG).show()
        println("🧪 Bouton trouvé ? ${button != null}") // test
        button.setOnClickListener {
            // Test du clic
            Toast.makeText(this, "Clic détecté !", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SpotsActivity::class.java)
            startActivity(intent)
        }
    }
}
