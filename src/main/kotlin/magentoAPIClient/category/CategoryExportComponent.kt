package magentoAPIClient.category

import magentoAPIClient.*
import java.awt.Color
import java.awt.Label
import javax.swing.*

class CategoryExportComponent : JPanel(), GuiComponentInterface {

    private val queryCategoryTreeBtn = JButton("query").apply { background = Color.GREEN }
    private val categoryTreeSizeJL = JLabel("0")
    private val saveCategoryTreeBtn = JButton("export CSV").apply { background = Color.ORANGE }
    private val queryCategoryListBtn = JButton("query").apply { background = Color.GREEN }
    private val categoryListSizeJL = JLabel("0")
    private val saveCategoryListBtn = JButton("export CSV").apply { background = Color.ORANGE }

    override fun createUI() {
        content {
            rowLayout {
                borderPanelWithTitle("category tree") {
                    rowLayout()
                    flowLayoutPanel {
                        add(queryCategoryTreeBtn)
                        flowLayoutPanel {
                            add(Label("categories in tree:"))
                            add(categoryTreeSizeJL)
                        }
                        add(saveCategoryTreeBtn)
                    }
                }
                borderPanelWithTitle("category details") {
                    rowLayout()
                    flowLayoutPanel {
                        add(queryCategoryListBtn)
                        flowLayoutPanel {
                            add(Label("categories in list:"))
                            add(categoryListSizeJL)
                        }
                        add(saveCategoryListBtn)
                    }
                }
            }
        }
    }

    override fun updateGuiFromConfig(config: Configuration) {
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