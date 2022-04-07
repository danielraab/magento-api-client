package magentoAPIClient.attribute

data class AttributeOption(val valueId: String, val label: String) {
    companion object
} //the id is not unique

//region csv Attribute Option functions

fun AttributeOption.Companion.csvHeader(): List<String> {
    return listOf("option.valueId", "option.label")
}

fun AttributeOption.Companion.emptyCsv(): List<String> {
    return listOf("", "")
}

fun AttributeOption.toCsv(): List<String> {
    return listOf(
        this.valueId, this.label
    )
}

//endregion

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
) {

    companion object
}

//region csv Attribute functions


fun Attribute.toCsv(): List<Any> {
    return listOf(
        this.id,
        this.code,
        this.scope,
        this.frontendInput,
        this.entityTypeID,
        this.isRequired,
        this.isUserDefined,
        this.defaultFrontendLabel
    )
}

fun Attribute.toCsvList(withOptions: Boolean): List<List<Any>> {
    return if (withOptions) {
        if (options.isEmpty()) {
            listOf((this.toCsv() + AttributeOption.emptyCsv()))
        } else {
            options.map { (_, option) ->
                this.toCsv() + option.toCsv()
            }
        }
    } else {
        listOf(this.toCsv())
    }
}


fun Attribute.Companion.csvHeader(withOptions: Boolean): List<String> {
    val header = listOf(
        "attr.id",
        "attr.code",
        "attr.scope",
        "attr.frontendInput",
        "attr.entityTypeId",
        "attr.isRequired",
        "attr.isUserDefined",
        "attr.defaultFrontendLabel"
    )
    return if (withOptions) header + AttributeOption.csvHeader()
    else header
}

fun Attribute.Companion.emptyCsv(withOptions: Boolean = false): MutableList<String> {
    val columnList = mutableListOf<String>()
    repeat(8) {
        columnList.add("")
    }
    if (withOptions) columnList.addAll(AttributeOption.emptyCsv())
    return columnList
}


//endregion


data class AttributeSet(val id: Int, val name: String, val attributes: MutableMap<Int, Attribute> = mutableMapOf()) {
    companion object
}


//region csv extension functions

fun AttributeSet.toCsv(): List<Any> {
    return listOf(this.id, this.name)
}


fun AttributeSet.toCsvList(withAttributes: Boolean, withOptions: Boolean): List<List<Any>> {
    return if (withAttributes) {
        if (this.attributes.isEmpty()) {
            listOf((this.toCsv() + Attribute.emptyCsv(withOptions)))
        } else {
            this.attributes.flatMap { (_, attr) ->
                attr.toCsvList(withOptions).map { this.toCsv() + it }
            }
        }
    } else {
        listOf(this.toCsv())
    }
}


fun AttributeSet.Companion.csvHeader(withAttributes: Boolean, withOptions: Boolean): List<String> {
    val attrSet = listOf("aSet.id", "aSet.name")

    return if(withAttributes) {
        attrSet + Attribute.csvHeader(withOptions)
    } else {
        return attrSet
    }
}

fun AttributeSet.Companion.emptyCsv(): List<String> {
    return listOf("", "")
}

//endregion

