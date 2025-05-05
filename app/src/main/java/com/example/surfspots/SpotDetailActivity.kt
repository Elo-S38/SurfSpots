// FICHIER : SpotDetailActivity.kt
// Cette activité affiche les détails d’un spot sélectionné depuis la liste.

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

        // 📄 On affiche le layout associé à cette activité
        setContentView(R.layout.activity_spot_detail)

        // 🔙 Bouton pour revenir à la liste des spots
        val buttonRetour = findViewById<Button>(R.id.buttonRetourList)
        buttonRetour.setOnClickListener {
            finish() // Ferme cette activité et revient en arrière
        }

        // 📦 On récupère l’objet Spot passé depuis la liste (via Intent)
        val spot = intent.getParcelableExtra<Spot>("spot")

        if (spot != null) {
            // 🧱 On relie les éléments du layout aux variables
            val imageView = findViewById<ImageView>(R.id.detailImage)
            val nameView = findViewById<TextView>(R.id.detailName)
            val locationView = findViewById<TextView>(R.id.detailLocation)
            val surfBreakView = findViewById<TextView>(R.id.detailSurfBreak)
            val difficultyView = findViewById<TextView>(R.id.detailDifficulty)
            val seasonView = findViewById<TextView>(R.id.detailSeason)
            val addressView = findViewById<TextView>(R.id.detailAddress)

            // 🖼️ Affiche l'image du spot grâce à Glide
            Glide.with(this)
                .load(spot.imageResId)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imageView)

            // 📝 On affiche les infos du spot dans les champs texte
            nameView.text = spot.name
            locationView.text = spot.location
            surfBreakView.text = spot.surfBreak
            difficultyView.text = "${spot.difficulty}/5"
            seasonView.text = "${spot.seasonStart} → ${spot.seasonEnd}"
            addressView.text = spot.address
        } else {
            // ❌ Si aucun spot n’est reçu, on ferme l’écran
            finish()
        }
    }
}
