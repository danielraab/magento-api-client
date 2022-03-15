package model

import selectionTable.model.SelectionObjectInterface

data class Product(val id: Int, val sku: String, val name: String, val status: Int, val type: String) :
    SelectionObjectInterface {
    override var selected = false


    fun updateColumn(columnIndex: Int, aValue: Any?) {
        if (columnIndex == 0 && aValue is Boolean) {
            selected = aValue
        }
    }

    fun toArray() = arrayOf(selected, id, sku, name, status, type)

    companion object {
        fun getColumns() = arrayOf("selected", "id", "sku", "name", "status", "type")
        fun editableColumnArray() = arrayOf(true, false, false, false, false, false)
        fun getColumnsClass() = arrayOf(
            Boolean::class.javaObjectType,
            Int::class.javaObjectType,
            String::class.javaObjectType,
            String::class.javaObjectType,
            Int::class.javaObjectType,
            String::class.javaObjectType
        )

    }
}