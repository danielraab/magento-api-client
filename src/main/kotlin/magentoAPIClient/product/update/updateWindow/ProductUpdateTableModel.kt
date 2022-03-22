package magentoAPIClient.product.update.updateWindow

import magentoAPIClient.product.update.Product
import java.lang.IllegalStateException
import java.net.http.HttpResponse
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

data class UpdateProductEntry(val product: Product, var response:HttpResponse<String>?) {
    fun toArray(): List<String> {
        return listOf(product.sku, product.name, (response?.statusCode() ?:"").toString(), response?.body()?:"")
    }
    companion object {
        const val COLUMN_RESPONSE_CODE_IDX = 2
        const val COLUMN_RESPONSE_BODY_IDX = 3
        fun getColumns(): List<String> {
            return listOf("sku", "product name", "response code", "response body")
        }
    }
}

class ProductUpdateTableModel(private val productList: List<UpdateProductEntry>) : TableModel {

    private val listenerList = mutableListOf<TableModelListener>()

    override fun getRowCount(): Int {
        return productList.size
    }

    override fun getColumnCount(): Int {
        return UpdateProductEntry.getColumns().size
    }

    override fun getColumnName(columnIndex: Int): String {
        return UpdateProductEntry.getColumns()[columnIndex]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return UpdateProductEntry.getColumns()[columnIndex]::class.javaObjectType
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return false
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return productList[rowIndex].toArray()[columnIndex]
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        throw IllegalStateException("not allowed to set values in ProductUpdateTableModel")
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
