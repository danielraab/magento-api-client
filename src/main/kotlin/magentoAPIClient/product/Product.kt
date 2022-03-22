package magentoAPIClient.product

import org.json.JSONObject


data class Product(
    val id: Int,
    val sku: String,
    val name: String,
    val status: Int,
    val type: String,
    var selected: Boolean = false
) {

    fun updateColumn(columnIndex: Int, aValue: Any?) {
        if (columnIndex == SELECTION_COLUMN_INDEX && aValue is Boolean) {
            selected = aValue
        }
    }

    fun toArray() = arrayOf(selected, id, sku, name, status, type)

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