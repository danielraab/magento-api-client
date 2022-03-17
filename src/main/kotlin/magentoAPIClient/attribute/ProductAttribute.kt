package magentoAPIClient.attribute

import magentoAPIClient.quote


data class AttributeOption(val valueId:String, val label: String) {companion object} //the id is not unique

data class Attribute(
    val id: Int,
    val code: String,
    val scope: String,
    val frontendInput: String,
    val entityTypeID: Int,
    val isRequired: Boolean,
    val isUserDefined: Boolean,
    var defaultFrontendLabel: String = "",
    val options: MutableMap<String, AttributeOption> = mutableMapOf()
) {companion object}

data class AttributeSet(val id: Int, val name: String, val attributes:MutableMap<Int, Attribute> = mutableMapOf()) {companion object}



//region csv extension functions

fun AttributeSet.Companion.csvHeader(columnSeparator:String): String {
    return listOf(
        "aSet.id".quote(), "aSet.name".quote()
    ).joinToString(columnSeparator)
}

fun AttributeSet.Companion.emptyCsvString(columnSeparator:String): String {
    return listOf(
        "".quote(), "".quote()
    ).joinToString(columnSeparator)
}

fun AttributeSet.toCsvString(columnSeparator:String): String {
    return listOf(this.id, this.name.quote()).joinToString(columnSeparator)
}

fun Attribute.Companion.csvHeader(columnSeparator:String): String {
    return listOf(
        "attr.id".quote(),
        "attr.code".quote(),
        "attr.scope".quote(),
        "attr.frontendInput".quote(),
        "attr.entityTypeId".quote(),
        "attr.isRequired".quote(),
        "attr.isUserDefined".quote(),
        "attr.defaultFrontendLabel".quote()
    ).joinToString(columnSeparator)
}

fun Attribute.toCsvString(columnSeparator:String): String {
    return listOf(
        this.id,
        this.code.quote(),
        this.scope.quote(),
        this.frontendInput.quote(),
        this.entityTypeID,
        this.isRequired,
        this.isUserDefined,
        this.defaultFrontendLabel.quote()
    ).joinToString(columnSeparator)
}
fun Attribute.Companion.emptyCsvString(columnSeparator:String, withOptions:Boolean = false): String {
    val columnList = mutableListOf<String>()
    repeat(8) {
        columnList.add("".quote())
    }
    if(withOptions) columnList.add(AttributeOption.emptyCsvString(columnSeparator))
    return columnList.joinToString(columnSeparator)
}

fun AttributeOption.Companion.csvHeader(columnSeparator:String): String {
    return listOf(
        "option.valueId".quote(), "option.label".quote()
    ).joinToString(columnSeparator)
}

fun AttributeOption.toCsvString(columnSeparator:String): String {
    return listOf(
        this.valueId.quote(), this.label.quote()
    ).joinToString(columnSeparator)
}
fun AttributeOption.Companion.emptyCsvString(columnSeparator:String): String {
    return listOf(
        "".quote(), "".quote()
    ).joinToString(columnSeparator)
}


//endregion

