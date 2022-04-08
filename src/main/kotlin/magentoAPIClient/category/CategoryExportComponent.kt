package magentoAPIClient.category

import magentoAPIClient.*
import java.awt.Label
import java.awt.Panel
import javax.swing.*

class CategoryExportComponent : JPanel(), GuiComponentInterface {

    private val queryCategoryTreeBtn = JButton("query").apply { background = QUERY_BTN }
    private val categoryTreeSizeJL = JLabel("0")
    private val saveCategoryTreeBtn = JButton("export CSV").apply { background = EXPORT_BTN }

    private val queryCategoryListBtn = JButton("query").apply { background = QUERY_BTN }
    private val categoryListSizeJL = JLabel("0")
    private val saveCategoryListBtn = JButton("export CSV").apply { background = EXPORT_BTN }

    private val readCSVFileBtn = JButton("read csv").apply { background = READ_BTN }
    private val categoryCntLbl = JLabel("0")
    private val categoryUpdateCntLbl = JLabel("0")
    private val showUpdatesBtn = JButton("show updates").apply { background = OPEN_WINDOW_BTN }
    private val openUpdateWindowBtn = JButton("UPDATE").apply { background = OPEN_WINDOW_BTN }

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

                borderPanelWithTitle("update categories") {
                    rowLayout()
                    flowLayoutPanel {
                        add(readCSVFileBtn)
                        rowLayout {
                            flowLayoutPanel {
                                add(Label("Categories:"))
                                add(categoryCntLbl)
                            }
                            flowLayoutPanel {
                                add(Label("Updates:"))
                                add(categoryUpdateCntLbl)
                            }
                        }
                        add(showUpdatesBtn)
                        add(openUpdateWindowBtn)
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


    fun updateInfoLabels(
        categoryTreeCnt: Int = -1,
        categoryListCnt: Int = -1,
        updateCategoryCnt: Int = -1,
        updatesCnt: Int = -1
    ) {
        if (categoryTreeCnt >= 0) categoryTreeSizeJL.text = categoryTreeCnt.toString()
        if (categoryListCnt >= 0) categoryListSizeJL.text = categoryListCnt.toString()
        if (updateCategoryCnt >= 0) categoryCntLbl.text = updateCategoryCnt.toString()
        if (updatesCnt >= 0) categoryUpdateCntLbl.text = updatesCnt.toString()
    }

    fun addCategoryTreeBtnHandlers(
        queryTreeAction: () -> Unit,
        saveTreeAction: () -> Unit,
    ) {
        queryCategoryTreeBtn.addActionListener { queryTreeAction() }
        saveCategoryTreeBtn.addActionListener { saveTreeAction() }
    }

    fun addCategoryDetailBtnHandlers(
        queryDetailsAction: () -> Unit,
        saveDetailsAction: () -> Unit
    ) {
        queryCategoryListBtn.addActionListener { queryDetailsAction() }
        saveCategoryListBtn.addActionListener { saveDetailsAction() }
    }

    fun addCategoryUpdateBtnHandlers(
        readCsvAction: () -> Unit,
        openShowUpdateWindow: () -> Unit,
        openUpdateWindow: () -> Unit,
    ) {
        readCSVFileBtn.addActionListener { readCsvAction() }
        showUpdatesBtn.addActionListener { openShowUpdateWindow() }
        openUpdateWindowBtn.addActionListener { openUpdateWindow() }
    }
}