package magentoAPIClient.product.update.updateWindow

import magentoAPIClient.*
import magentoAPIClient.http.HttpHelper
import magentoAPIClient.http.ProductRequestFactory
import magentoAPIClient.product.FullProduct
import org.json.JSONException
import java.awt.event.WindowEvent
import java.io.IOException
import java.net.ConnectException
import javax.swing.JOptionPane
import javax.swing.SwingWorker

class ProductUpdateController(val base: BaseController) {

    private var updateWorker: UpdateProductsWorker? = null
    private var config = Configuration()
    private var tableEntryList = listOf<UpdateProductEntry>()

    private var tableModel: ProductUpdateTableModel = ProductUpdateTableModel(tableEntryList)
    private var view: ProductUpdateWindow? = null

    fun showWindow(productList: List<FullProduct>) {
        tableEntryList = productList.map { UpdateProductEntry(it) }

        //close open window (to reopen with new data)
        view?.dispatchEvent(WindowEvent(view, WindowEvent.WINDOW_CLOSING))


        tableModel = ProductUpdateTableModel(tableEntryList)
        view = ProductUpdateWindow(tableModel)
        view!!.isRunning(false)

        view!!.setButtonActions({
            base.allControlsEnabled(false)
            view!!.isRunning(true)

            tableEntryList.forEach { it.responseCode = ""; it.responseBody = "" }
            updateView()

            this.config = base.updateConfigFromGui(this.config)

            updateWorker = UpdateProductsWorker(this)
            updateWorker!!.execute()
        }, {
            updateWorker?.cancel(true)
        })

        updateView()
    }

    class UpdateProductsWorker(
        private val controller: ProductUpdateController
    ) : SwingWorker<Unit, UpdateProductEntry>() {

        override fun doInBackground() {
            try {
                controller.tableEntryList.filter { it.product.selected }.forEach {
                    try {
                        val httpResponse = HttpHelper(
                            ProductRequestFactory.updateProduct(
                                controller.config.baseUrl,
                                controller.config.authentication,
                                controller.config.storeView,
                                it.product.sku(),
                                controller.config.productAttributeUpdateList
                            )
                        ).sendRequest()
                        it.responseCode = httpResponse.statusCode().toString()
                        it.responseBody = httpResponse.body()
                    } catch (_: IOException) {
                    } catch (e: Exception) {
                        it.responseCode = "error"
                        it.responseBody = e.message.toString()
                        println("error in updating occurred:")
                        e.printStackTrace()
                    }
                    publish(it)
                }
            } catch (e: JSONException) {
                JOptionPane.showMessageDialog(
                    controller.view,
                    "Unable to create update message. Check your settings.",
                    "error in creating message.",
                    JOptionPane.ERROR_MESSAGE
                )
            } catch (e: InterruptedException) {
                JOptionPane.showMessageDialog(
                    controller.view,
                    "Update process was cancelled by the user."
                )
            } catch (e: ConnectException) {
                JOptionPane.showMessageDialog(
                    controller.view,
                    "Unable to connect to endpoint: ${e.message}"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                JOptionPane.showMessageDialog(
                    controller.view,
                    "Unable to create update message. Check your settings.",
                    "error in creating message.",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }

        override fun process(chunks: MutableList<UpdateProductEntry>?) {
            controller.updateView()
        }

        override fun done() {
            controller.base.allControlsEnabled(true)
            controller.view?.isRunning(false)
            if (!isCancelled)
                JOptionPane.showMessageDialog(
                    controller.view,
                    "Update process has finished."
                )
        }
    }

    private fun updateView() {
        tableModel.triggerTableModelListener()
        view?.updateProgressBar(tableEntryList.size, tableEntryList.count { it.responseCode.isNotEmpty() })
    }
}