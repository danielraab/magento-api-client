package magentoAPIClient.product

import magentoAPIClient.*
import magentoAPIClient.http.HttpHelper
import magentoAPIClient.http.ProductRequestFactory
import org.json.JSONObject
import magentoAPIClient.product.selectionTable.ProductSelectionTableModel
import magentoAPIClient.product.selectionTable.ProductSelectionTableJFrame
import magentoAPIClient.product.updateWindow.ProductUpdateController
import magentoAPIClient.product.updateWindow.ProductUpdateWindow
import org.json.JSONException
import java.awt.EventQueue
import java.net.http.HttpResponse
import javax.swing.JOptionPane
import javax.swing.SwingWorker

class ProductController(val base: BaseController, val view: ProductComponent) {

    private var config = Configuration()
    private val productList = mutableListOf<Product>()

    private var productSelectorFrame: ProductSelectionTableJFrame? = null
    private var productUpdateController: ProductUpdateController = ProductUpdateController(base)


    fun initController() {
        view.addBtnActionHandlers({
            this.config = base.updateConfigFromGui(this.config)
            queryHandling(base, refreshTimeoutWhileLoading, {
                queryProducts()
            }, {}, {
                updateInfoLabels()
            })
        }, {
            if (productSelectorFrame == null || !productSelectorFrame!!.isVisible) {
                EventQueue.invokeLater {
                    val tableModel = ProductSelectionTableModel(productList)
                    tableModel.addTableModelListener { updateInfoLabels() }
                    productSelectorFrame = ProductSelectionTableJFrame("Select products", tableModel)
                    productSelectorFrame!!.initMenu({
                        productList.forEach { it.selected = true }
                        tableModel.productListChanged()
                        updateInfoLabels()
                    }, {
                        productList.forEach { it.selected = false }
                        tableModel.productListChanged()
                        updateInfoLabels()
                    })
                }
            } else {
                productSelectorFrame!!.toFront()
            }
        }, {
            productUpdateController.showWindow(productList.filter { it.selected })
        })

        updateInfoLabels()
    }

    private fun updateInfoLabels() {
        view.updateInfoLabels(productList.size, productList.filter { it.selected }.size)
    }


    //region query and parsing list

    private fun queryProducts() {
        productList.clear()

        val httpResponse = HttpHelper(
            ProductRequestFactory.getProductList(
                config.baseUrl,
                config.authentication,
                config.storeView
            )
        ).sendRequest()
        if (httpResponse.statusCode() == 200) {
            val jsonRoot = httpResponse.body().toJSONObject()
            val prodArr = jsonRoot.getJSONArray("items")

            prodArr.forEach {
                productList.add(parseProductJsonObject(it as JSONObject))
            }

            if (prodArr.length() != jsonRoot.getInt("total_count")) {
                println("given items are not complete:")
                println(jsonRoot)
            }
        } else {
            println("Unable to read site response.")
            println(httpResponse.body())
            JOptionPane.showMessageDialog(
                view,
                "Unable to read site response.",
                "error in reading site",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun parseProductJsonObject(jsonObject: JSONObject): Product {
        return Product(
            jsonObject.getInt("id"),
            jsonObject.getString("sku"),
            jsonObject.getString("name"),
            jsonObject.getInt("status"),
            jsonObject.getString("type_id")
        )
    }

    //endregion
}