package magentoAPIClient.product

import magentoAPIClient.BaseController
import magentoAPIClient.Configuration
import magentoAPIClient.GuiControllerInterface
import magentoAPIClient.product.export.ProductExportController
import magentoAPIClient.product.update.ProductUpdateController

class ProductController(private val base:BaseController, private val view: ProductComponent): GuiControllerInterface {

    private val productUpdateController = ProductUpdateController(base, view.productUpdateComponent)
    private val productExportController = ProductExportController(base, view.productExportComponent)


    override fun initController() {
        productUpdateController.initController()
        productExportController.initController()
    }

}