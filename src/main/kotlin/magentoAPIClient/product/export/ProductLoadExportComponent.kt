package magentoAPIClient.product.export

import magentoAPIClient.*
import java.awt.Color
import java.awt.Label
import javax.swing.*

class ProductLoadExportComponent : JPanel(), GuiComponentInterface {

    private val queryProductsBtn = JButton("query").apply { background = QUERY_BTN }
    private val productListSizeJL = JLabel("0")

    private val exportBtn = JButton("export CSV").apply { background = EXPORT_BTN }

    override fun createUI() {
        rowLayout()
        borderPanelWithTitle("export products") {
            rowLayout()
            flowLayoutPanel {
                add(queryProductsBtn)
                flowLayoutPanel {
                    add(Label("products cnt:"))
                    add(productListSizeJL)
                }
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