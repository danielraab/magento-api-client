package magentoAPIClient.product.selectionTable

import magentoAPIClient.menu
import magentoAPIClient.menuBar
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import javax.swing.*


class ProductSelectionTableJFrame(title: String, model: ProductSelectionTableModel) : JFrame(title) {

    private val selectAllMenu = JMenuItem("select all")
    private val unselectAllMenu = JMenuItem("unselect all")
    private val selectionTable = JTable(model)

    init {
        createUi()

        rootPane.registerKeyboardAction(
            { dispatchEvent(WindowEvent(this@ProductSelectionTableJFrame, WindowEvent.WINDOW_CLOSING)) },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )

        selectionTable.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {}

            override fun keyPressed(e: KeyEvent?) {}

            override fun keyReleased(e: KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_SPACE) {
                    selectionTable.selectedRows.forEach { model.toggleSelection(it) }
                }
            }
        })

        isVisible = true
        pack()
    }

    fun initMenu(selectAllAction: () -> Unit, unselectAllAction: () -> Unit) {
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

        add(JScrollPane(selectionTable))
    }
}
