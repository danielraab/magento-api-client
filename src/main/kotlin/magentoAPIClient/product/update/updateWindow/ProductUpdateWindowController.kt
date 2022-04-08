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

class ProductUpdateWindowController(val base: BaseController) {

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
        config = base.updateConfigFromGui(config)
        view = ProductUpdateWindow(tableModel, config)

        view!!.setButtonActions({
            if (updateWorker == null || updateWorker!!.isDone) {
                base.allControlsEnabled(false)

//            tableEntryList.forEach { it.status=UpdateStatus.NONE it.responseCode = ""; it.responseBody = "" }     //to clear possible runs

                updateWorker = UpdateProductsWorker(this)
                updateWorker!!.execute()
                updateView()
            }
        }, {
            updateWorker?.cancel(true)
        })

        updateView()
    }

    class UpdateProductsWorker(
        private val controller: ProductUpdateWindowController
    ) : SwingWorker<Unit, UpdateProductEntry>() {

        override fun doInBackground() {
            try {
                controller.tableEntryList.filter { it.product.selected && it.status != UpdateStatus.SUCCESS }.forEach {
                    try {
                        val httpResponse = HttpHelper(
                            ProductRequestFactory.updateProduct(
                                controller.config.baseUrl,
                                controller.config.authentication,
                                controller.config.storeView,
                                it.product.sku,
                                controller.config.productAttributeUpdateList
                            )
                        ).sendRequest()
                        it.status = UpdateStatus.SUCCESS
                        it.responseCode = httpResponse.statusCode().toString()
                        it.responseBody = httpResponse.body()
                    } catch (_: IOException) {
                    } catch (ie: InterruptedException) {
                        throw ie
                    } catch (e: Exception) {
                        it.status = UpdateStatus.FAILED
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
                throw e
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
            controller.updateView()
            if (!isCancelled)
                JOptionPane.showMessageDialog(
                    controller.view,
                    "Update process has finished."
                )
        }
    }

    fun updateView() {
        tableModel.triggerTableModelListener()
        view?.updateView(
            tableEntryList.size,
            tableEntryList.count { it.status != UpdateStatus.NONE },
            updateWorker != null && !updateWorker!!.isDone
        )
    }
}