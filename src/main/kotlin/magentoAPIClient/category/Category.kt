package magentoAPIClient.category

import org.apache.commons.csv.CSVPrinter

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