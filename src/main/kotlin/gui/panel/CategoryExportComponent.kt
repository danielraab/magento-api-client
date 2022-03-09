package gui.panel

import gui.GuiComponentInterface
import gui.borderPanelWithTitle
import gui.flowLayoutPanel
import gui.rowLayout
import model.Configuration
import java.awt.Label
import javax.swing.*

class CategoryExportComponent : JPanel(), GuiComponentInterface {

    private val queryCategoryTreeBtn = JButton("query")
    private val categoryTreeSizeJL = JLabel("0")
    private val saveCategoryTreeBtn = JButton("save")
    private val queryCategoryListBtn = JButton("query")
    private val categoryListSizeJL = JLabel("0")
    private val saveCategoryListBtn = JButton("save")

    fun createUI() {
        rowLayout()
        borderPanelWithTitle("category tree") {
            rowLayout()
            flowLayoutPanel {
                add(queryCategoryTreeBtn)
            }
            flowLayoutPanel {
                add(Label("categories in tree:"))
                add(categoryTreeSizeJL)
            }
            flowLayoutPanel {
                add(saveCategoryTreeBtn)
            }
        }
        borderPanelWithTitle("category details") {
            rowLayout()
            flowLayoutPanel {
                add(queryCategoryListBtn)
            }
            flowLayoutPanel {
                add(Label("categories in list:"))
                add(categoryListSizeJL)
            }
            flowLayoutPanel {
                add(saveCategoryListBtn)
            }
        }
    }

    override fun updateControls(config: Configuration) {
    }

    override fun updateConfigFromGui(config: Configuration): Configuration {
        return config
    }

    override fun allControlsEnabled(enabled: Boolean) {
        queryCategoryListBtn.isEnabled = enabled
        queryCategoryTreeBtn.isEnabled = enabled
        saveCategoryTreeBtn.isEnabled = enabled
        saveCategoryListBtn.isEnabled = enabled
    }


    fun updateInfoLabels(categoryTreeCnt: Int = -1, categoryListCnt: Int = -1) {
        if (categoryTreeCnt >= 0) categoryTreeSizeJL.text = categoryTreeCnt.toString()
        if (categoryListCnt >= 0) categoryListSizeJL.text = categoryListCnt.toString()
    }

    fun addBtnActionHandlers(
        queryTreeAction: () -> Unit,
        saveTreeAction: () -> Unit,
        queryDetailsAction: () -> Unit,
        saveDetailsAction: () -> Unit
    ) {
        queryCategoryTreeBtn.addActionListener { queryTreeAction() }
        saveCategoryTreeBtn.addActionListener { saveTreeAction() }
        queryCategoryListBtn.addActionListener { queryDetailsAction() }
        saveCategoryListBtn.addActionListener { saveDetailsAction() }
    }

}