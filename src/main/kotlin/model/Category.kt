package model

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


fun CategoryBasics.toCsvString(columnSeparator: String): String {
    return listOf(
        this.id,
        this.parentId,
        this.name.quote(),
        this.isActive,
        this.position,
        this.level,
        this.productCnt,
        this.children.size
    ).joinToString(columnSeparator)
}

fun CategoryBasics.toLevelBasedCsvTree(columnSeparator: String): String {
    val cols = mutableListOf<String>()
    repeat(this.level-1) {cols.add("")}
    cols.add(this.id.toString())
    cols.add(this.name.quote())
    return cols.joinToString(columnSeparator)
}

fun CategoryBasics.recursiveCsvTree(columnSeparator: String, treeList: MutableList<String>){
    treeList.add(this.toLevelBasedCsvTree(columnSeparator))
    this.children.forEach { it.recursiveCsvTree(columnSeparator, treeList) }
}


fun CategoryDetail.toCsvString(columnSeparator: String): List<String> {
    val preAttrInfo = listOf(
        this.basic.toCsvString(columnSeparator),
        this.includeInMenu
    ).joinToString(columnSeparator)

    val catDetailsList = mutableListOf<String>()

    if (this.customAttributes.isEmpty())
        catDetailsList.add(listOf(preAttrInfo, "", "").joinToString(columnSeparator))
    else
        catDetailsList.addAll(this.customAttributes.map {
            listOf(preAttrInfo, it.key.quote(), it.value.quote()).joinToString(columnSeparator)
        })

    return catDetailsList
}


fun CategoryBasics.Companion.csvHeader(columnSeparator: String): String {
    return listOf(
        "cat.id".quote(),
        "cat.parentId".quote(),
        "cat.name".quote(),
        "cat.isActive".quote(),
        "cat.position".quote(),
        "cat.level".quote(),
        "cat.productCnt".quote(),
        "cat.childrenCnt".quote()
    ).joinToString(columnSeparator)
}

fun CategoryDetail.Companion.csvHeader(columnSeparator: String): String {
    return listOf(
        CategoryBasics.csvHeader(columnSeparator),
        "cat.includeInMenu".quote(),
        "cat.customAttrCode".quote(),
        "cat.customAttrValue".quote()
    ).joinToString(columnSeparator)
}