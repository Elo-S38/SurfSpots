// FICHIER : SpotAdapter.kt
// ----------------------------------------------
// Ce fichier contient l'adapter personnalisé pour afficher
// une liste de spots dans une ListView. Il transforme chaque
// objet Spot en une ligne affichable avec une image, un nom et un lieu.

package com.example.surfspots

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

// 👇 Cette classe prend en entrée le contexte (Activity) et une liste de Spot
class SpotAdapter(private val context: Context, private val spots: List<Spot>) : BaseAdapter() {

    // ➕ Retourne le nombre total d’éléments à afficher dans la liste
    override fun getCount(): Int = spots.size

    // 🔢 Retourne l'objet Spot à une position donnée
    override fun getItem(position: Int): Any = spots[position]

    // 🆔 Retourne l’ID de l’élément (ici, sa position)
    override fun getItemId(position: Int): Long = position.toLong()

    // 🧱 Fonction principale qui génère la vue pour chaque ligne de la ListView
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // ⚙️ Si une vue peut être recyclée, on l'utilise. Sinon, on en crée une nouvelle à partir du layout XML
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_spot, parent, false)

        // 🔍 On récupère les éléments graphiques à remplir dans la ligne
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val nameView = view.findViewById<TextView>(R.id.textViewName)
        val locationView = view.findViewById<TextView>(R.id.textViewLocation)

        // 📦 On récupère le Spot correspondant à la ligne en cours
        val spot = spots[position]

        // ✏️ On remplit les vues avec les données du Spot
        nameView.text = spot.name
        locationView.text = spot.location
        imageView.setImageResource(spot.imageResId)  // On affiche l’image depuis drawable

        // 🔚 On retourne la ligne remplie
        return view
    }
}
//Un adapter en Android, c’est un intermédiaire :
//    Il prend une liste de données (ici : tes objets Spot)
//    Et il fabrique des vues à afficher à l’écran (dans une ListView)