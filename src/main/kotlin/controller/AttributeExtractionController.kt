package controller

import gui.panel.AttributeExtractionComponent
import magentoAPIClient.*
import model.*
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Component
import javax.swing.JOptionPane

class AttributeExtractionController(private val base: BaseController, private val view: AttributeExtractionComponent) {

    private var attributeSets = mutableMapOf<Int, AttributeSet>()
    private var attributes = mutableMapOf<Int, Attribute>()
    private var attributeOptions = mutableSetOf<AttributeOption>() //the id is not unique

    private var config = Configuration()

    fun initController() {

        view.addQueryAPIBtnAction {
            queryHandling(base, refreshTimeoutWhileLoading, {
                this.config = base.updateConfigFromGui(this.config)
                queryShop()
            }, {
                view.updateInfoLabels(attributeSets.size, attributes.size, attributeOptions.size)
            },{})
        }

        view.addSaveAttributeSetsBtnAction { saveAttributeSetsToCSV(view) }
        view.addSaveAttributesBtnAction { saveAttributesToCSV(view) }
        view.addSaveAttributesWithOptionsBtnAction { saveAttributesWithOptionsToCSV(view) }
        view.addAllBtnAction { saveAllToCSV(view) }

    }

    private fun clearMaps() {
        attributeSets.clear()
        attributes.clear()
        attributeOptions.clear()
    }


    //region query functions

    private fun queryShop() {
        try {
            clearMaps()
            queryAttributeSets()
            queryAllAttributes()
            queryAttributeFromSets()

        } catch (e: Exception) {
            println("unable to query given url")
            JOptionPane.showMessageDialog(view, "unable to query given url.")
        }
    }

    private fun queryAttributeSets() {

        val result = HttpHelper(ProductRequestFactory.listAttributeSets(config.baseUrl, config.authentication)).sendRequest()
        val jsonRoot = result.body().toJSONObject()


        val itemsArr = jsonRoot.getJSONArray("items")

        itemsArr.forEach {
            if (it is JSONObject) {
                val id = it.get("attribute_set_id").toString().toInt()
                if (attributeSets.containsKey(id)) {
                    println("AttributeSet: $it already exists")
                } else {
                    attributeSets[id] = AttributeSet(id, it.get("attribute_set_name").toString())
                }
            }
        }
        if (itemsArr.length() != jsonRoot.getInt("total_count")) {
            println("given items are not complete:")
            println(jsonRoot)
        }
    }


    private fun queryAllAttributes() {

        val result = HttpHelper(ProductRequestFactory.listAttributes(config.baseUrl, config.authentication)).sendRequest()
        val jsonRoot = result.body().toJSONObject()

        val itemArr = jsonRoot.getJSONArray("items")
        itemArr.forEach { attrJsonObj ->
            if (attrJsonObj is JSONObject) {
                try {
                    parseJSONAttributeObject(attrJsonObj)
                } catch (e: Exception) {
                    println("given object was no valid attribute json:")
                    println(attrJsonObj)
                }
            } else {
                println("given object was no json object:")
                println(attrJsonObj)
            }
        }

        if (itemArr.length() != jsonRoot.getInt("total_count")) {
            println("given items are not complete:")
            println(jsonRoot)
        }

    }

    private fun queryAttributeFromSets() {

        attributeSets.forEach { attrSet ->

            val result =
                HttpHelper(ProductRequestFactory.attributesOfAttributeSet(config.baseUrl, config.authentication, attrSetId=attrSet.key)).sendRequest()

            result.body().toJSONArray().forEach { attrJsonObj ->
                if (attrJsonObj is JSONObject) {
                    try {
                        val attribute = parseJSONAttributeObject(attrJsonObj)
                        attrSet.value.attributes[attribute.id] = attribute
                    } catch (e: Exception) {
                        println("given object was no valid attribute json:")
                        println(attrJsonObj)
                    }
                } else {
                    println("given object was no json object:")
                    println(attrJsonObj)
                }
            }
        }
    }

    private fun parseJSONAttributeObject(jsonObject: JSONObject): Attribute {
        val id = jsonObject.getInt("attribute_id")
        var attribute = attributes[id]
        if (attribute == null) {
            try {
                attribute = jsonObject.toAttributeObject()
                attributes[id] = attribute
            } catch (e: Exception) {
                println("missing attribute in:")
                println(jsonObject)
                throw e
            }
        }
        return attribute
    }

    //endregion


    //region csv generating functions

    private fun saveAttributeSetsToCSV(parent: Component) {
        if (attributeSets.isNotEmpty()) {
            saveDialogHandler(
                parent,
                createCSVString(
                    withHeader = true,
                    withAttributeSet = true,
                    withAttributes = false,
                    withOptions = false
                ),
                config.encoding.charset
            )
        } else {
            JOptionPane.showMessageDialog(parent, "No attribute set to save.")
        }
    }

    private fun saveAttributesToCSV(parent: Component) {
        if (attributeSets.isNotEmpty()) {
            saveDialogHandler(
                parent,
                createCSVString(
                    withHeader = true,
                    withAttributeSet = false,
                    withAttributes = true,
                    withOptions = false
                ),
                config.encoding.charset
            )
        } else {
            JOptionPane.showMessageDialog(parent, "No attributes to save.")
        }
    }

