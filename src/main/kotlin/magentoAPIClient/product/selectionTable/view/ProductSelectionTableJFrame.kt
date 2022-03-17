package magentoAPIClient.product.selectionTable.view

import gui.menu
import gui.menuBar
import javax.swing.*
import javax.swing.table.TableModel


class ProductSelectionTableJFrame(title: String, val model: TableModel) : JFrame(title) {

    private val selectAllMenu = JMenuItem("select all")
    private val unselectAllMenu = JMenuItem("unselect all")

    init {
        createUi()


        isVisible = true
        pack()
    }

    fun initMenu(selectAllAction: () -> Unit, unselectAllAction:()->Unit) {
        selectAllMenu.addActionListener {
            selectAllAction()
        }
        unselectAllMenu.addActionListener {
            unselectAllAction()
        }
    }

    private fun createUi() {
        menuBar {
            menu("selection") {
                add(selectAllMenu)
                add(unselectAllMenu)
            }
        }

        val table = JTable(model)
        val jsp = JScrollPane(table)
        add(jsp)
    }
}
