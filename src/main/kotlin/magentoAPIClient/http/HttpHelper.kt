package magentoAPIClient.http

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")

class HttpHelper(private val requestInfo: RequestInfo) {

    fun sendRequest(): HttpResponse<String> {
        val client = HttpClient.newBuilder().build()
        var requestUrl = requestInfo.baseUrl + requestInfo.serverPath

        if (requestInfo.urlParameters.any()) {
            requestUrl += "?" + requestInfo.urlParameters.map { (k, v) -> "${(k.utf8())}=${v.utf8()}" }.joinToString("&")
        }

        val requestBuilder = HttpRequest.newBuilder().uri(URI.create(requestUrl))
        if(requestInfo.method != Method.GET) throw NotImplementedError()
        requestInfo.headers.forEach { (k, v) -> requestBuilder.header(k.key, v) }

        return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
    }
}
