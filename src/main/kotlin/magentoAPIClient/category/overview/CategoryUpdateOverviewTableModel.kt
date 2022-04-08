package magentoAPIClient.category.overview

import magentoAPIClient.category.CategoryUpdate
import magentoAPIClient.category.CategoryUpdateHeader
import magentoAPIClient.product.FullProduct
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel


class CategoryUpdateOverviewTableModel(categoryList: List<CategoryUpdate>) : TableModel {

    private val tableList = categoryList.flatMap { it.toList() }

    override fun getRowCount(): Int {
        return tableList.size
    }

    override fun getColumnCount(): Int {
        return CategoryUpdateHeader.values().size
    }

    override fun getColumnName(columnIndex: Int): String {
        return CategoryUpdateHeader.values()[columnIndex].label
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return CategoryUpdate.getColumnsClass()[columnIndex]
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return false
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return tableList[rowIndex][columnIndex]
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {}
    override fun addTableModelListener(l: TableModelListener?) {}
    override fun removeTableModelListener(l: TableModelListener?) {}
}
