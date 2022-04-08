package magentoAPIClient.category.overview

import magentoAPIClient.*
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import javax.swing.*


class CategoryUpdateOverviewWindow(title: String, model: CategoryUpdateOverviewTableModel) : JFrame(title) {

    private val selectionTable = JTable(model)

    init {
        createUi()

        rootPane.registerKeyboardAction(
            { dispatchEvent(WindowEvent(this@CategoryUpdateOverviewWindow, WindowEvent.WINDOW_CLOSING)) },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )

        isVisible = true
        pack()
    }


    private fun createUi() {

        content {
            rowLayout {
                add(JScrollPane(selectionTable))
            }
        }
    }
}