    private fun saveAttributesWithOptionsToCSV(parent: Component) {
        if (attributes.isNotEmpty()) {
            saveDialogHandler(
                parent,
                createCSVString(withHeader = true, withAttributeSet = false, withAttributes = true, withOptions = true),
                config.encoding.charset
            )
        } else {
            JOptionPane.showMessageDialog(parent, "No attribute options to save.")
        }
    }

    private fun createCSVString(
        withHeader: Boolean,
        withAttributeSet: Boolean,
        withAttributes: Boolean,
        withOptions: Boolean
    ): String {
        val csvContent = mutableListOf<String>()
        if (withHeader) {
            val headerList = mutableListOf<String>()
            if (withAttributeSet) headerList.add(AttributeSet.csvHeader(config.columnSeparator))
            if (withAttributes) headerList.add(Attribute.csvHeader(config.columnSeparator))
            if (withOptions) headerList.add(AttributeOption.csvHeader(config.columnSeparator))
            csvContent.add(headerList.joinToString(config.columnSeparator))
        }

        if (withAttributeSet) {
            val csvContentList = mutableListOf<String>()
            csvContentList.addAll(attributeSets.flatMap { (_, attrSet) ->
                val attrSetCsv = attrSet.toCsvString(config.columnSeparator)
                if (withAttributes) {
                    if (attrSet.attributes.isEmpty()) {
                        attrSetCsv.prefixForList(listOf(Attribute.emptyCsvString(config.columnSeparator, withOptions)))
                    } else {
                        attrSetCsv.prefixForList(attrSet.attributes.toCSVList(withOptions))
                    }
                } else {
                    listOf(attrSetCsv)
                }
            })

            if (withAttributes) {
                csvContentList.addAll(
                    AttributeSet.emptyCsvString(config.columnSeparator).prefixForList(attributes.filterNot {
                        attributeSets.flatMap { set -> set.value.attributes.keys }.contains(it.key)
                    }.toCSVList(withOptions))
                )
            }
            csvContent.addAll(csvContentList)
        } else if (withAttributes) {
            csvContent.addAll(attributes.toCSVList(withOptions))
        } else if (withOptions) {
            csvContent.addAll(attributeOptions.map { it.toCsvString(config.columnSeparator) })
        }
        return csvContent.joinToString(System.lineSeparator())
    }

    private fun saveAllToCSV(parent: Component) {
        if (attributeSets.isNotEmpty() || attributes.isNotEmpty()) {

            saveDialogHandler(
                parent,
                createCSVString(withHeader = true, withAttributeSet = true, withAttributes = true, withOptions = true),
                config.encoding.charset
            )
        } else {
            JOptionPane.showMessageDialog(parent, "Nothing to save.")
        }
    }

    //endregion

    //region csv generating extension function

    private fun Map<Int, Attribute>.toCSVList(withOptions: Boolean = false): List<String> {
        return this.flatMap { (_, attr) ->
            attr.toCSVList(withOptions)
        }
    }

    private fun Attribute.toCSVList(withOptions: Boolean): List<String> {
        val baseString = this.toCsvString(config.columnSeparator)
        return if (withOptions) {
            if (this.options.isEmpty()) {
                baseString.prefixForList(listOf(AttributeOption.emptyCsvString(config.columnSeparator)))
            } else {
                baseString.prefixForList(this.options.map { it.value.toCsvString(config.columnSeparator) })
            }
        } else
            listOf(baseString)
    }

    private fun String.prefixForList(list: List<String>) =
        list.map { joinStrings(config.columnSeparator, this, it) }

    //endregion


    //region attribute json methods

    private fun JSONObject.toAttributeOptionObject() = AttributeOption(
        this.getString("value"), this.getString("label")
    )

    private fun JSONArray.toAttributeOptionList(): List<AttributeOption> = this.mapNotNull { optionJsonObj ->
        if (optionJsonObj is JSONObject) {
            try {
                return@mapNotNull optionJsonObj.toAttributeOptionObject()
            } catch (e: Exception) {
                println("given object was no valid attribute option json:")
                println(optionJsonObj)
            }
        } else {
            println("given object was no json object:")
            println(optionJsonObj)
        }
        return@mapNotNull null
    }

    private fun JSONObject.toAttributeObject(): Attribute {

        val attribute = Attribute(
            this.getInt("attribute_id"),
            this.getString("attribute_code"),
            this.getString("scope"),
            this.get("frontend_input").toString(),
            this.getInt("entity_type_id"),
            this.getBoolean("is_required"),
            this.getBoolean("is_user_defined")
        )
        if (this.has("default_frontend_label")) attribute.defaultFrontendLabel =
            this.getString("default_frontend_label")

        val optionArr = this.get("options") as JSONArray
        val optionList = optionArr.toAttributeOptionList()
        attribute.options.putAll(optionList.associateBy { it.valueId })

        attributeOptions.addAll(optionArr.toAttributeOptionList())

        return attribute
    }

    //endregion

}