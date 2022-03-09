package gui

import model.Configuration
import javax.swing.*

class CategoryExportComponent : JPanel(), GuiComponentInterface {


    fun createUI() {
        rowLayout()
        borderPanelWithTitle("attributes info") {
            rowLayout()
            flowLayoutPanel {
                add(JLabel("test label"))
            }
        }
    }

    override fun updateControls(config: Configuration) {
    }

    override fun updateCurrentConfig(config: Configuration): Configuration {
        return config
    }

    override fun allControlsEnabled(enabled: Boolean) {
    }


    //region add listener functions


    //endregion

}