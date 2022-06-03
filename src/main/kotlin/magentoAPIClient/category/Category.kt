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


fun CategoryBasics.toCsvList(): List<Any> {
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
    val preAttrInfo = this.basic.toCsvList() + listOf(this.includeInMenu)

    val catDetailsList = mutableListOf<List<Any>>()

    catDetailsList.add(
        listOf(this.basic.id,CategoryAttributeType.BASIC.label,CategoryAttribute.PARENT_ID.label,this.basic.parentId)
    )
    catDetailsList.add(
        listOf(this.basic.id,CategoryAttributeType.BASIC.label,CategoryAttribute.NAME.label,this.basic.name)
    )
    catDetailsList.add(
        listOf(this.basic.id,CategoryAttributeType.BASIC.label,CategoryAttribute.IS_ACTIVE.label,this.basic.isActive)
    )
    catDetailsList.add(
        listOf(this.basic.id,CategoryAttributeType.BASIC.label,CategoryAttribute.POSITION.label,this.basic.position)
    )
    catDetailsList.add(
        listOf(this.basic.id,CategoryAttributeType.BASIC.label,CategoryAttribute.LEVEL.label,this.basic.level)
    )

        catDetailsList.addAll(this.customAttributes.map {
            listOf(
                this.basic.id,
                CategoryAttributeType.CUSTOM_ATTRIBUTES.label,
                it.key,
                it.value
            )
        })

    return catDetailsList
}


fun CategoryDetail.Companion.csvHeader(): List<String> {
    return listOf(
        CategoryDetailHeader.ID.label,
        CategoryDetailHeader.TYPE.label,
        CategoryDetailHeader.CODE.label,
        CategoryDetailHeader.VALUE.label
    )
}

enum class CategoryAttribute(val label: String) {
    ID("id"),
    PARENT_ID("parent_id"),
    NAME("name"),
    IS_ACTIVE("is_active"),
    POSITION("position"),
    LEVEL("level"),
    PRODUCT_COUNT("product_count"),
    INCLUDE_IN_MENU("include_in_menu"),
}

enum class CategoryDetailHeader(val label: String) {
    ID("cat.id"), TYPE("cat.attributeType"), CODE("cat.attrCode"), VALUE("cat.attrValue");
}

enum class CategoryAttributeType(val label: String) { BASIC("basic"), CUSTOM_ATTRIBUTES("custom_attributes") }

data class CategoryUpdate(val id: Int, val customAttributes: MutableMap<String, String> = mutableMapOf()) {

    fun toList() = customAttributes.map { listOf(id, CategoryAttributeType.CUSTOM_ATTRIBUTES, it.key, it.value) }

    fun toUpdateJSONObject(): JSONObject {
        val obj = JSONObject()

        obj.put(CategoryAttributeType.CUSTOM_ATTRIBUTES.label, customAttributes.map { attr ->
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