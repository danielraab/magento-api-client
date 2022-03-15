package gui.panel

import gui.GuiComponentInterface
import gui.borderPanelWithTitle
import gui.flowLayoutPanel
import gui.rowLayout
import model.Configuration
import java.awt.Label
import javax.swing.*

class ProductUpdateComponent : JPanel(), GuiComponentInterface {

    private val queryProductsBtn = JButton("query")
    private val productListSizeJL = JLabel("0")

    fun createUI() {
        rowLayout()
        borderPanelWithTitle("load products") {
            rowLayout()
            flowLayoutPanel {
                add(queryProductsBtn)
                add(Label("products cnt:"))
                add(productListSizeJL)
            }
        }
        borderPanelWithTitle("update products:") {
            rowLayout()
            flowLayoutPanel {
                add(Label("test"))
            }
        }
    }

    override fun updateControls(config: Configuration) {
    }

    override fun updateConfigFromGui(config: Configuration): Configuration {
        return config
    }

    override fun allControlsEnabled(enabled: Boolean) {
        queryProductsBtn.isEnabled = enabled
    }


    fun updateInfoLabels(productListCnt: Int = -1) {
        if (productListCnt >= 0) productListSizeJL.text = productListCnt.toString()
    }

    fun addBtnActionHandlers(
        queryProductsAction: () -> Unit,
    ) {
        queryProductsBtn.addActionListener { queryProductsAction() }
    }

}