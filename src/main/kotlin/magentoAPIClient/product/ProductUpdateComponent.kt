package magentoAPIClient.product

import magentoAPIClient.*
import java.awt.Label
import javax.swing.*

class ProductUpdateComponent : JPanel(), GuiComponentInterface {

    private val queryProductsBtn = JButton("query")
    private val productListSizeJL = JLabel("0")
    private val selectProductsBtn = JButton("selectProducts")
    private val selectProductsCntJL = JLabel("0")

    private val attrTypeJCB = JComboBox<ProductAttributeType>(ProductAttributeType.values())
    private val attrValueTypeJCB = JComboBox<ProductAttributeValueType>(ProductAttributeValueType.values())
    private val attrKeyJTF = JTextField().also { it.columns = 10 }
    private val attrValueJTF = JTextField().also { it.columns = 10 }


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
                add(attrTypeJCB)
                add(attrValueTypeJCB)
                add(attrKeyJTF)
                add(attrValueJTF)
            }
        }
    }

    override fun updateGuiFromConfig(config: Configuration) {
        if(config.productAttributeUpdateList.isNotEmpty()) {
            val singleUpdateData = config.productAttributeUpdateList[0]
            attrTypeJCB.selectedItem = singleUpdateData.type
            attrValueTypeJCB.selectedItem = singleUpdateData.valueType
            attrKeyJTF.text = singleUpdateData.key
            attrValueJTF.text = singleUpdateData.value
        }
    }

    private fun getProductAttributeUpdateData():ProductAttributeUpdate {
        return ProductAttributeUpdate(attrTypeJCB.selectedItem as ProductAttributeType,
            attrValueTypeJCB.selectedItem as ProductAttributeValueType,
            attrKeyJTF.text,
            attrValueJTF.text
        )
    }

    override fun updateConfigFromGui(config: Configuration): Configuration {
        config.productAttributeUpdateList.clear()
        config.productAttributeUpdateList.add(getProductAttributeUpdateData())
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