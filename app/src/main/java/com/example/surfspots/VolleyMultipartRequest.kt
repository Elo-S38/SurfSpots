package com.example.surfspotsxml

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import java.io.*
import java.nio.charset.Charset

open class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    private val boundary = "apiclient-${System.currentTimeMillis()}"
    private val mimeType = "multipart/form-data;boundary=$boundary"

    override fun getBodyContentType(): String = mimeType

    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        // Paramètres texte
        getParams()?.forEach { (key, value) ->
            dos.writeBytes("--$boundary\r\n")
            dos.writeBytes("Content-Disposition: form-data; name=\"$key\"\r\n\r\n")
            dos.writeBytes("$value\r\n")
        }

        // Paramètres fichier
        getByteData().forEach { (key, dataPart) ->
            dos.writeBytes("--$boundary\r\n")
            dos.writeBytes("Content-Disposition: form-data; name=\"$key\"; filename=\"${dataPart.fileName}\"\r\n")
            dos.writeBytes("Content-Type: ${dataPart.type}\r\n\r\n")
            dos.write(dataPart.content)
            dos.writeBytes("\r\n")
        }

        dos.writeBytes("--$boundary--\r\n")
        return bos.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }

    open fun getByteData(): Map<String, DataPart> = hashMapOf()

    data class DataPart(
        val fileName: String,
        val content: ByteArray,
        val type: String = "image/jpeg"
    )
}
