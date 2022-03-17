package magentoAPIClient

enum class Method { GET, POST }

enum class Header(val key: String) { AUTHORIZATION("Authorization") }

data class RequestInfo(
    var baseUrl: String,
    val serverPath: String,
    val method: Method,
    val urlParameters: MutableMap<String, String>,
    val headers: MutableMap<Header, String>
)