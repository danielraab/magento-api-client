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
}

data class CategoryDetail(
    val basic: CategoryBasics,
    val includeInMenu: Boolean,
    val customAttributes: MutableMap<String, String> = mutableMapOf()
)