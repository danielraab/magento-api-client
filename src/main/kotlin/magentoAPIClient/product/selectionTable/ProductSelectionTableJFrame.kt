package magentoAPIClient.product.selectionTable

import magentoAPIClient.*
import java.awt.Dimension
import java.awt.Label
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.TableRowSorter
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec


class ProductSelectionTableJFrame(title: String, model: ProductSelectionTableModel) : JFrame(title) {

    private val selectAllMenu = JMenuItem("select all")
    private val unselectAllMenu = JMenuItem("unselect all")
    private val selectionTable = JTable(model)

    private val searchJTF = JTextField().also { it.columns = 20 }
    private val rowSorter = TableRowSorter(model)

    init {
        createUi()

        rootPane.registerKeyboardAction(
            { dispatchEvent(WindowEvent(this@ProductSelectionTableJFrame, WindowEvent.WINDOW_CLOSING)) },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )


        selectionTable.rowSorter = rowSorter
        searchJTF.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                updateRowFilter()
            }
            override fun removeUpdate(e: DocumentEvent?) {
                updateRowFilter()
            }
            override fun changedUpdate(e: DocumentEvent?) {
                updateRowFilter()
            }
        })


        selectionTable.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {}

            override fun keyPressed(e: KeyEvent?) {}

            override fun keyReleased(e: KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_SPACE) {
                    selectionTable.selectedRows.forEach {model.toggleSelection(selectionTable.convertRowIndexToModel(it))}
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

        content {
            rowLayout {
                flowLayoutPanel {
                    add(Label("Search: "))
                    add(searchJTF)
                    maximumSize= Dimension(Int.MAX_VALUE, 20)
                }
                add(JScrollPane(selectionTable))
            }
        }
    }


    private fun updateRowFilter() {
        val text: String = searchJTF.text

        if (text.trim().isEmpty()) {
            rowSorter.setRowFilter(null)
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)$text"))
        }
    }
}
