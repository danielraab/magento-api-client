package magentoAPIClient.product.export

import magentoAPIClient.*
import java.awt.Label
import javax.swing.*

class ProductExportComponent : JPanel(), GuiComponentInterface {

    private val queryProductsBtn = JButton("query")
    private val productListSizeJL = JLabel("0")

    private val exportBtn = JButton("export CSV")

    override fun createUI() {
        rowLayout()
        borderPanelWithTitle("export products") {
            rowLayout()
            flowLayoutPanel {
                add(queryProductsBtn)
                add(Label("products cnt:"))
                add(productListSizeJL)
            }
            flowLayoutPanel {
                add(exportBtn)
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
        exportBtn.isEnabled = enabled
    }

    fun updateInfoLabels(productListCnt: Int = -1) {
        if (productListCnt >= 0) productListSizeJL.text = productListCnt.toString()
    }

    fun addBtnActionHandlers(
        queryProductsAction: () -> Unit,
        exportCSVAction: () -> Unit
    ) {
        queryProductsBtn.addActionListener { queryProductsAction() }
        exportBtn.addActionListener { exportCSVAction() }
    }
}