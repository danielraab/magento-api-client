package magentoAPIClient.product.update

import magentoAPIClient.*
import magentoAPIClient.product.ProductController
import magentoAPIClient.product.update.selectionTable.ProductSelectionTableModel
import magentoAPIClient.product.update.selectionTable.ProductSelectionTableJFrame
import magentoAPIClient.product.update.updateWindow.ProductUpdateController
import java.awt.EventQueue

class ProductUpdateController(
    private val base: BaseController,
    private val productController: ProductController,
    private val view: ProductUpdateComponent
) :
    GuiControllerInterface {

    private var productSelectorFrame: ProductSelectionTableJFrame? = null
    private var productUpdateController: ProductUpdateController = ProductUpdateController(base)


    override fun initController() {
        view.addBtnActionHandlers({
            if (productSelectorFrame == null || !productSelectorFrame!!.isVisible) {
                EventQueue.invokeLater {
                    val tableModel = ProductSelectionTableModel(productController.productList)
                    tableModel.addTableModelListener { updateInfoLabels() }
                    productSelectorFrame = ProductSelectionTableJFrame("Select products", tableModel)
                    productSelectorFrame!!.initMenu({
                        productController.productList.forEach { it.selected = true }
                        tableModel.productListChanged()
                        updateInfoLabels()
                    }, {
                        productController.productList.forEach { it.selected = false }
                        tableModel.productListChanged()
                        updateInfoLabels()
                    })
                }
            } else {
                productSelectorFrame!!.toFront()
            }
        }, {
            productUpdateController.showWindow(productController.productList.filter { it.selected })
        })

        updateInfoLabels()
    }

    private fun updateInfoLabels() {
        view.updateInfoLabels(productController.productList.filter { it.selected }.size)
    }

}