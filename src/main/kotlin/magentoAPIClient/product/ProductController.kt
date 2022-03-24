package magentoAPIClient.product

import magentoAPIClient.BaseController
import magentoAPIClient.GuiControllerInterface
import magentoAPIClient.product.export.ProductLoadExportController
import magentoAPIClient.product.update.ProductUpdateController

class ProductController(private val base:BaseController, private val view: ProductComponent): GuiControllerInterface {

    private val productUpdateController = ProductUpdateController(base, this, view.productUpdateComponent)
    private val productExportController = ProductLoadExportController(base, this, view.productExportComponent)

    val productList = mutableListOf<FullProduct>()

    override fun initController() {
        productUpdateController.initController()
        productExportController.initController()
    }

}