package magentoAPIClient.product.updateWindow

import magentoAPIClient.*
import magentoAPIClient.http.HttpHelper
import magentoAPIClient.http.ProductRequestFactory
import magentoAPIClient.product.Product
import org.json.JSONObject
import magentoAPIClient.product.selectionTable.ProductSelectionTableModel
import magentoAPIClient.product.selectionTable.ProductSelectionTableJFrame
import magentoAPIClient.product.updateWindow.ProductUpdateWindow
import org.json.JSONException
import java.awt.EventQueue
import java.awt.event.WindowEvent
import java.net.http.HttpResponse
import javax.swing.JOptionPane
import javax.swing.SwingWorker

class ProductUpdateController(val base: BaseController) {

    private var updateWorker: UpdateProductsWorker? = null
    private var config = Configuration()
    private var tableEntryList = listOf<UpdateProductEntry>()

    private var tableModel: ProductUpdateTableModel = ProductUpdateTableModel(tableEntryList)
    private var view: ProductUpdateWindow? = null

    fun showWindow(productList: List<Product>) {
        tableEntryList = productList.map { UpdateProductEntry(it, null) }

        //close open window (to reopen with new data)
        view?.dispatchEvent(WindowEvent(view, WindowEvent.WINDOW_CLOSING))


        tableModel = ProductUpdateTableModel(tableEntryList)
        view = ProductUpdateWindow(tableModel)
        view!!.isRunning(false)

        view!!.setButtonActions({
            base.allControlsEnabled(false)
            view!!.isRunning(true)

            tableEntryList.forEach { it.response = null }
            tableModel.triggerTableModelListener()

            this.config = base.updateConfigFromGui(this.config)

            updateWorker = UpdateProductsWorker(this)
            updateWorker!!.execute()
        }, {
            updateWorker?.cancel(true)
        })

    }

    class UpdateProductsWorker(
        private val controller: ProductUpdateController
    ) : SwingWorker<Unit, UpdateProductEntry>() {

        override fun doInBackground() {
            try {
                controller.tableEntryList.filter { it.product.selected }.forEach {
                    val httpResponse = HttpHelper(
                        ProductRequestFactory.updateProduct(
                            controller.config.baseUrl,
                            controller.config.authentication,
                            controller.config.storeView,
                            it.product.sku,
                            controller.config.productAttributeUpdateList
                        )
                    ).sendRequest()
                    it.response = httpResponse
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
            controller.tableModel.triggerTableModelListener()
        }

        override fun done() {
            controller.base.allControlsEnabled(true)
            controller.view?.isRunning(false)
            if(!isCancelled)
                JOptionPane.showMessageDialog(
                    controller.view,
                    "Update process has finished."
                )
        }
    }
}