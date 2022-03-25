package magentoAPIClient.attribute

import magentoAPIClient.*
import java.awt.Color
import java.awt.Label
import javax.swing.*

class AttributeExtractionComponent : JPanel(), GuiComponentInterface {

    private val queryAPIJB = JButton("query").apply { background = QUERY_BTN }
    private val attributeSetCntJL = JLabel("0")
    private val attributeCntJL = JLabel("0")
    private val attributeOptionCntJL = JLabel("0")
    private val saveAttributeSetsJB = JButton("attribute sets").apply { background = EXPORT_LIGHT_BTN }
    private val saveAttributesJB = JButton("attributes").apply { background = EXPORT_LIGHT_BTN }
    private val saveAttributesWithOptionsJB = JButton("attributes with options").apply { background = EXPORT_BTN }
    private val saveAllJB = JButton("all").apply { background = EXPORT_BTN }

    override fun createUI() {
        rowLayout {
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
                    add(saveAttributeSetsJB)
                    add(saveAttributesJB)
                }
                flowLayoutPanel {
                    add(saveAttributesWithOptionsJB)
                    add(saveAllJB)
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
        queryAPIJB.isEnabled = enabled
        saveAttributeSetsJB.isEnabled = enabled
        saveAttributesJB.isEnabled = enabled
        saveAttributesWithOptionsJB.isEnabled = enabled
        saveAllJB.isEnabled = enabled
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


}