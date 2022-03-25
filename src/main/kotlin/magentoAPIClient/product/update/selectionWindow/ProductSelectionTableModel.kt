package magentoAPIClient.product.update.selectionWindow

import magentoAPIClient.product.FullProduct
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel


class ProductSelectionTableModel(private val productList: List<FullProduct>) : TableModel {

    private val listenerList = mutableListOf<TableModelListener>()

    fun productListChanged() {
        listenerList.forEach { it.tableChanged(TableModelEvent(this)) }
    }

    override fun getRowCount(): Int {
        return productList.size
    }

    override fun getColumnCount(): Int {
        return FullProduct.getColumns().size
    }

    override fun getColumnName(columnIndex: Int): String {
        return FullProduct.getColumns()[columnIndex]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return FullProduct.getColumnsClass()[columnIndex]
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return FullProduct.editableColumnArray()[columnIndex]
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return productList[rowIndex].toArray()[columnIndex]
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        productList[rowIndex].updateColumn(columnIndex, aValue)
        notifyListeners(rowIndex)
    }

    fun toggleSelection(rowIndex: Int) {
        val curSel = getValueAt(rowIndex, FullProduct.SELECTION_COLUMN_INDEX)
        if (curSel is Boolean) {
            productList[rowIndex].updateColumn(FullProduct.SELECTION_COLUMN_INDEX, !curSel)
            notifyListeners(rowIndex)
        }
    }

    override fun addTableModelListener(l: TableModelListener?) {
        if (l != null) {
            listenerList.add(l)
        }
    }

    private fun notifyListeners(rowIndex: Int) {
        listenerList.forEach { it.tableChanged(TableModelEvent(this, rowIndex)) }
    }

    override fun removeTableModelListener(l: TableModelListener?) {
        listenerList.remove(l)
    }
}
