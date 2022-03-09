package magentoAPIClient

class ProductRequestFactory {
    companion object {

        fun listAttributeSets(baseUrl: String, authentication: String, pageSize: Int = 300) = RequestInfo(
            baseUrl,
            "/rest/all/V1/products/attribute-sets/sets/list",
            Method.GET,
            mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )


        fun listAttributes(baseUrl: String, authentication: String, pageSize: Int = 300) = RequestInfo(
            baseUrl,
            "/rest/all/V1/products/attributes",
            Method.GET,
            mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )


        fun attributesOfAttributeSet(baseUrl: String, authentication: String, attrSetId: Int) = RequestInfo(
            baseUrl,
            "/rest/all/V1/products/attribute-sets/${attrSetId}/attributes",
            Method.GET,
            mutableMapOf(),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )
    }
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
