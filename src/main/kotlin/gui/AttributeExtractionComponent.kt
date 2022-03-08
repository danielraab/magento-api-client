package gui

import model.AvailableCharset
import model.Configuration
import java.awt.Label
import javax.swing.*

class AttributeExtractionComponent : JPanel(), GuiComponentInterface {

    private val queryAPIJB = JButton("query API")
    private val attributeSetCntJL = JLabel("0")
    private val attributeCntJL = JLabel("0")
    private val attributeOptionCntJL = JLabel("0")
    private val columnSeparatorJTF = JTextField()
    private val encodingJCB = JComboBox(AvailableCharset.values())
    private val saveAttributeSetsJB = JButton("attribute sets")
    private val saveAttributesJB = JButton("attributes")
    private val saveAttributesWithOptionsJB = JButton("attributes with options")
    private val saveAllJB = JButton("all")

    fun createUI() {
        rowLayout()
        borderPanelWithTitle("attributes info") {
            rowLayout()
            flowLayoutPanel {
                add(queryAPIJB)
            }
            flowLayoutPanel {
                add(Label("attribute set cnt:"))
                add(attributeSetCntJL)
            }
            flowLayoutPanel {
                add(Label("attribute cnt:"))
                add(attributeCntJL)
            }
            flowLayoutPanel {
                add(Label("attribute option cnt:"))
                add(attributeOptionCntJL)
            }
        }
        borderPanelWithTitle("save to CSV") {
            rowLayout()
            flowLayoutPanel {
                add(Label("column separator:"))
                add(columnSeparatorJTF)
                add(Label("encoding:"))
                add(encodingJCB)
            }
            flowLayoutPanel {
                add(saveAttributeSetsJB)
            }
            flowLayoutPanel {
                add(saveAttributesJB)
                add(saveAttributesWithOptionsJB)
            }
            flowLayoutPanel {
                add(saveAllJB)
            }
        }


        columnSeparatorJTF.columns = 2
    }

    override fun updateControls(config: Configuration) {
        columnSeparatorJTF.text = config.columnSeparator
        encodingJCB.selectedItem = config.encoding
    }

    override fun updateCurrentConfig(config: Configuration): Configuration {
        val sel = encodingJCB.selectedItem
        config.columnSeparator = columnSeparatorJTF.text
        if (sel is AvailableCharset) config.encoding = sel

        return config
    }


//region add listener functions

    fun addQueryAPIBtnAction(action: () -> Unit) {
        queryAPIJB.addActionListener { action() }
    }

    fun addSaveAttributeSetsBtnAction(action: () -> Unit) {
        saveAttributeSetsJB.addActionListener { action() }
    }

    fun addSaveAttributesBtnAction(action: () -> Unit) {
        saveAttributesJB.addActionListener { action() }
    }

    fun addSaveAttributesWithOptionsBtnAction(action: () -> Unit) {
        saveAttributesWithOptionsJB.addActionListener { action() }
    }

    fun addAllBtnAction(action: () -> Unit) {
        saveAllJB.addActionListener { action() }
    }

    //endregion


    fun updateInfoLabels(attributeSetCnt: Int, attributeCnt: Int, attributeOptionCnt: Int) {
        attributeSetCntJL.text = attributeSetCnt.toString()
        attributeCntJL.text = attributeCnt.toString()
        attributeOptionCntJL.text = attributeOptionCnt.toString()
    }

    override fun allControlsEnabled(enabled: Boolean) {
        queryAPIJB.isEnabled = enabled
        saveAttributeSetsJB.isEnabled = enabled
        saveAttributesJB.isEnabled = enabled
        saveAttributesWithOptionsJB.isEnabled = enabled
        saveAllJB.isEnabled = enabled
    }
}