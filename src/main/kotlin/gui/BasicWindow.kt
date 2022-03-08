package gui

import model.Configuration
import java.awt.*
import javax.swing.*
import kotlin.system.exitProcess

class BasicWindow : JFrame("Magento API Attribute Extractor"), GuiComponentInterface {

    val attributeExtractionPanel = AttributeExtractionComponent()
    private val baseUrlJTF = JTextField()
    private val authJTF = JTextField()
    private val loadConfigMenu = JMenuItem("load")
    private val saveConfigMenu = JMenuItem("save")

    init {
        attributeExtractionPanel.createUI()
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
                    add(attributeExtractionPanel)
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

    override fun updateControls(config: Configuration) {
        baseUrlJTF.text = config.baseUrl
        authJTF.text = config.authentication
        attributeExtractionPanel.updateControls(config)
    }

    override fun updateCurrentConfig(config: Configuration): Configuration {
        var newConfig = Configuration(baseUrlJTF.text, authJTF.text);
        newConfig = attributeExtractionPanel.updateCurrentConfig(newConfig)
        return newConfig
    }



//region add listener functions

    fun addSaveConfigMenuItem(action: () -> Unit) {
        saveConfigMenu.addActionListener {action()}
    }

    fun addLoadConfigMenuItem(action: () -> Unit) {
        loadConfigMenu.addActionListener {action()}
    }

//endregion


//region update view functions


    override fun allControlsEnabled(enabled: Boolean) {
        attributeExtractionPanel.allControlsEnabled(enabled)
    }

//endregion

}

