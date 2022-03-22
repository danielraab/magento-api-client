package magentoAPIClient.product.export

import magentoAPIClient.*
import magentoAPIClient.http.HttpHelper
import magentoAPIClient.http.ProductRequestFactory
import org.json.JSONArray
import org.json.JSONObject
import java.lang.IllegalArgumentException
import javax.swing.JOptionPane

class ProductExportController(private val base: BaseController, private val view: ProductExportComponent) :
    GuiControllerInterface {

    companion object {
        private const val PRODUCT_QUERY_PAGE_SIZE: Int = 300
    }

    private var config = Configuration()
    private val productList = mutableListOf<FullProduct>()

    override fun initController() {
        view.addBtnActionHandlers ({
            this.config = base.updateConfigFromGui(this.config)
            queryHandling(base, refreshTimeoutWhileLoading, {
                queryFullProducts()
            }, {}, {
                updateInfoLabels()
            })
        },{
            TODO()
        })

        updateInfoLabels()
    }

    private fun updateInfoLabels() {
        view.updateInfoLabels(productList.size)
    }


    //region query and parsing list

    private fun queryProductWithPagination(currentPage: Int, pageSize: Int): Int {

        var totalProductCnt = 0

        val httpResponse = HttpHelper(
            ProductRequestFactory.getProductList(
                config.baseUrl,
                config.authentication,
                config.storeView,
                pageSize,
                currentPage
            )
        ).sendRequest()

        if (httpResponse.statusCode() == 200) {
            val jsonRoot = httpResponse.body().toJSONObject()
            val prodArr = jsonRoot.getJSONArray("items")

            prodArr.forEach {
                productList.add(parseProductJsonObject(it as JSONObject))
            }

            totalProductCnt = jsonRoot.getInt("total_count")

        } else {
            println("Wrong status code returned: ${httpResponse.statusCode()}")
            println(httpResponse.body())
            throw IllegalArgumentException()
        }
        return totalProductCnt
    }

    private fun queryFullProducts() {
        productList.clear()

        var curPage = 1
        try {
            var totalCnt = queryProductWithPagination(curPage, PRODUCT_QUERY_PAGE_SIZE)
            while (totalCnt > curPage * PRODUCT_QUERY_PAGE_SIZE) {
                curPage++
                totalCnt = queryProductWithPagination(curPage, PRODUCT_QUERY_PAGE_SIZE)
            }
            if (totalCnt != productList.size) {

                JOptionPane.showMessageDialog(
                    view,
                    "There are inconsistencies while reading products. (read ${productList.size} of total $totalCnt)",
                    "Inconsistent read",
                    JOptionPane.WARNING_MESSAGE
                )
            }
        } catch (e: IllegalArgumentException) {
            JOptionPane.showMessageDialog(
                view,
                "Unable to read site response.",
                "error in reading site",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun parseProductJsonObject(jsonObject: JSONObject): FullProduct {
        val newFullProd = FullProduct(jsonObject.getInt("id"))

        jsonObject.keys().forEach { outerAttr ->
            when (outerAttr) {
                "custom_attributes" -> (jsonObject.get(outerAttr) as JSONArray).forEach {
                    newFullProd.customAttrMap[(it as JSONObject).getString("attribute_code")] = it.get("value")
                }
                "extension_attributes" -> {
                    val extAttrObj = (jsonObject.get(outerAttr) as JSONObject)
                    extAttrObj.keys().forEach {
                        newFullProd.extAttrMap[it] = extAttrObj.get(it)
                    }
                }
                else -> newFullProd.simpleAttrMap[outerAttr] = jsonObject.get(outerAttr)
            }
        }
        return newFullProd
    }

    //endregion
}