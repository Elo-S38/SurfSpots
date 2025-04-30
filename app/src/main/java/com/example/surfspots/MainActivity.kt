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
        //Toast.makeText(this, "Clic détecté !", Toast.LENGTH_LONG).show()
        //println("🧪 Bouton trouvé ? ${button != null}") // test
        button.setOnClickListener {
            // Test du clic
            //Toast.makeText(this, "Clic détecté !", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SpotsActivity::class.java)
            startActivity(intent)
        }
    }

    //test cycle de vie de l'appli (a voir dans Logcat)
    override fun onStart() {
        super.onStart()
        println("🟢 onStart appelé")
    }

    override fun onResume() {
        super.onResume()
        println("✅ onResume appelé")
    }

    override fun onPause() {
        super.onPause()
        println("⏸️ onPause appelé")
    }

    override fun onStop() {
        super.onStop()
        println("⏹️ onStop appelé")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("❌ onDestroy appelé")
    }

}
