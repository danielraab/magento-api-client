package controller

import gui.BasicWindow
import gui.CategoryExportComponent
import magentoAPIClient.*
import model.*
import org.json.JSONArray
import org.json.JSONObject
import java.awt.EventQueue
import java.time.zone.ZoneRulesProvider
import javax.swing.JFrame
import javax.swing.JOptionPane

class CategoryExportController(private val base: BaseController, private val view: CategoryExportComponent) {

    private var config = Configuration()

    private var treeRootCategory: CategoryBasics? = null
    private var categoryDetailsList = mutableListOf<CategoryDetail>()

    fun initController() {
        view.addBtnActionHandlers({

            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                queryCategoryTree()
            }, {}, {
                view.updateInfoLabels(treeRootCategory?.recursiveSize() ?: 0)
            })

        }, {}, {  //TODO
            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                categoryDetailsList.clear()
                queryCategoryDetailsList()
            }, {}, {
                view.updateInfoLabels(categoryListCnt = categoryDetailsList.size)
            })
        }, {}) //TODO

        view.updateInfoLabels(0, 0)
    }

    private fun queryCategoryTree() {

        val result =
            HttpHelper(CategoryRequestFactory.categoryTree(config.baseUrl, config.authentication)).sendRequest()
        val jsonRoot = result.toJSONObject()

        treeRootCategory = try {
            parseCategoryJsonObject(jsonRoot)
        } catch (e: Exception) {
            println("failed to parse category response:")
            println(jsonRoot)
            null
        }
    }

    private fun parseCategoryJsonObject(json: JSONObject): CategoryBasics {
        val cat = CategoryBasics(
            json.getInt("id"),
            json.getInt("parent_id"),
            json.getString("name"),
            json.getBoolean("is_active", false),
            json.getInt("position"),
            json.getInt("level"),
            json.getInt("product_count", 0)
        )

        val children = json.getJSONArray("children_data", JSONArray())

        children.forEach {
            cat.children.add(parseCategoryJsonObject(it as JSONObject))
        }
        return cat
    }

    private fun JSONObject.getBoolean(key: String, default: Boolean) =
        if (this.has(key)) this.getBoolean(key) else default

    private fun JSONObject.getInt(key: String, default: Int) =
        if (this.has(key)) this.getInt(key) else default

    private fun JSONObject.getJSONArray(key: String, default: JSONArray) =
        if(this.has(key)) this.getJSONArray("children_data") else default

    private fun queryCategoryDetailsList() {
        val result =
            HttpHelper(CategoryRequestFactory.categoryDetailsList(config.baseUrl, config.authentication)).sendRequest()
        val jsonRoot = result.toJSONObject()
        val catArr = jsonRoot.getJSONArray("items")

        catArr.forEach {
            categoryDetailsList.add(parseCategoryDetailJsonObject(it as JSONObject))
        }

        if (catArr.length() != jsonRoot.getInt("total_count")) {
            println("given items are not complete:")
            println(jsonRoot)
        }
    }

    private fun parseCategoryDetailJsonObject(jsonObject: JSONObject): CategoryDetail {
        val basic = parseCategoryJsonObject(jsonObject)
        val detail = CategoryDetail(basic, jsonObject.getBoolean("include_in_menu", false))

        val customAttributeArr = jsonObject.getJSONArray("custom_attributes")
        customAttributeArr.forEach {
            detail.customAttributes[(it as JSONObject).getString("attribute_code")] = it.getString("value")
        }

        return detail
    }
}