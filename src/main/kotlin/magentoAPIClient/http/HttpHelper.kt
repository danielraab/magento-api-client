package magentoAPIClient.http

import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.swing.JOptionPane


fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")

class HttpHelper(private val requestInfo: RequestInfo) {

    fun sendRequest(showMessagePaneOnError:Boolean = true): HttpResponse<String> {
        try {
            val client = HttpClient.newBuilder().build()
            var requestUrl = requestInfo.baseUrl + requestInfo.serverPath

            if (requestInfo.urlParameters.any()) {
                requestUrl += "?" + requestInfo.urlParameters.map { (k, v) -> "${(k.utf8())}=${v.utf8()}" }
                    .joinToString("&")
            }

            val requestBuilder = HttpRequest.newBuilder().uri(URI.create(requestUrl))
            when (requestInfo.method) {
                Method.GET -> requestBuilder.GET()
                Method.POST -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestInfo.body))
                Method.PUT -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(requestInfo.body))
                else -> throw NotImplementedError()
            }
            requestInfo.headers.forEach { (k, v) -> requestBuilder.header(k.key, v) }

            return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
        } catch (ioe:IOException) {
            ioe.printStackTrace()
            if(showMessagePaneOnError) {    //TODO remove message pane again, no view action in helper
                JOptionPane.showMessageDialog(
                    null,
                    "Unable to connect to site.",
                    "error in connecting site",
                    JOptionPane.ERROR_MESSAGE
                )
            }
            throw ioe
        }
    }
}
