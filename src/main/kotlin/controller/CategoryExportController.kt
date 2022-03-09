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

class CategoryExportController(private val base:BaseController, private val view: CategoryExportComponent) {

    private var config = Configuration()

    private var treeRootCategory:CategoryBasics? = null
    private var categoryDetailsList = mutableListOf<CategoryBasics>()

    fun initController() {
        view.addBtnActionHandlers({

            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                queryCategoryTree()
            },{}, {
                view.updateInfoLabels(treeRootCategory?.recursiveSize()?:0)
            })

        },{},{  //TODO
            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                queryCategoryDetailsList()
            },{}, {
                view.updateInfoLabels(treeRootCategory?.recursiveSize()?:0)
            })
        },{}) //TODO

        view.updateInfoLabels(0, 0)
    }

    private fun queryCategoryTree() {

        val result = HttpHelper(CategoryRequestFactory.categoryTree(config.baseUrl, config.authentication)).sendRequest()
        val jsonRoot = result.toJSONObject()

        treeRootCategory = try {
            parseCategoryJsonObject(jsonRoot)
        } catch (e:Exception) {
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
            json.getBoolean("is_active"),
            json.getInt("position"),
            json.getInt("level"),
            json.getInt("product_count")
        )

        val children = json.getJSONArray("children_data")

        children.forEach {
            cat.children.add(parseCategoryJsonObject(it as JSONObject))
        }
        return cat
    }



    private fun queryCategoryDetailsList() {

        val result = HttpHelper(CategoryRequestFactory.categoryDetailsList(config.baseUrl, config.authentication)).sendRequest()
        val jsonRoot = result.toJSONObject()
        val catArr = jsonRoot.getJSONArray("items")




        if (catArr.length() != jsonRoot.getInt("total_count")) {
            println("given items are not complete:")
            println(jsonRoot)
        }
    }
}