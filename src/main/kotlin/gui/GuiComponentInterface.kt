package gui

import model.Configuration

interface GuiComponentInterface {


    fun updateControls(config: Configuration)

    fun updateConfigFromGui(config: Configuration): Configuration

    fun allControlsEnabled(enabled: Boolean)
}