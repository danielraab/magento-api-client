package magentoAPIClient.http

class ProductRequestFactory {
    companion object {

        fun listAttributeSets(baseUrl: String, authentication: String, storeView: String="all", pageSize: Int = 300) = RequestInfo(
            baseUrl,
            "/rest/${storeView}/V1/products/attribute-sets/sets/list",
            Method.GET,
            mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )


        fun listAttributes(baseUrl: String, authentication: String, storeView: String="all", pageSize: Int = 300) = RequestInfo(
            baseUrl,
            "/rest/${storeView}/V1/products/attributes",
            Method.GET,
            mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )


        fun attributesOfAttributeSet(baseUrl: String, authentication: String, storeView: String="all", attrSetId: Int) = RequestInfo(
            baseUrl,
            "/rest/${storeView}/V1/products/attribute-sets/${attrSetId}/attributes",
            Method.GET,
            mutableMapOf(),
            mutableMapOf(Header.AUTHORIZATION to authentication)
        )




        fun getProductList(baseUrl: String, authentication: String, storeView:String, pageSize: Int = 300) = RequestInfo(
            baseUrl,
            "/rest/${storeView}/V1/products",
            Method.GET,
            mutableMapOf("searchCriteria[pageSize]" to pageSize.toString()),
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
