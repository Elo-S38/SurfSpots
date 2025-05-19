package com.example.surfspots

// 📦 Imports nécessaires
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.io.File

// 🎯 Adaptateur personnalisé pour relier la liste des spots au ListView
class SpotAdapter(
    private val context: Context,
    private val spots: List<Spot>
) : BaseAdapter() {

    // 📏 Nombre d’éléments dans la liste
    override fun getCount(): Int = spots.size

    // 📦 Retourne un objet Spot à une position donnée
    override fun getItem(position: Int): Any = spots[position]

    // 🔢 Retourne un ID pour chaque élément (ici sa position)
    override fun getItemId(position: Int): Long = position.toLong()

    // 🎨 Génère l'affichage de chaque élément dans la liste
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // ♻️ Réutilisation des vues pour l’optimisation (ViewHolder Pattern simplifié)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_spot, parent, false)

        // 🎯 Récupération des composants de la vue
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val nameView = view.findViewById<TextView>(R.id.textViewName)
        val locationView = view.findViewById<TextView>(R.id.textViewLocation)

        // 📥 Récupération des données du spot à cette position
        val spot = spots[position]
        nameView.text = spot.name
        locationView.text = spot.location

        val imageSource = spot.imageUrlOrPath

        // 🐞 Logs utiles pour déboguer les chemins d’image
        Log.d("DEBUG_IMAGE", "SpotAdapter: $imageSource")
        Log.d("DEBUG_IMAGE", "Exists = ${File(imageSource).exists()}")

        // 🖼️ Chargement de l’image selon son origine
        when {
            imageSource.startsWith("http") -> {
                // 🌐 URL distante → on utilise Glide
                Glide.with(context).load(imageSource)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView)
            }
            File(imageSource).exists() -> {
                // 📁 Fichier local → Glide peut aussi charger depuis un `File`
                Glide.with(context).load(File(imageSource))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView)
            }
            imageSource.startsWith("content://") -> {
                // 📦 URI Android (image sélectionnée depuis galerie)
                imageView.setImageURI(Uri.parse(imageSource))
            }
            else -> {
                // ❌ Aucun chemin valide → on affiche un placeholder
                imageView.setImageResource(R.drawable.placeholder)
            }
        }

        return view
    }
}
