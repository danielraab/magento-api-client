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
    private val selectProductsBtn = JButton("selectProducts")
    private val selectProductsCntJL = JLabel("0")

    fun createUI() {
        rowLayout()
        borderPanelWithTitle("load products") {
            rowLayout()
            flowLayoutPanel {
                add(queryProductsBtn)
            }
            flowLayoutPanel {
                add(Label("products cnt:"))
                add(productListSizeJL)
            }

            flowLayoutPanel {
                add(selectProductsBtn)
            }
            flowLayoutPanel {
                add(Label("selected products: "))
                add(selectProductsCntJL)
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
        selectProductsBtn.isEnabled = enabled
    }


    fun updateInfoLabels(productListCnt: Int = -1, selectedProductCnt:Int = -1) {
        if (productListCnt >= 0) productListSizeJL.text = productListCnt.toString()
        if (selectedProductCnt >= 0) selectProductsCntJL.text = selectedProductCnt.toString()
    }

    fun addBtnActionHandlers(
        queryProductsAction: () -> Unit,
        selectProductsAction: () -> Unit
    ) {
        queryProductsBtn.addActionListener { queryProductsAction() }
        selectProductsBtn.addActionListener { selectProductsAction() }
    }
}