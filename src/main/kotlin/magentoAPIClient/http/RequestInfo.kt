package magentoAPIClient.http

enum class Method { GET, POST, PUT }

enum class Header(val key: String) { AUTHORIZATION("Authorization"), CONTENT_TYPE("Content-Type")}
enum class HeaderContentType(val key:String) { JSON("application/json")}

data class RequestInfo(
    var baseUrl: String,
    val serverPath: String,
    val method: Method,
    val urlParameters: MutableMap<String, String>,
    val headers: MutableMap<Header, String>,
    val body: String = ""
)