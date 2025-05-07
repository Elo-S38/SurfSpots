// Ce fichier définit un modèle de données nommé "Spot", qui représente un spot de surf.
// Il est utilisé pour stocker et transférer les données d’un spot entre différentes parties de l’application.
package com.example.surfspots

import android.os.Parcel
import android.os.Parcelable

// Version avec imageResId (chargement en dur depuis drawable)
data class Spot( // pour stocker des données.
    val name: String,          // nom du spot
    val location: String,      // pays ou état du spot
    val imageResId: Int,       // identifiant d'image locale (ex: R.drawable.nom)
    val surfBreak: String,     // type de vague (beach break, reef, etc.)
    val difficulty: Int,       // niveau de difficulté (ex: 1 à 5)
    val seasonStart: String,   // début de la meilleure saison
    val seasonEnd: String,     // fin de la meilleure saison
    val address: String        // adresse exacte ou lieu plus précis
) : Parcelable { //indique qu’on pourra transporter cette classe entre activités Android via un Intent.
    constructor(parcel: Parcel) : this( //	Reconstruit un Spot depuis un Parcel
        parcel.readString() ?: "",      // lit le nom depuis le Parcel
        parcel.readString() ?: "",      // lit le lieu
        parcel.readInt(),               // lit l'imageResId
        parcel.readString() ?: "",      // lit le type de vague
        parcel.readInt(),               // lit la difficulté
        parcel.readString() ?: "",      // saison début
        parcel.readString() ?: "",      // saison fin
        parcel.readString() ?: ""       // adresse
    )

    //Convertit un Spot en Parcel pour transmission
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeInt(imageResId)
        parcel.writeString(surfBreak)
        parcel.writeInt(difficulty)
        parcel.writeString(seasonStart)
        parcel.writeString(seasonEnd)
        parcel.writeString(address)
    }

    override fun describeContents(): Int = 0

    //Outil utilisé par Android pour reconstruire un Spot
    companion object CREATOR : Parcelable.Creator<Spot> {
        override fun createFromParcel(parcel: Parcel): Spot = Spot(parcel)
        override fun newArray(size: Int): Array<Spot?> = arrayOfNulls(size)
    }
}