package controller

import gui.panel.ProductUpdateComponent
import magentoAPIClient.*
import model.*
import javax.swing.JOptionPane

class ProductUpdateController(private val base: BaseController, private val view: ProductUpdateComponent) {

    private var config = Configuration()


    fun initController() {
        view.addBtnActionHandlers {

            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                queryProducts()   //TODO
            }, {}, {
//                view.updateInfoLabels(treeRootCategory?.recursiveSize() ?: 0)
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
//        val httpResponse = HttpHelper(
//                ProductRequestFactory.getProductList(
//                    config.baseUrl,
//                    config.authentication,
//
//                )
//            ).sendRequest()
//        if (httpResponse.statusCode() == 200) {
//            val jsonRoot = httpResponse.body().toJSONObject()
//            val catArr = jsonRoot.getJSONArray("items")
//
//            catArr.forEach {
//                categoryDetailsList.add(parseCategoryDetailJsonObject(it as JSONObject))
//            }
//
//            if (catArr.length() != jsonRoot.getInt("total_count")) {
//                println("given items are not complete:")
//                println(jsonRoot)
//            }
//        } else {
//            println("Unable to read site response.")
//            println(httpResponse.body())
//            JOptionPane.showMessageDialog(view, "Unable to read site response.")
//        }
    }

//    private fun parseCategoryDetailJsonObject(jsonObject: JSONObject): CategoryDetail {
//        val basic = parseCategoryJsonObject(jsonObject)
//        val detail = CategoryDetail(basic, jsonObject.getBoolean("include_in_menu", false))
//
//        val customAttributeArr = jsonObject.getJSONArray("custom_attributes")
//        customAttributeArr.forEach {
//            detail.customAttributes[(it as JSONObject).getString("attribute_code")] = it.getString("value")
//        }
//
//        return detail
//    }

    //endregion

}