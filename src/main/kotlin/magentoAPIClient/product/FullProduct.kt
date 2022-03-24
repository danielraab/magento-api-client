package magentoAPIClient.product

import org.json.JSONObject

data class FullProduct(
    val id: Int,
    val simpleAttrMap: MutableMap<String, Any> = mutableMapOf(),
    val customAttrMap: MutableMap<String, Any> = mutableMapOf(),
    val extAttrMap: MutableMap<String, Any> = mutableMapOf()
) {
    var selected: Boolean = false
    val sku: String
        get() = simpleAttrMap["sku"] as String
    val name: String
        get() = simpleAttrMap["name"] as String
    val status: Int
        get() = simpleAttrMap["status"] as Int
    val type: String
        get() = simpleAttrMap["type_id"] as String

    companion object {
        const val SELECTION_COLUMN_INDEX = 0
        fun getColumns() = arrayOf("selected", "id", "sku", "name", "status", "type")
        fun editableColumnArray() = arrayOf(true, false, false, false, false, false)
        fun getColumnsClass() = arrayOf(
            Boolean::class.javaObjectType,
            Int::class.javaObjectType,
            String::class.javaObjectType,
            String::class.javaObjectType,
            Int::class.javaObjectType,
            String::class.javaObjectType
        )
    }

    fun updateColumn(columnIndex: Int, aValue: Any?) {
        if (columnIndex == SELECTION_COLUMN_INDEX && aValue is Boolean) {
            selected = aValue
        }
    }

    fun toArray() = arrayOf(selected, id, sku, name, status, type)

}


enum class ProductAttributeType(val typeKey: String) {
    BASIC(""),
    CUSTOM("custom_attributes"),
    EXTENSION("extension_attributes")
}

enum class ProductAttributeValueType() { STRING, NUMBER, PLAIN }

data class ProductAttributeUpdate(
    val type: ProductAttributeType,
    val valueType: ProductAttributeValueType,
    val key: String,
    val value: String
)

fun ProductAttributeUpdate.toJSONObject(): JSONObject {
    val obj = JSONObject()
    obj.put("type", this.type)
    obj.put("valueType", this.valueType)
    obj.put("key", this.key)
    obj.put("value", this.value)
    return obj
}

fun JSONObject.toProductAttributeUpdateObj(): ProductAttributeUpdate {
    return ProductAttributeUpdate(
        ProductAttributeType.valueOf(this.getString("type")),
        ProductAttributeValueType.valueOf(this.getString("valueType")),
        this.getString("key"),
        this.getString("value")
    )
}