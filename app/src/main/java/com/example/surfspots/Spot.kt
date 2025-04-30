package com.example.surfspots

import android.os.Parcel
import android.os.Parcelable

// Classe de données représentant un spot de surf
// Elle implémente Parcelable pour pouvoir être envoyée entre activités
data class Spot(
    val name: String,         // Nom du spot
    val location: String,     // Lieu du spot
    val imageResId: Int,      // ID de l’image dans drawable (ex : R.drawable.plage_catalans)
    val surfBreak: String     // Type de vague (reef, point, beach, etc.)
) : Parcelable {

    // Constructeur secondaire pour recréer un objet Spot depuis un Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",      // Lecture du champ name
        parcel.readString() ?: "",      // Lecture du champ location
        parcel.readInt(),               // Lecture du champ imageResId
        parcel.readString() ?: ""       // Lecture du champ surfBreak
    )

    // Méthode obligatoire : écrit chaque propriété dans le Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeInt(imageResId)
        parcel.writeString(surfBreak)
    }

    // Méthode obligatoire, presque toujours 0
    override fun describeContents(): Int = 0

    // Objet compagnon pour recréer des objets Spot depuis un Parcel
    companion object CREATOR : Parcelable.Creator<Spot> {

        // Crée un Spot à partir d’un Parcel (utilisé par Android automatiquement)
        override fun createFromParcel(parcel: Parcel): Spot = Spot(parcel)

        // Crée un tableau de Spot (souvent inutilisé)
        override fun newArray(size: Int): Array<Spot?> = arrayOfNulls(size)
    }
}
