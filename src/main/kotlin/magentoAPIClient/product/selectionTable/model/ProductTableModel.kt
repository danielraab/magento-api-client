package magentoAPIClient.product.selectionTable.model

import magentoAPIClient.product.Product
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel


class ProductTableModel(private val productList: List<Product>) : TableModel {

    private val listenerList = mutableListOf<TableModelListener>()

    fun productListChanged() {
        listenerList.forEach { it.tableChanged(TableModelEvent(this)) }
    }

    override fun getRowCount(): Int {
        return productList.size
    }

    override fun getColumnCount(): Int {
        return Product.getColumns().size
    }

    override fun getColumnName(columnIndex: Int): String {
        return Product.getColumns()[columnIndex]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return Product.getColumnsClass()[columnIndex]
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return Product.editableColumnArray()[columnIndex]
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return productList[rowIndex].toArray()[columnIndex]
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        listenerList.forEach { it.tableChanged(TableModelEvent(this, rowIndex)) }
        productList[rowIndex].updateColumn(columnIndex, aValue)
    }

    override fun addTableModelListener(l: TableModelListener?) {
        if (l != null) {
            listenerList.add(l)
        }
    }

    override fun removeTableModelListener(l: TableModelListener?) {
        listenerList.remove(l)
    }
}
