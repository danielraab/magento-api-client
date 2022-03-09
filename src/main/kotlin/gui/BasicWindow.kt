package gui

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
                    add(tabbedPanel)
                }
            }
        }



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
        attributeExtractionPanel.updateControls(config)
        categoryExportPanel.updateControls(config)
    }

    override fun updateCurrentConfig(config: Configuration): Configuration {
        var newConfig = Configuration(baseUrlJTF.text, authJTF.text);
        newConfig = attributeExtractionPanel.updateCurrentConfig(newConfig)
        newConfig = categoryExportPanel.updateCurrentConfig(newConfig)
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

