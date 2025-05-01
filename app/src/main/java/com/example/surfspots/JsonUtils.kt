package com.example.surfspots

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

fun lireJsonDepuisRaw(context: Context, resourceId: Int): JSONObject? {
    return try {
        val inputStream = context.resources.openRawResource(resourceId)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val jsonString = String(buffer, Charset.forName("UTF-8"))
        JSONObject(jsonString)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
