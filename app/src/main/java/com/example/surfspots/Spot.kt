package com.example.surfspots

import android.os.Parcel
import android.os.Parcelable

data class Spot(
    val name: String,
    val location: String,
    val imageUrlOrPath: String,     // ✅ Nouveau champ unifié
    val surfBreak: String,
    val difficulty: Int,
    val seasonStart: String,
    val seasonEnd: String,
    val address: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",  // 🔄 lecture de imageUrlOrPath
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(imageUrlOrPath) // 🔄 écriture de imageUrlOrPath
        parcel.writeString(surfBreak)
        parcel.writeInt(difficulty)
        parcel.writeString(seasonStart)
        parcel.writeString(seasonEnd)
        parcel.writeString(address)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Spot> {
        override fun createFromParcel(parcel: Parcel): Spot = Spot(parcel)
        override fun newArray(size: Int): Array<Spot?> = arrayOfNulls(size)
    }
}
