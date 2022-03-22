package magentoAPIClient.product.export

data class FullProduct(
    val id: Int,
    val simpleAttrMap: MutableMap<String, Any> = mutableMapOf(),
    val customAttrMap: MutableMap<String, Any?> = mutableMapOf(),
    val extAttrMap: MutableMap<String, Any> = mutableMapOf()
)