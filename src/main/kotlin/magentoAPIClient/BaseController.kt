package magentoAPIClient

import magentoAPIClient.attribute.AttributeExtractionController
import magentoAPIClient.category.CategoryExportController
import magentoAPIClient.product.ProductUpdateController
import model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.awt.Component
import java.awt.EventQueue
import java.io.File
import java.nio.charset.Charset
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JOptionPane

const val refreshTimeoutWhileLoading: Long = 2000
private const val startupConfigFilePath = "./config.json"

class BaseController(val view: BaseWindow) {

    private var config = Configuration()


    private val attrExtractionController = AttributeExtractionController(this, view.attributeExtractionPanel)
    private val categoryExportController = CategoryExportController(this, view.categoryExportPanel)
    private val productUpdateController = ProductUpdateController(this, view.productUpdaterComponent)

    fun initController() {
        attrExtractionController.initController()
        categoryExportController.initController()
        productUpdateController.initController()

        view.updateControls(config)

        view.addSaveConfigMenuItem {
            config = view.updateConfigFromGui(config)
            saveDialogHandler(view, config.toJSONObject().toString(), Charsets.UTF_8)
        }

        view.addLoadConfigMenuItem {
            val configStr = readFileDialogHandler(view, Charsets.UTF_8)
            if (configStr.isNotBlank()) {
                try {
                    config = configStr.toJSONObject().toConfigObject()
                    view.updateControls(config)
                    JOptionPane.showMessageDialog(view, "Config successfully loaded.")
                } catch (e: JSONException) {
                    println("unable to read json file:")
                    println(configStr)
                    JOptionPane.showMessageDialog(view, "no valid config file.")
                }
            }
        }

        view.isVisible = true
        view.pack()

        checkForLocalConfigFile()
    }

    fun updateConfigFromGui(config: Configuration) = view.updateConfigFromGui(config)

    fun allControlsEnabled(enabled: Boolean) = view.allControlsEnabled(enabled)

    private fun checkForLocalConfigFile() {
        val configFile = File(startupConfigFilePath)
        if (configFile.canRead()) {
            val configStr = configFile.readText(Charsets.UTF_8)
            try {
                config = configStr.toJSONObject().toConfigObject()
                view.updateControls(config)
                JOptionPane.showMessageDialog(view, "Config successfully loaded.")
            } catch (e: JSONException) {
                println("unable to read json file:")
                println(configStr)
            }
        } else {
            println("no (startup) config file found at: ${configFile.absolutePath}")
        }
    }


    //region configuration json methods

    private fun Configuration.toJSONObject(): JSONObject {
        val root = JSONObject()
        root.put("baseUrl", this.baseUrl)
        root.put("auth", this.authentication)
        root.put("storeView", this.storeView)
        root.put("columnSeparator", this.columnSeparator)
        root.put("encoding", this.encoding)
        return root
    }

    private fun JSONObject.toConfigObject(): Configuration {
        return Configuration(
            this.getString("baseUrl"),
            this.getString("auth"),
            this.getString("storeView"),
            this.getString("columnSeparator"),
            AvailableCharset.valueOf(this.getString("encoding"))
        )
    }
    //endregion
}


fun joinStrings(separator: String, vararg parts: String): String {
    return parts.joinToString(separator)
}

fun String.toJSONObject(): JSONObject {
    val jsonRoot = JSONTokener(this).nextValue()
    if (jsonRoot is JSONObject)
        return jsonRoot
    else
        throw IllegalArgumentException("given string is no JsonObject: $jsonRoot")
}

fun String.toJSONArray(): JSONArray {
    val jsonRoot = JSONTokener(this).nextValue()
    if (jsonRoot is JSONArray)
        return jsonRoot
    else
        throw IllegalArgumentException("given string is no JSONArray: $jsonRoot")
}


// region dialog handler

fun saveDialogHandler(parent: Component, contentToSave: String, encoding: Charset) {
    val fileChooser = JFileChooser()

    if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
        val fileToSave: File = fileChooser.selectedFile
        if (fileToSave.createNewFile() || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                parent, "Should the file be overwritten ?", "File already exists.", JOptionPane.YES_NO_OPTION
            )
        ) {
            fileToSave.writeText(contentToSave, encoding)
            JOptionPane.showMessageDialog(parent, "File successfully saved.")
        }
    }
}

fun readFileDialogHandler(parent: JFrame, encoding: Charset): String {
    val fileChooser = JFileChooser()

    if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
        val fileToSave: File = fileChooser.selectedFile
        if (fileToSave.canRead()) {
            return fileToSave.readText(encoding)
        } else {
            JOptionPane.showMessageDialog(parent, "File cannot be read.")
        }
    }
    return ""
}

//endregion


fun queryHandling(
    base: BaseController,
    guiRefreshInterval: Long,
    queryJob: () -> Unit,
    afterGuiUpdate: () -> Unit,
    recurringGuiUpdate: () -> Unit
) {
    base.allControlsEnabled(false)

    Thread {
        try {
            var isRefreshingAllowed = true
            Thread {
                while (isRefreshingAllowed) {
                    EventQueue.invokeLater {
                        recurringGuiUpdate()
                    }
                    Thread.sleep(guiRefreshInterval)
                }
            }.start()
            queryJob()
            isRefreshingAllowed = false
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            EventQueue.invokeLater {
                recurringGuiUpdate()
                afterGuiUpdate()
                base.allControlsEnabled(true)
            }
        }
    }.start()
}


fun JSONObject.getBoolean(key: String, default: Boolean) =
    if (this.has(key)) this.getBoolean(key) else default

fun JSONObject.getInt(key: String, default: Int) =
    if (this.has(key)) this.getInt(key) else default

fun JSONObject.getJSONArray(key: String, default: JSONArray): JSONArray =
    if (this.has(key)) this.getJSONArray(key) else default