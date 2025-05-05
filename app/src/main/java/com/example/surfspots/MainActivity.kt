// FICHIER : MainActivity.kt
// ----------------------------
// Ce fichier représente l'écran d’accueil de l’application SurfSpots.
// Il affiche le logo, le titre, un bouton "Voir les spots" et lance une musique en fond.

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

        // ✅ Initialise la musique uniquement une fois, quand l'activité démarre
        mediaPlayer = MediaPlayer.create(this, R.raw.surfmusic)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        val button = findViewById<Button>(R.id.buttonVoirSpots)

        button.setOnClickListener {
            // ✅ Stoppe et libère la musique AVANT de changer d’écran
            if (::mediaPlayer.isInitialized) {
                try {
                    mediaPlayer.stop()
                } catch (_: IllegalStateException) {}
                mediaPlayer.release()
            }

            val intent = Intent(this, SpotsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 🧼 Sécurité : libération finale si activité détruite (ex: app fermée)
        if (::mediaPlayer.isInitialized) {
            try {
                mediaPlayer.stop()
            } catch (_: IllegalStateException) {}
            mediaPlayer.release()
        }
    }
}



//Ce fichier affiche la page d’accueil.
//Il joue de la musique en boucle (option sympa !).
//Il arrête la musique quand l’utilisateur clique sur "Voir les spots".
//Il ouvre SpotsActivity.kt via un Intent.