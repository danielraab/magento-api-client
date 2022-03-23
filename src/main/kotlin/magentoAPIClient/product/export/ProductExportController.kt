package magentoAPIClient.product.export

import magentoAPIClient.*
import magentoAPIClient.category.CategoryBasics
import magentoAPIClient.category.CategoryDetail
import magentoAPIClient.category.csvHeader
import magentoAPIClient.category.toCsvString
import magentoAPIClient.http.HttpHelper
import magentoAPIClient.http.ProductRequestFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.IllegalArgumentException
import java.net.http.HttpResponse
import javax.swing.JOptionPane

class ProductExportController(private val base: BaseController, private val view: ProductExportComponent) :
    GuiControllerInterface {

    companion object {
        private const val PRODUCT_QUERY_PAGE_SIZE: Int = 300
    }

    private var config = Configuration()
    private val productList = mutableListOf<FullProduct>()

    override fun initController() {
        view.addBtnActionHandlers({
            this.config = base.updateConfigFromGui(this.config)
            queryHandling(base, refreshTimeoutWhileLoading, {
                queryFullProducts()
            }, {}, {
                updateInfoLabels()
            })
        }, {
            this.config = base.updateConfigFromGui(this.config)
            if (productList.isNotEmpty()) {
                saveDialogHandler(
                    view,
                    createProductCSVList(true),
                    config.encoding.charset
                )
            } else {
                JOptionPane.showMessageDialog(view, "No product to save.")
            }
        })

        updateInfoLabels()
    }

    private fun updateInfoLabels() {
        view.updateInfoLabels(productList.size)
    }


    //region query and parsing list

    private fun queryProductWithPagination(currentPage: Int, pageSize: Int): Int {

        var totalProductCnt = 0
        var httpResponse: HttpResponse<String>? = null

        try {
            httpResponse = HttpHelper(
                ProductRequestFactory.getProductList(
                    config.baseUrl,
                    config.authentication,
                    config.storeView,
                    pageSize,
                    currentPage
                )
            ).sendRequest()

            val jsonRoot = httpResponse.body().toJSONObject()
            val prodArr = jsonRoot.getJSONArray("items")

            prodArr.forEach {
                productList.add(parseProductJsonObject(it as JSONObject))
            }

            totalProductCnt = jsonRoot.getInt("total_count")

        } catch (_: IOException) {
        } catch (e: Exception) {
            println("Wrong status code returned: ${httpResponse?.statusCode()}")
            println(httpResponse?.body())
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
        } catch (_: IOException) {
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                view,
                "An error occurred while querying site.",
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


    //region csv creating

    private fun createProductCSVList(withHeader: Boolean): String {
        val lines = mutableListOf<List<Any>>()
        if (withHeader) {
            lines.add(FullProduct.csvHeader())
        }

        lines.addAll(productList.flatMap { it.toCsvList() })

        return lines.joinToString(System.lineSeparator()) { list -> list.joinToString(config.columnSeparator) { it.toQuoteString() } }

    }

    private fun FullProduct.toCsvList(): List<List<Any>> {
        val lines = mutableListOf<List<Any>>()
        val id = this.id
        val sku = this.simpleAttrMap["sku"] ?: ""
        val name = this.simpleAttrMap["name"] ?: ""

        this.simpleAttrMap.forEach { lines.add(listOf(id, sku, name, "simple", it.key, it.value)) }
        this.customAttrMap.forEach { lines.add(listOf(id, sku, name, "custom", it.key, it.value)) }
        this.extAttrMap.forEach { lines.add(listOf(id, sku, name, "extension", it.key, it.value)) }

        return lines
    }

    private fun Any.toQuoteString() = when (this) {
        is Number -> this.toString()
        else -> this.toString().quote()
    }


    private fun FullProduct.Companion.csvHeader(): List<String> {
        return listOf("id", "sku", "name", "attr.type", "attr.name", "attr.value")
    }

    //endregion
}

