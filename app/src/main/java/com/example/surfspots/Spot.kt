package com.example.surfspots

import android.os.Parcel
import android.os.Parcelable

// 🏄 Modèle représentant un spot de surf
data class Spot(
    val id: Int,                    // Identifiant du spot
    val name: String,              // Nom du spot
    val location: String,          // Lieu géographique
    val imageUrlOrPath: String,    // URL distante ou chemin local de l’image
    val surfBreak: String,         // Type de vague (ex: beach break, reef break...)
    val difficulty: Int,           // Difficulté sur 5
    val seasonStart: String,       // Début de la saison
    val seasonEnd: String,         // Fin de la saison
    val address: String,           // Adresse (dupliquée avec location parfois)
    val rating: Int                // ✅ Note moyenne (sur 5)
) : Parcelable {

    // 📥 Constructeur secondaire pour reconstruire un objet à partir d’un Parcel
    constructor(parcel: Parcel) : this(
        parcel.readInt(),                     // id
        parcel.readString() ?: "",            // name
        parcel.readString() ?: "",            // location
        parcel.readString() ?: "",            // imageUrlOrPath
        parcel.readString() ?: "",            // surfBreak
        parcel.readInt(),                     // difficulty
        parcel.readString() ?: "",            // seasonStart
        parcel.readString() ?: "",            // seasonEnd
        parcel.readString() ?: "",            // address
        parcel.readInt()                      // ✅ lecture de rating
    )

    // 📤 Écrit les données dans un Parcel (ordre important)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(imageUrlOrPath)
        parcel.writeString(surfBreak)
        parcel.writeInt(difficulty)
        parcel.writeString(seasonStart)
        parcel.writeString(seasonEnd)
        parcel.writeString(address)
        parcel.writeInt(rating) // ✅ écriture de rating
    }

    // 👤 Méthode obligatoire pour Parcelable
    override fun describeContents(): Int = 0

    // 🎁 Companion object pour recréer un Spot depuis un Parcel
    companion object CREATOR : Parcelable.Creator<Spot> {
        override fun createFromParcel(parcel: Parcel): Spot = Spot(parcel)
        override fun newArray(size: Int): Array<Spot?> = arrayOfNulls(size)
    }
}
