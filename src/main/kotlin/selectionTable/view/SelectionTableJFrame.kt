package selectionTable.view

import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.TableModel


class SelectionTableJFrame(title: String, val model: TableModel) : JFrame(title) {

    init {
        createUi()
    }

    private fun createUi() {

        val table = JTable(model)
        val jsp = JScrollPane(table)
        add(jsp)

    }
}
