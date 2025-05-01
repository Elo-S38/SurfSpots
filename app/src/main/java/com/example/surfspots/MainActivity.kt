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

        // 🎵 Initialise la musique
        mediaPlayer = MediaPlayer.create(this, R.raw.surfmusic)
        mediaPlayer.isLooping = true // la musique tourne en boucle
        mediaPlayer.start()

        val button = findViewById<Button>(R.id.buttonVoirSpots)
        button.setOnClickListener {
            // ⛔ Arrête la musique quand on quitte la page d’accueil
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }

            val intent = Intent(this, SpotsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Nettoyage final au cas où
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    // Cycle de vie : pour debug Logcat
    override fun onStart() { super.onStart(); println("🟢 onStart appelé") }
    override fun onResume() { super.onResume(); println("✅ onResume appelé") }
    override fun onPause() { super.onPause(); println("⏸️ onPause appelé") }
    override fun onStop() { super.onStop(); println("⏹️ onStop appelé") }
}
