package magentoAPIClient.product.update

import magentoAPIClient.*
import magentoAPIClient.product.ProductController
import magentoAPIClient.product.update.selectionWindow.ProductSelectionTableModel
import magentoAPIClient.product.update.selectionWindow.ProductSelectionWindow
import magentoAPIClient.product.update.updateWindow.ProductUpdateWindowController
import java.awt.EventQueue

class ProductUpdateController(
    private val base: BaseController,
    private val productController: ProductController,
    private val view: ProductUpdateComponent
) :
    GuiControllerInterface {

    private var productSelectorFrame: ProductSelectionWindow? = null
    private var productUpdateController: ProductUpdateWindowController = ProductUpdateWindowController(base)


    override fun initController() {
        view.addBtnActionHandlers({
            if (productSelectorFrame == null || !productSelectorFrame!!.isVisible) {
                EventQueue.invokeLater {
                    val tableModel = ProductSelectionTableModel(productController.productList)
                    tableModel.addTableModelListener { updateInfoLabels() }
                    productSelectorFrame = ProductSelectionWindow("Select products", tableModel)
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