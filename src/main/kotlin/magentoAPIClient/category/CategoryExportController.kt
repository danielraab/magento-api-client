package magentoAPIClient.category

import magentoAPIClient.*
import magentoAPIClient.category.overview.CategoryUpdateOverviewTableModel
import magentoAPIClient.category.overview.CategoryUpdateOverviewWindow
import magentoAPIClient.category.update.CategoryUpdateWindowController
import magentoAPIClient.http.CategoryRequestFactory
import magentoAPIClient.http.HttpHelper
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Component
import java.awt.event.WindowEvent
import java.io.*
import java.lang.IllegalArgumentException
import java.net.http.HttpResponse
import javax.swing.JOptionPane

class CategoryExportController(private val base: BaseController, private val view: CategoryExportComponent) :
    GuiControllerInterface {

    private var config = Configuration()

    private var treeRootCategory: CategoryBasics? = null
    private var categoryDetailsList = mutableListOf<CategoryDetail>()
    private var categoryUpdateList = listOf<CategoryUpdate>()

    private var categoryUpdateWindow:CategoryUpdateOverviewWindow? = null
    private var categoryUpdateController: CategoryUpdateWindowController = CategoryUpdateWindowController(base)

    override fun initController() {
        view.addCategoryTreeBtnHandlers({

            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                queryCategoryTree()
            }, {}, {
                view.updateInfoLabels(treeRootCategory?.recursiveSize() ?: 0)
            })

        }, {
            this.config = base.updateConfigFromGui(this.config)
            saveCategoryTreeToCSV(view)
        })

        view.addCategoryDetailBtnHandlers({
            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                categoryDetailsList.clear()
                queryCategoryDetailsList()
            }, {}, {
                view.updateInfoLabels(categoryListCnt = categoryDetailsList.size)
            })
        }, {
            this.config = base.updateConfigFromGui(this.config)
            saveCategoryDetailListToCSV(view)
        })


        view.addCategoryUpdateBtnHandlers({
            readCategoryUpdatesFromFile()
            view.updateInfoLabels(
                updateCategoryCnt = categoryUpdateList.size,
                updatesCnt = categoryUpdateList.sumOf { it.customAttributes.size })
        }, {
            if(categoryUpdateWindow != null && categoryUpdateWindow!!.isVisible)
                categoryUpdateWindow!!.dispatchEvent(WindowEvent(categoryUpdateWindow, WindowEvent.WINDOW_CLOSING))

            categoryUpdateWindow = CategoryUpdateOverviewWindow("Updates", CategoryUpdateOverviewTableModel(categoryUpdateList))
        }, {
            categoryUpdateController.showWindow(categoryUpdateList)
        })

        view.updateInfoLabels(0, 0, 0, 0)
    }

    private fun readCategoryUpdatesFromFile() {

        try {

            val file = openFileDialogHandler(view) ?: throw FileNotFoundException()

            val csvParser = CSVParser(
                FileReader(file),
                CSVFormat.EXCEL.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                    .withDelimiter(config.columnSeparator)
            )

            categoryUpdateList = parseCSVToCategoryUpdateList(csvParser)
        } catch (_: FileNotFoundException) { }
        catch (iae: IllegalArgumentException) {
            JOptionPane.showMessageDialog(view, "Unable to read csv file.")
        }
    }

    private fun parseCSVToCategoryUpdateList(csvParser: CSVParser): List<CategoryUpdate> {

        val catUpdateList = mutableMapOf<Int, CategoryUpdate>()

        for (csvRecord in csvParser) {
            val id = csvRecord.get(CategoryUpdateHeader.ID.label).toInt()
            val catUpdate = catUpdateList[id] ?: CategoryUpdate(id)

            catUpdateList[catUpdate.id] = addCSVRecordToCategoryUpdate(catUpdate, csvRecord)
        }

        return catUpdateList.values.toList()
    }

    private fun addCSVRecordToCategoryUpdate(catUpdate:CategoryUpdate, csvRecord:CSVRecord): CategoryUpdate {
        when(csvRecord.get(CategoryUpdateHeader.TYPE.label)) {
            CategoryAttributeType.CUSTOM_ATTRIBUTE.label -> catUpdate.customAttributes[csvRecord.get(CategoryUpdateHeader.CODE.label)] = csvRecord.get(CategoryUpdateHeader.VALUE.label)
        }

        return catUpdate
    }


    //region query and parsing list and trees

    private fun queryCategoryTree() {

        var result: HttpResponse<String>? = null
        try {
            result =
                HttpHelper(CategoryRequestFactory.categoryTree(config.baseUrl, config.authentication)).sendRequest()

            val jsonRoot = result.body().toJSONObject()

            treeRootCategory = try {
                parseCategoryJsonObject(jsonRoot)
            } catch (e: Exception) {
                println("failed to parse category response:")
                println(jsonRoot)
                null
            }
        } catch (_: IOException) {
        } catch (e: Exception) {
            println("Unable to read site response.")
            println(result?.body())
            JOptionPane.showMessageDialog(view, "Unable to read site response.")
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


    private fun queryCategoryDetailsList() {
        var httpResponse: HttpResponse<String>? = null
        try {
            httpResponse = HttpHelper(
                CategoryRequestFactory.categoryDetailsList(
                    config.baseUrl,
                    config.authentication
                )
            ).sendRequest()

            val jsonRoot = httpResponse.body().toJSONObject()
            val catArr = jsonRoot.getJSONArray("items")

            catArr.forEach {
                categoryDetailsList.add(parseCategoryDetailJsonObject(it as JSONObject))
            }

            if (catArr.length() != jsonRoot.getInt("total_count")) {
                println("given items are not complete:")
                println(jsonRoot)
            }
        } catch (_: IOException) {
        } catch (e: Exception) {
            println("Unable to read site response.")
            println(httpResponse?.body())
            JOptionPane.showMessageDialog(view, "Unable to read site response.")
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

    //endregion

    //region saving
    private fun saveCategoryDetailListToCSV(parent: Component) {
        if (categoryDetailsList.isNotEmpty()) {
            saveDialogHandler(
                view,
                createCategoryDetailsListCSV(),
                config.encoding.charset
            )
        } else {
            JOptionPane.showMessageDialog(parent, "No category details list to save.")
        }
    }

    private fun createCategoryDetailsListCSV(): String {
        val bld = StringBuilder()
        val csvPrinter = CSVPrinter(
            bld,
//            CSVFormat.EXCEL.withHeader(*(CategoryDetail.csvHeader().toTypedArray())).withDelimiter(config.columnSeparator)
            CSVFormat.EXCEL.withDelimiter(config.columnSeparator)
        )

        csvPrinter.printRecord(CategoryDetail.csvHeader())

        categoryDetailsList.forEach { csvPrinter.printRecords(it.toCsvList()) }

        csvPrinter.flush()
        csvPrinter.close()

        return bld.toString()
    }


    private fun saveCategoryTreeToCSV(view: CategoryExportComponent) {
        if (treeRootCategory != null) {
            saveDialogHandler(
                view,
                createCategoryTreeCSV(),
                config.encoding.charset
            )
        } else {
            JOptionPane.showMessageDialog(view, "No category tree to save.")
        }
    }

    private fun createCategoryTreeCSV(): String {
        val bld = StringBuilder()
        val csvPrinter = CSVPrinter(bld, CSVFormat.EXCEL.withDelimiter(config.columnSeparator))

        treeRootCategory?.recursiveCsvTree(csvPrinter)

        csvPrinter.flush()
        csvPrinter.close()

        return bld.toString()
    }


    //endregion


}