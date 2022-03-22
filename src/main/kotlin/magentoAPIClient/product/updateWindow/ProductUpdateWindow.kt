package magentoAPIClient.product.updateWindow

import magentoAPIClient.content
import magentoAPIClient.flowLayoutPanel
import magentoAPIClient.rowLayout
import java.awt.Color
import java.awt.Component
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer


class ProductUpdateWindow(private val productUpdateModel: ProductUpdateTableModel) : JFrame() {

    private val productUpdateTable = JTable(productUpdateModel)

    private val updateJProg = JProgressBar()
    private val startJBtn = JButton("start")
    private val stopJBtn = JButton("stop")

    init {
        createUi()

        rootPane.registerKeyboardAction(
            { dispatchEvent(WindowEvent(this@ProductUpdateWindow, WindowEvent.WINDOW_CLOSING)) },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )

        //copy text content to clipboard with double click
        productUpdateTable.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.clickCount == 2) {
                    val text = productUpdateModel.getValueAt(
                        productUpdateTable.rowAtPoint(e.point),
                        productUpdateTable.columnAtPoint(e.point)
                    )
                    if (text.toString().isNotEmpty()) {
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text.toString()), null)
                        JOptionPane.showMessageDialog(this@ProductUpdateWindow, "cell content copied to clipboard.")
                    }
                }
            }
        })

        productUpdateTable.columnModel.getColumn(UpdateProductEntry.COLUMN_RESPONSE_CODE_IDX).cellRenderer = object :
            DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable?,
                value: Any?,
                isSelected: Boolean,
                hasFocus: Boolean,
                row: Int,
                column: Int
            ): Component {
                val l = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column) as JLabel

                if(value.toString().startsWith("2")) {
                    l.background = Color.GREEN
                } else if(value.toString().isNotEmpty()) {
                    l.background = Color.RED
                } else {
                    l.background = Color.LIGHT_GRAY
                }
                return l
            }
        }

        productUpdateTable.columnModel.getColumn(UpdateProductEntry.COLUMN_RESPONSE_BODY_IDX).cellRenderer = object :
            DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable?,
                value: Any?,
                isSelected: Boolean,
                hasFocus: Boolean,
                row: Int,
                column: Int
            ): Component {
                val l = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column) as JLabel
                l.toolTipText = "copy to clipboard with a double click."
                return l
            }
        }

        updateJProg.isStringPainted = true
        updateJProg.string = ""

        isVisible = true
        pack()
    }

    fun setButtonActions(startAction: () -> Unit, stopAction: () -> Unit) {
        startJBtn.addActionListener { startAction() }
        stopJBtn.addActionListener { stopAction() }
    }

    private fun createUi() {
        content {
            rowLayout {
                add(JScrollPane(productUpdateTable))

                flowLayoutPanel {
                    add(updateJProg)
                    add(startJBtn)
                    add(stopJBtn)
                }
            }
        }
    }

    fun updateProgressBar(max:Int, value:Int) {
        updateJProg.minimum = 0
        updateJProg.maximum = max
        updateJProg.value = value
        updateJProg.string = "$value/$max"
    }

    fun isRunning(running: Boolean) {
        startJBtn.isEnabled = !running
        stopJBtn.isEnabled = running
    }
}