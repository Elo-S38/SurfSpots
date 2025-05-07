package com.example.surfspots

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class SpotAdapter(private val context: Context, private val spots: List<Spot>) : BaseAdapter() {

    override fun getCount(): Int = spots.size
    override fun getItem(position: Int): Any = spots[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_spot, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val nameView = view.findViewById<TextView>(R.id.textViewName)
        val locationView = view.findViewById<TextView>(R.id.textViewLocation)

        val spot = spots[position]
        nameView.text = spot.name
        locationView.text = spot.location

        // 🖼️ Si imageUrl est présent, on charge avec Glide, sinon on utilise l'image locale
        if (!spot.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(spot.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imageView)
        } else {
            imageView.setImageResource(spot.imageResId)
        }

        return view
    }
}
