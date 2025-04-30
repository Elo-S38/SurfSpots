package com.example.surfspotsxml

// Import des classes nécessaires pour la gestion de la vue (UI)
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.surfspots.R
import com.example.surfspots.Spot // Import de la classe Spot qui contient les données

// Classe principale de l'adaptateur
// Cet adaptateur est utilisé pour transformer la liste des objets Spot en vues (éléments de la ListView)
class SpotAdapter(private val context: Context, private val dataSource: List<Spot>) : BaseAdapter() {

    // Cette méthode renvoie le nombre d'éléments dans la liste de données
    // Elle est utilisée par la ListView
    override fun getCount(): Int = dataSource.size

    // Cette méthode renvoie l'élément (spot) à la position donnée dans la liste
    // Elle est utilisée pour obtenir les données de l'élément en cours
    override fun getItem(position: Int): Any = dataSource[position]

    // Cette méthode renvoie l'ID unique de l'élément à la position donnée
    // L'ID n'est pas utilisé dans notre cas, donc on renvoie simplement la position
    override fun getItemId(position: Int): Long = position.toLong()

    // Cette méthode est la plus importante et est appelée pour chaque élément de la liste
    // Elle crée la vue pour un élément de la ListView à la position donnée
    // Si la vue a déjà été créée (via convertView), elle est réutilisée, sinon elle est gonflée
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Si convertView est null, cela signifie qu'il faut gonfler une nouvelle vue
        // Sinon, on réutilise la vue existante (optimisation des performances)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_spot, parent, false)

        // Récupère les références des composants de la vue : ImageView et TextViews
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val nameTextView = view.findViewById<TextView>(R.id.textViewName)
        val locationTextView = view.findViewById<TextView>(R.id.textViewLocation)

        // Récupère l'objet Spot correspondant à la position actuelle dans la liste
        val spot = getItem(position) as Spot

        // Met à jour l'ImageView avec l'image correspondant à l'ID de la ressource (imageResId)
        imageView.setImageResource(spot.imageResId)

        // Met à jour le TextView pour le nom du spot
        nameTextView.text = spot.name

        // Met à jour le TextView pour le lieu du spot
        locationTextView.text = spot.location

        // Renvoie la vue (l'élément de la liste)
        // Cette vue sera utilisée par la ListView pour afficher un élément
        return view
    }
}

