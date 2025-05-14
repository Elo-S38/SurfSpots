package com.example.surfspots

import android.os.Parcel
import android.os.Parcelable

data class Spot(
    val id: Int,
    val name: String,
    val location: String,
    val imageUrlOrPath: String,
    val surfBreak: String,
    val difficulty: Int,
    val seasonStart: String,
    val seasonEnd: String,
    val address: String,
    val rating: Int // ✅ Nouveau champ ajouté
) : Parcelable {

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

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Spot> {
        override fun createFromParcel(parcel: Parcel): Spot = Spot(parcel)
        override fun newArray(size: Int): Array<Spot?> = arrayOfNulls(size)
    }
}
