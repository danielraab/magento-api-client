package magentoAPIClient.category.update

import magentoAPIClient.*
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer


class CategoryUpdateWindow(private val categoryUpdateModel: CategoryUpdateTableModel, config: Configuration) : JFrame() {

    private val categoryUpdateTable = JTable(categoryUpdateModel)

    private val targetUrlJLabel = JLabel(config.baseUrl)
    private val targetStoreViewJLabel = JLabel(config.storeView)

    private val updateJProg = JProgressBar()
    private val startJBtn = JButton("start").apply { background = UPDATE_BTN }
    private val stopJBtn = JButton("stop")

    init {
        createUi()

        //close with ESC Button
        rootPane.registerKeyboardAction(
            { dispatchEvent(WindowEvent(this@CategoryUpdateWindow, WindowEvent.WINDOW_CLOSING)) },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )

        //copy text content to clipboard with double click
        categoryUpdateTable.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.clickCount == 2) {
                    val text = categoryUpdateModel.getValueAt(
                        categoryUpdateTable.rowAtPoint(e.point),
                        categoryUpdateTable.columnAtPoint(e.point)
                    )
                    if (text.toString().isNotEmpty()) {
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text.toString()), null)
                        JOptionPane.showMessageDialog(this@CategoryUpdateWindow, "cell content copied to clipboard.")
                    }
                }
            }
        })

        columnCustomization()

        updateJProg.isStringPainted = true
        updateJProg.string = ""

        isVisible = true
        pack()
    }

    private fun columnCustomization() {

        categoryUpdateTable.columnModel.getColumn(UpdateCategoryEntry.COLUMN_RESPONSE_CODE_IDX).cellRenderer = object :
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

                when ((categoryUpdateTable.model as CategoryUpdateTableModel).getStatusAt(row)) {
                    UpdateStatus.SUCCESS -> l.background = Color.GREEN
                    UpdateStatus.FAILED -> l.background = Color.RED
                    UpdateStatus.NONE -> l.background = Color.LIGHT_GRAY
                }
                return l
            }
        }

        categoryUpdateTable.columnModel.getColumn(UpdateCategoryEntry.COLUMN_RESPONSE_BODY_IDX).cellRenderer = object :
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
    }

    fun setButtonActions(startAction: () -> Unit, stopAction: () -> Unit) {
        startJBtn.addActionListener { startAction() }
        stopJBtn.addActionListener { stopAction() }
    }

    private fun createUi() {
        content {
            rowLayout {
                add(JScrollPane(categoryUpdateTable))

                rowLayout {
                    flowLayoutPanel {
                        add(Label("Target url:"))
                        add(targetUrlJLabel)
                        add(Label("store view:"))
                        add(targetStoreViewJLabel)
                    }
                    flowLayoutPanel {
                        add(updateJProg)
                        add(startJBtn)
                        add(stopJBtn)
                    }
                    maximumSize = Dimension(Int.MAX_VALUE, 20)
                }
            }
        }
    }

    fun updateView(progressMax: Int, progressValue: Int, isUpdating: Boolean) {
        updateProgressBar(progressMax, progressValue)
        isUpdating(isUpdating)
    }

    private fun updateProgressBar(max: Int, value: Int) {
        updateJProg.minimum = 0
        updateJProg.maximum = max
        updateJProg.value = value
        updateJProg.string = "$value/$max"
    }

    private fun isUpdating(updating: Boolean) {
        startJBtn.isEnabled = !updating
        stopJBtn.isEnabled = updating
    }
}