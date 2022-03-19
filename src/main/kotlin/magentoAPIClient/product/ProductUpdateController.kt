package magentoAPIClient.product

import magentoAPIClient.*
import magentoAPIClient.http.HttpHelper
import magentoAPIClient.http.ProductRequestFactory
import org.json.JSONObject
import magentoAPIClient.product.selectionTable.ProductTableModel
import magentoAPIClient.product.selectionTable.ProductSelectionTableJFrame
import org.json.JSONException
import java.awt.EventQueue
import javax.swing.JOptionPane

class ProductUpdateController(private val base: BaseController, private val view: ProductUpdateComponent) {

    private var config = Configuration()

    private val productList = mutableListOf<Product>()


    fun initController() {
        view.addBtnActionHandlers({
            this.config = base.updateConfigFromGui(this.config)
            queryHandling(base, refreshTimeoutWhileLoading, {
                queryProducts()
            }, {}, {
                updateInfoLabels()
            })
        }, {
            EventQueue.invokeLater {
                val tableModel = ProductTableModel(productList)
                tableModel.addTableModelListener { updateInfoLabels() }
                val frame = ProductSelectionTableJFrame("Select products", tableModel)
                frame.initMenu({
                    productList.forEach { it.selected = true }
                    tableModel.productListChanged()
                    updateInfoLabels()
                }, {
                    productList.forEach { it.selected = false }
                    tableModel.productListChanged()
                    updateInfoLabels()
                })
            }
        }, {
            this.config = base.updateConfigFromGui(this.config)
            queryHandling(base, refreshTimeoutWhileLoading, {
                updateProducts()
            }, {}, {
            })
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


    private fun updateProducts() {
        try {
            productList.filter { it.selected }.forEach {
                val httpResponse = HttpHelper(    //TODO
                    ProductRequestFactory.updateProduct(
                        config.baseUrl,
                        config.authentication,
                        config.storeView,
                        it.sku,
                        config.productAttributeUpdateList
                    )
                ).sendRequest()
            }
        } catch (e: JSONException) {
            JOptionPane.showMessageDialog(
                view,
                "Unable to create update message. Check your settings.",
                "error in creating message.",
                JOptionPane.ERROR_MESSAGE
            )
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                view,
                "Unable to create update message. Check your settings.",
                "error in creating message.",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

}