package com.example.surfspotsxml

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.surfspots.R

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialise la musique uniquement une fois, quand l'activité démarre
        mediaPlayer = MediaPlayer.create(this, R.raw.surfmusic)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Bouton "Voir les spots" - ouvre SpotsActivity
        val buttonVoirSpots = findViewById<Button>(R.id.buttonVoirSpots)
        buttonVoirSpots.setOnClickListener {
            //  Stoppe et libère la musique AVANT de changer d’écran
            stopMusic()

            // Ouvre SpotsActivity
            val intent = Intent(this, SpotsActivity::class.java)
            startActivity(intent)
        }

        // Bouton "Ajouter un Spot" - ouvre AjoutSpotActivity
        val buttonAjouterSpot = findViewById<Button>(R.id.buttonAjoutSpot)
        buttonAjouterSpot.setOnClickListener {
            //  Stoppe et libère la musique AVANT de changer d’écran
            stopMusic()

            // Ouvre AjoutSpotActivity
            val intent = Intent(this, AjoutSpotActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //  Sécurité : libération finale si activité détruite (ex: app fermée)
        stopMusic()
    }

    // Fonction pour arrêter la musique proprement
    private fun stopMusic() {
        if (::mediaPlayer.isInitialized) {
            try {
                mediaPlayer.stop()
            } catch (_: IllegalStateException) {}
            mediaPlayer.release()
        }
    }
}
