package magentoAPIClient.category

import org.apache.commons.csv.CSVPrinter
import org.json.JSONObject

data class CategoryBasics(
    val id: Int,
    val parentId: Int,
    val name: String,
    val isActive: Boolean,
    val position: Int,
    val level: Int,
    val productCnt: Int,
    val children: MutableList<CategoryBasics> = mutableListOf()
) {
    fun recursiveSize(): Int {
        return children.sumOf { it.recursiveSize() } + 1
    }

    companion object
}

data class CategoryDetail(
    val basic: CategoryBasics,
    val includeInMenu: Boolean,
    val customAttributes: MutableMap<String, String> = mutableMapOf()
) {
    companion object
}


fun CategoryBasics.toCsv(): List<Any> {
    return listOf(
        this.id,
        this.parentId,
        this.name,
        this.isActive,
        this.position,
        this.level,
        this.productCnt,
        this.children.size
    )
}

fun CategoryBasics.toLevelBasedCsvTree(): List<String> {
    val cols = mutableListOf<String>()
    repeat(this.level - 1) { cols.add("") }
    cols.add(this.id.toString())
    cols.add(this.name)
    return cols
}

fun CategoryBasics.recursiveCsvTree(csvPrinter: CSVPrinter) {
    csvPrinter.printRecord(this.toLevelBasedCsvTree())
    this.children.forEach { it.recursiveCsvTree(csvPrinter) }
}


fun CategoryDetail.toCsvList(): List<List<Any>> {
    val preAttrInfo = this.basic.toCsv() + listOf(this.includeInMenu)

    val catDetailsList = mutableListOf<List<Any>>()

    if (this.customAttributes.isEmpty()) {
        catDetailsList.add(preAttrInfo + listOf("", ""))
    } else {
        catDetailsList.addAll(this.customAttributes.map {
            preAttrInfo + listOf(it.key, it.value)
        })
    }

    return catDetailsList
}


fun CategoryBasics.Companion.csvHeader(): List<String> {
    return listOf(
        "cat.id",
        "cat.parentId",
        "cat.name",
        "cat.isActive",
        "cat.position",
        "cat.level",
        "cat.productCnt",
        "cat.childrenCnt"
    )
}

fun CategoryDetail.Companion.csvHeader(): List<String> {
    return CategoryBasics.csvHeader() +
            listOf(
                "cat.includeInMenu",
                "cat.customAttrCode",
                "cat.customAttrValue"
            )
}


enum class CategoryUpdateHeader(val label: String) {
    ID("cat.id"), TYPE("cat.attributeType"), CODE("cat.attrCode"), VALUE("cat.attrValue");
}

enum class CategoryAttributeType(val label: String) { CUSTOM_ATTRIBUTE("custom_attribute") }

data class CategoryUpdate(val id: Int, val customAttributes: MutableMap<String, String> = mutableMapOf()) {

    fun toList() = customAttributes.map { listOf(id, CategoryAttributeType.CUSTOM_ATTRIBUTE, it.key, it.value) }

    fun toUpdateJSONObject(): JSONObject {
        val obj = JSONObject();

        obj.put("custom_attributes", customAttributes.map { attr ->
            JSONObject().also {
                it.put("attribute_code", attr.key)
                it.put("value", attr.value)
            }
        })
        return obj
    }

    companion object {
        fun getColumnsClass() = arrayOf(
            Int::class.javaObjectType,
            String::class.javaObjectType,
            String::class.javaObjectType,
            String::class.javaObjectType
        )
    }
}