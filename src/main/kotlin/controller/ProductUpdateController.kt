package controller

import gui.panel.ProductUpdateComponent
import magentoAPIClient.*
import model.*
import org.json.JSONObject
import javax.swing.JOptionPane

class ProductUpdateController(private val base: BaseController, private val view: ProductUpdateComponent) {

    private var config = Configuration()

    private val productList = mutableListOf<Product>()

    fun initController() {
        view.addBtnActionHandlers {

            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                queryProducts()
            }, {}, {
                view.updateInfoLabels(productList.size)
            })

        }

        view.updateInfoLabels(0)
    }


    //region query and parsing list

//    private fun parseCategoryJsonObject(json: JSONObject): CategoryBasics {
//        val cat = CategoryBasics(
//            json.getInt("id"),
//            json.getInt("parent_id"),
//            json.getString("name"),
//            json.getBoolean("is_active", false),
//            json.getInt("position"),
//            json.getInt("level"),
//            json.getInt("product_count", 0)
//        )
//
//        val children = json.getJSONArray("children_data", JSONArray())
//
//        children.forEach {
//            cat.children.add(parseCategoryJsonObject(it as JSONObject))
//        }
//        return cat
//    }

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
            JOptionPane.showMessageDialog(view, "Unable to read site response.")
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