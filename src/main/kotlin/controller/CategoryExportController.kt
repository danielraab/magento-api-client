package controller

import gui.BasicWindow
import gui.CategoryExportComponent
import magentoAPIClient.HttpHelper
import magentoAPIClient.attributesOfAttributeSet
import magentoAPIClient.listAttributeSets
import magentoAPIClient.listAttributes
import model.*
import org.json.JSONArray
import org.json.JSONObject
import java.awt.EventQueue
import java.time.zone.ZoneRulesProvider
import javax.swing.JFrame
import javax.swing.JOptionPane

class CategoryExportController(private val base:BaseController, private val view: CategoryExportComponent) {

    private var config = Configuration()

    fun initController() {
        view.addBtnActionHandlers({
            base.allControlsEnabled(false)
            this.config = base.updateConfigFromGui(this.config)

        },{},{},{})

        view.updateInfoLabels()
    }

}