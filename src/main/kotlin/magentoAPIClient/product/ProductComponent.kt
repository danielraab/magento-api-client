package magentoAPIClient.product

import magentoAPIClient.*
import magentoAPIClient.product.export.ProductExportComponent
import magentoAPIClient.product.update.ProductUpdateComponent
import javax.swing.JPanel

class ProductComponent  : JPanel(), GuiComponentInterface {

    val productUpdateComponent = ProductUpdateComponent()
    val productExportComponent = ProductExportComponent()

    override fun createUI() {
        productUpdateComponent.createUI()
        productExportComponent.createUI()


        content {
            rowLayout {
                add(productExportComponent)
                add(productUpdateComponent)
            }
        }
    }


    override fun updateGuiFromConfig(config: Configuration) {
        productUpdateComponent.updateGuiFromConfig(config)
        productExportComponent.updateGuiFromConfig(config)
    }

    override fun updateConfigFromGui(config: Configuration): Configuration {
        var newConfig = productUpdateComponent.updateConfigFromGui(config)
        newConfig = productExportComponent.updateConfigFromGui(newConfig)
        return newConfig
    }

    override fun allControlsEnabled(enabled: Boolean) {
        productUpdateComponent.allControlsEnabled(enabled)
        productExportComponent.allControlsEnabled(enabled)
    }
}