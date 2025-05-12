package com.example.surfspots

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

class SpotAdapter(private val context: Context, private val spots: List<Spot>) : BaseAdapter() {

    override fun getCount(): Int = spots.size
    override fun getItem(position: Int): Any = spots[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_spot, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val nameView = view.findViewById<TextView>(R.id.textViewName)
        val locationView = view.findViewById<TextView>(R.id.textViewLocation)

        val spot = spots[position]
        nameView.text = spot.name
        locationView.text = spot.location

        val imageSource = spot.imageUrlOrPath

        Log.d("DEBUG_IMAGE", "SpotAdapter: $imageSource")
        Log.d("DEBUG_IMAGE", "Exists = ${File(imageSource).exists()}")


        when {
            imageSource.startsWith("http") -> {
                Glide.with(context).load(imageSource)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView)
            }
            File(imageSource).exists() -> {
                Glide.with(context).load(File(imageSource))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView)
            }
            imageSource.startsWith("content://") -> {
                imageView.setImageURI(Uri.parse(imageSource))
            }
            else -> {
                imageView.setImageResource(R.drawable.placeholder)
            }
        }

        return view
    }
}
