package magentoAPIClient

interface GuiComponentInterface {
    fun createUI()

    fun updateGuiFromConfig(config: Configuration)

    fun updateConfigFromGui(config: Configuration): Configuration

    fun allControlsEnabled(enabled: Boolean)
}