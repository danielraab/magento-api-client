package magentoAPIClient

interface GuiComponentInterface {


    fun updateGuiFromConfig(config: Configuration)

    fun updateConfigFromGui(config: Configuration): Configuration

    fun allControlsEnabled(enabled: Boolean)
}