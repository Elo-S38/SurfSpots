package com.example.surfspots

import android.os.Parcel
import android.os.Parcelable

// Version avec imageResId (chargement en dur depuis drawable)
data class Spot(
    val name: String,
    val location: String,
    val imageResId: Int,
    val surfBreak: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeInt(imageResId)
        parcel.writeString(surfBreak)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Spot> {
        override fun createFromParcel(parcel: Parcel): Spot = Spot(parcel)
        override fun newArray(size: Int): Array<Spot?> = arrayOfNulls(size)
    }
}
