package controller

import gui.BasicWindow
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

class CategoryExportController(base:BasicWindow) {
    private val view = base.categoryExportPanel

    private var config = Configuration()

    fun initController() {


    }

}