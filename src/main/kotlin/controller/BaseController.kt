package controller

import gui.BasicWindow
import model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.nio.charset.Charset
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JOptionPane

const val refreshTimeoutWhileLoading: Long = 2000
private const val startupConfigFilePath = "./config.json"

class BaseController(private val base: BasicWindow) {

    private var config = Configuration()


    private val attrExtractionController = AttributeExtractionController(base)
    private val categoryExportController = CategoryExportController(base)

    fun initController() {
        attrExtractionController.initController()
        categoryExportController.initController()

        base.updateControls(config)

        base.addSaveConfigMenuItem {
            config = base.updateCurrentConfig(config)
            saveDialogHandler(base, config.toJSONObject().toString(), Charsets.UTF_8)
        }

        base.addLoadConfigMenuItem {
            val configStr = readFileDialogHandler(base, Charsets.UTF_8)
            if (configStr.isNotBlank()) {
                try {
                    config = configStr.toJSONObject().toConfigObject()
                    base.updateControls(config)
                    JOptionPane.showMessageDialog(base, "Config successfully loaded.")
                } catch (e: JSONException) {
                    println("unable to read json file:")
                    println(configStr)
                    JOptionPane.showMessageDialog(base, "no valid config file.")
                }
            }
        }

        base.isVisible = true
        base.pack()

        checkForLocalConfigFile()
    }

    private fun checkForLocalConfigFile() {
        val configFile = File(startupConfigFilePath)
        if (configFile.canRead()) {
            val configStr = configFile.readText(Charsets.UTF_8)
            try {
                config = configStr.toJSONObject().toConfigObject()
                base.updateControls(config)
                JOptionPane.showMessageDialog(base, "Config successfully loaded.")
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
        root.put("columnSeparator", this.columnSeparator)
        root.put("encoding", this.encoding)
        return root
    }

    private fun JSONObject.toConfigObject(): Configuration {
        return Configuration(
            this.getString("baseUrl"),
            this.getString("auth"),
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

fun saveDialogHandler(parent: JFrame, contentToSave: String, encoding: Charset) {
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