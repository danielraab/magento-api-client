package gui

import gui.panel.AttributeExtractionComponent
import gui.panel.CategoryExportComponent
import model.AvailableCharset
import model.Configuration
import java.awt.*
import javax.swing.*
import kotlin.system.exitProcess

class BasicWindow : JFrame("Magento API Attribute Extractor"), GuiComponentInterface {

    private val tabbedPanel = JTabbedPane()
    val attributeExtractionPanel = AttributeExtractionComponent()
    val categoryExportPanel = CategoryExportComponent()
    private val baseUrlJTF = JTextField()
    private val authJTF = JTextField()
    private val columnSeparatorJTF = JTextField()
    private val encodingJCB = JComboBox(AvailableCharset.values())
    private val loadConfigMenu = JMenuItem("load")
    private val saveConfigMenu = JMenuItem("save")

    init {
        attributeExtractionPanel.createUI()
        categoryExportPanel.createUI()
        tabbedPanel.addTab("Read Attributes", attributeExtractionPanel)
        tabbedPanel.addTab("Read Categories", categoryExportPanel)
        createUi()
    }

    private fun createUi() {
        menuBar {
            menu("config") {
                add(loadConfigMenu)
                add(saveConfigMenu)
                add(JSeparator())
                item("exit") + { exitProcess(0) }
            }
        }
        content {
            borderLayout {
                addSpacedBorder(5)
                center {
                    rowLayout()
                    borderPanelWithTitle("basic parameters") {
                        rowLayout()
                        boxLayoutPanel(BoxLayout.X_AXIS) {
                            add(Label("Base Url:"))
                            add(baseUrlJTF)
                        }
                        boxLayoutPanel(BoxLayout.X_AXIS) {
                            add(Label("Authorization:"))
                            add(authJTF)
                        }
                    }
                    borderPanelWithTitle("CSV settings:") {
                        rowLayout()
                        flowLayoutPanel {
                            add(Label("column separator:"))
                            add(columnSeparatorJTF)
                            add(Label("encoding:"))
                            add(encodingJCB)
                        }
                    }
                    add(tabbedPanel)
                }
            }
        }

        columnSeparatorJTF.columns = 2

        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(300, 200)
        isResizable = false
        setLocationRelativeTo(null)
    }


    private fun menuBar(init: JMenuBar.() -> Unit) {
        jMenuBar = JMenuBar()
        jMenuBar.init()
    }

    //region update view functions
    override fun updateControls(config: Configuration) {
        baseUrlJTF.text = config.baseUrl
        authJTF.text = config.authentication
        columnSeparatorJTF.text = config.columnSeparator
        encodingJCB.selectedItem = config.encoding
        attributeExtractionPanel.updateControls(config)
        categoryExportPanel.updateControls(config)
    }

    override fun updateConfigFromGui(config: Configuration): Configuration {
        var newConfig = Configuration(baseUrlJTF.text, authJTF.text)
        val sel = encodingJCB.selectedItem

        newConfig.columnSeparator = columnSeparatorJTF.text
        if (sel is AvailableCharset) newConfig.encoding = sel

        newConfig = attributeExtractionPanel.updateConfigFromGui(newConfig)
        newConfig = categoryExportPanel.updateConfigFromGui(newConfig)
        return newConfig
    }

    override fun allControlsEnabled(enabled: Boolean) {
        attributeExtractionPanel.allControlsEnabled(enabled)
        categoryExportPanel.allControlsEnabled(enabled)
    }
//endregion

//region add listener functions

    fun addSaveConfigMenuItem(action: () -> Unit) {
        saveConfigMenu.addActionListener {action()}
    }

    fun addLoadConfigMenuItem(action: () -> Unit) {
        loadConfigMenu.addActionListener {action()}
    }

//endregion

}

