package magentoAPIClient.product.export

import magentoAPIClient.*
import java.awt.Label
import javax.swing.*

class ProductExportComponent : JPanel(), GuiComponentInterface {

    private val queryProductsBtn = JButton("query")
    private val productListSizeJL = JLabel("0")


    override fun createUI() {
        rowLayout()
        borderPanelWithTitle("load products") {
            rowLayout()
            flowLayoutPanel {
                add(queryProductsBtn)
                add(Label("products cnt:"))
                add(productListSizeJL)
            }
        }
    }

    override fun updateGuiFromConfig(config: Configuration) {
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
        queryProductsAction: () -> Unit
    ) {
        queryProductsBtn.addActionListener { queryProductsAction() }
    }
}