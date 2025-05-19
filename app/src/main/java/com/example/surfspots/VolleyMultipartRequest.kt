package com.example.surfspotsxml

// 📦 Imports nécessaires pour Volley et le traitement binaire
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import java.io.*
import java.nio.charset.Charset

// 🧩 Classe personnalisée pour envoyer une requête multipart avec Volley (images, fichiers, etc.)
open class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    // 🔗 Délimiteur pour séparer les parties de la requête multipart
    private val boundary = "apiclient-${System.currentTimeMillis()}"

    // 🧾 Type MIME de la requête : multipart/form-data
    private val mimeType = "multipart/form-data;boundary=$boundary"

    override fun getBodyContentType(): String = mimeType

    // 📦 Corps de la requête : texte + fichiers
    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        // ➕ Ajout des champs texte
        getParams()?.forEach { (key, value) ->
            dos.writeBytes("--$boundary\r\n")
            dos.writeBytes("Content-Disposition: form-data; name=\"$key\"\r\n\r\n")
            dos.writeBytes("$value\r\n")
        }

        // ➕ Ajout des fichiers (images, etc.)
        getByteData().forEach { (key, dataPart) ->
            dos.writeBytes("--$boundary\r\n")
            dos.writeBytes("Content-Disposition: form-data; name=\"$key\"; filename=\"${dataPart.fileName}\"\r\n")
            dos.writeBytes("Content-Type: ${dataPart.type}\r\n\r\n")
            dos.write(dataPart.content)
            dos.writeBytes("\r\n")
        }

        // 🧵 Fin de la requête multipart
        dos.writeBytes("--$boundary--\r\n")
        return bos.toByteArray()
    }

    // 🔄 Traite la réponse reçue de l'API
    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    // 📩 Envoie la réponse au listener
    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }

    // 📥 Méthode à surcharger pour fournir les fichiers à envoyer
    open fun getByteData(): Map<String, DataPart> = hashMapOf()

    // 📄 Structure de fichier à envoyer (nom, contenu, type MIME)
    data class DataPart(
        val fileName: String,
        val content: ByteArray,
        val type: String = "image/jpeg"
    )
}
