// FICHIER : JsonUtils.kt
// Ce fichier contient une fonction pour lire un fichier JSON local
// (placé dans le dossier res/raw) et le convertir en objet JSONObject.

package com.example.surfspots

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

// Fonction pour lire un fichier JSON à partir du dossier raw
// Elle prend le contexte et l’identifiant du fichier (ex : R.raw.spots)
fun lireJsonDepuisRaw(context: Context, resourceId: Int): JSONObject? {
    return try {
        // Ouvre le fichier JSON dans les ressources
        val inputStream = context.resources.openRawResource(resourceId)

        // Lit la taille du fichier (en octets)
        val size = inputStream.available()

        // Crée un tableau de bytes de la même taille
        val buffer = ByteArray(size)

        // Lis le contenu dans le buffer
        inputStream.read(buffer)

        // Ferme le fichier après lecture
        inputStream.close()

        // Transforme le contenu du buffer (UTF-8) en texte
        val jsonString = String(buffer, Charset.forName("UTF-8"))

        // Transforme le texte en objet JSON utilisable
        JSONObject(jsonString)

    } catch (e: IOException) {
        // En cas d'erreur de lecture, affiche le problème et retourne null
        e.printStackTrace()
        null
    }
}
