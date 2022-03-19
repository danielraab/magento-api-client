package magentoAPIClient.http

import magentoAPIClient.product.ProductAttributeType
import magentoAPIClient.product.ProductAttributeUpdate
import magentoAPIClient.product.ProductAttributeValueType
import magentoAPIClient.toJSONObject
import org.json.JSONObject
import org.json.JSONTokener

class ProductRequestFactory {
    companion object {

        fun listAttributeSets(baseUrl: String, authentication: String, storeView: String = "all", pageSize: Int = 300) =
            RequestInfo(
                baseUrl,
                "/rest/${storeView}/V1/products/attribute-sets/sets/list",
                Method.GET,
                mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
                mutableMapOf(Header.AUTHORIZATION to authentication)
            )


        fun listAttributes(baseUrl: String, authentication: String, storeView: String = "all", pageSize: Int = 300) =
            RequestInfo(
                baseUrl,
                "/rest/${storeView}/V1/products/attributes",
                Method.GET,
                mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
                mutableMapOf(Header.AUTHORIZATION to authentication)
            )


        fun attributesOfAttributeSet(
            baseUrl: String,
            authentication: String,
            storeView: String = "all",
            attrSetId: Int
        ) = RequestInfo(
            baseUrl,
            "/rest/${storeView}/V1/products/attribute-sets/${attrSetId}/attributes",
            Method.GET,
            mutableMapOf(),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )


        fun getProductList(baseUrl: String, authentication: String, storeView: String, pageSize: Int = 300) =
            RequestInfo(
                baseUrl,
                "/rest/${storeView}/V1/products",
                Method.GET,
                mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
                mutableMapOf(Header.AUTHORIZATION to authentication)
            )


        fun updateProduct(
            baseUrl: String,
            authentication: String,
            storeView: String?,
            sku: String,
            updateAttrList: List<ProductAttributeUpdate>
        ) = RequestInfo(
            baseUrl,
            "/rest/${storeView?.let { "$this/" }}V1/products/$sku",
            Method.PUT,
            mutableMapOf(),
            mutableMapOf(Header.AUTHORIZATION to authentication),
            JSONObject().also { prod ->
                prod.put("product", updateAttrList.toUpdateObject())
            }.toString()
        )
    }
}

fun List<ProductAttributeUpdate>.toUpdateObject(): JSONObject {
    val update = JSONObject()

    this.filter { it.type == ProductAttributeType.BASIC }.forEach {
        update.put(it.key, it.getValueString())
    }

    val custAttr = this.filter { it.type == ProductAttributeType.CUSTOM }
    val extAttr = this.filter { it.type == ProductAttributeType.EXTENSION }

    if (custAttr.isNotEmpty()) {
        update.put(
            "custom_attributes",
            custAttr.map { updateAttr ->
                JSONObject().also {
                    it.put("attribute_code", updateAttr.key)
                    it.put("value", updateAttr.getValueString())
                }
            })
    }

    if (extAttr.isNotEmpty()) {
        update.put(
            "extension_attributes",
            JSONObject().also { extObj ->
                extAttr.forEach { extObj.put(it.key, it.getValueString()) }
            })
    }

    return update
}

fun ProductAttributeUpdate.getValueString(): Any =
    when (this.valueType) {
        ProductAttributeValueType.PLAIN -> JSONTokener(this.value).nextValue()
        ProductAttributeValueType.STRING -> this.value
        ProductAttributeValueType.NUMBER -> this.value.toFloat()
    }

class CategoryRequestFactory {
    companion object {
        fun categoryTree(baseUrl: String, authentication: String) = RequestInfo(
            baseUrl,
            "/rest/V1/categories",
            Method.GET,
            mutableMapOf(),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )

        fun categoryDetailsList(baseUrl: String, authentication: String, pageSize: Int = 300) = RequestInfo(
            baseUrl,
            "/rest/V1/categories/list",
            Method.GET,
            mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )
    }
}
