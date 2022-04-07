package magentoAPIClient

import magentoAPIClient.product.ProductAttributeUpdate
import java.nio.charset.Charset

enum class AvailableCharset(val charset:Charset) { ISO_8859_1(Charsets.ISO_8859_1), UTF_8(Charsets.UTF_8)}

data class Configuration(
    var baseUrl: String = "",
    var authentication: String = "",
    var storeView: String = "",
    var columnSeparator: Char = ';',
    var encoding: AvailableCharset = AvailableCharset.ISO_8859_1,
    val productAttributeUpdateList: MutableList<ProductAttributeUpdate> = mutableListOf()
)