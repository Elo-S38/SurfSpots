// FICHIER : SpotsActivity.kt
// ---------------------------------------------------
// Cette activité lit un fichier JSON contenant une liste de spots de surf,
// crée une liste d'objets Spot, et les affiche dans une ListView.

package com.example.surfspotsxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfspots.R
import com.example.surfspots.Spot
import com.example.surfspots.lireJsonDepuisRaw // fonction pour lire le JSON
import com.example.surfspots.SpotAdapter       // adapter pour la ListView
import org.json.JSONArray

class SpotsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 📄 On associe cette activité à son layout XML
        setContentView(R.layout.activity_spots)

        // 🔙 Bouton pour revenir à la page d'accueil
        val buttonRetour = findViewById<Button>(R.id.buttonRetourAccueil)
        buttonRetour.setOnClickListener { finish() }

        // 🧾 La ListView qui va afficher la liste des spots
        val listView: ListView = findViewById(R.id.listView)

        // 🧪 Liste vide à remplir avec les objets Spot
        val spots = mutableListOf<Spot>()

        // 📥 Lecture du fichier JSON dans /res/raw/spots.json
        val json = lireJsonDepuisRaw(this, R.raw.spots)

        // 🔍 On récupère le tableau "records" à l’intérieur du fichier JSON
        val spotsArray: JSONArray = json?.getJSONArray("records") ?: JSONArray()

        // 🔁 Boucle sur chaque spot dans le JSON
        for (i in 0 until spotsArray.length()) {
            val item = spotsArray.getJSONObject(i).getJSONObject("fields")

            // On extrait les champs qu'on veut
            val name = item.getString("Destination")
            val location = item.getString("Destination State/Country")
            val surfBreak = item.getJSONArray("Surf Break").getString(0)
            val difficulty = item.optInt("Difficulty Level", 0)
            val seasonStart = item.optString("Peak Surf Season Begins", "N/A")
            val seasonEnd = item.optString("Peak Surf Season Ends", "N/A")
            val address = item.optString("Address", "N/A")

            // 🖼️ On transforme le nom en nom d’image (ex : bali_beach)
            val imageName = name
                .lowercase()
                .replace(" ", "_")
                .replace("-", "_")

            // 🔍 On récupère l'identifiant de l'image dans drawable
            val imageResId = resources.getIdentifier(imageName, "drawable", packageName)

            // 🛟 Si l’image n’est pas trouvée, on utilise une image par défaut
            val finalImageResId = if (imageResId != 0) imageResId else R.drawable.placeholder

            // 📦 On crée un objet Spot avec les infos du JSON
            val spot = Spot(name, location, finalImageResId, surfBreak, difficulty, seasonStart, seasonEnd, address)

            // ➕ On ajoute le Spot à la liste
            spots.add(spot)
        }

        // 📋 On crée un adapter pour afficher la liste dans la ListView
        val adapter = SpotAdapter(this, spots)
        listView.adapter = adapter

        // 👆 Quand on clique sur un spot, on ouvre la page de détail
        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, SpotDetailActivity::class.java)
            intent.putExtra("spot", spots[position]) // On envoie le Spot cliqué
            startActivity(intent)
        }
    }
}

//Lit les données depuis le fichier spots.json
//Crée une liste d’objets Spot
//Affiche chaque Spot dans une ListView grâce à un SpotAdapter
//Ouvre SpotDetailActivity quand on clique sur une ligne