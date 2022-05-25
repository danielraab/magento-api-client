package magentoAPIClient.category.update

import magentoAPIClient.category.CategoryUpdate
import java.lang.IllegalStateException
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

enum class UpdateStatus {SUCCESS, FAILED, NONE}

data class UpdateCategoryEntry(
    val category: CategoryUpdate,
    var status: UpdateStatus = UpdateStatus.NONE,
    var responseCode: String = "",
    var responseBody: String = ""
) {
    fun toArray(): List<String> {
        return listOf(category.id.toString(), responseCode, responseBody)
    }

    companion object {
        const val COLUMN_RESPONSE_CODE_IDX = 1
        const val COLUMN_RESPONSE_BODY_IDX = 2
        fun getColumns(): List<String> {
            return listOf("category id", "response code", "response body")
        }
    }
}

class CategoryUpdateTableModel(private val categoryList: List<UpdateCategoryEntry>) : TableModel {

    private val listenerList = mutableListOf<TableModelListener>()

    override fun getRowCount(): Int {
        return categoryList.size
    }

    override fun getColumnCount(): Int {
        return UpdateCategoryEntry.getColumns().size
    }

    override fun getColumnName(columnIndex: Int): String {
        return UpdateCategoryEntry.getColumns()[columnIndex]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return UpdateCategoryEntry.getColumns()[columnIndex]::class.javaObjectType
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return false
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return categoryList[rowIndex].toArray()[columnIndex]
    }

    fun getStatusAt(rowIndex: Int): UpdateStatus {
        return categoryList[rowIndex].status
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        throw IllegalStateException("not allowed to set values in CategoryUpdateTableModel")
    }

    override fun addTableModelListener(l: TableModelListener?) {
        if (l != null) {
            listenerList.add(l)
        }
    }

    fun triggerTableModelListener() {
        listenerList.forEach { it.tableChanged((TableModelEvent(this))) }
    }

    private fun notifyListeners(rowIndex: Int) {
        listenerList.forEach { it.tableChanged(TableModelEvent(this, rowIndex)) }
    }

    override fun removeTableModelListener(l: TableModelListener?) {
        listenerList.remove(l)
    }
}
