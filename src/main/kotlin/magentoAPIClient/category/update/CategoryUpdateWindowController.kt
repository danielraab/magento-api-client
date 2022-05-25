package magentoAPIClient.category.update

import magentoAPIClient.*
import magentoAPIClient.category.CategoryUpdate
import magentoAPIClient.http.CategoryRequestFactory
import magentoAPIClient.http.HttpHelper
import org.json.JSONException
import java.awt.event.WindowEvent
import java.io.IOException
import java.net.ConnectException
import javax.swing.JOptionPane
import javax.swing.SwingWorker

class CategoryUpdateWindowController(val base: BaseController) {

    private var updateWorker: UpdateCategoryWorker? = null
    private var config = Configuration()
    private var tableEntryList = listOf<UpdateCategoryEntry>()

    private var tableModel: CategoryUpdateTableModel = CategoryUpdateTableModel(tableEntryList)
    private var view: CategoryUpdateWindow? = null



    fun showWindow(categoryList: List<CategoryUpdate>) {
        tableEntryList = categoryList.map { UpdateCategoryEntry(it) }

        //close open window (to reopen with new data)
        view?.dispatchEvent(WindowEvent(view, WindowEvent.WINDOW_CLOSING))


        tableModel = CategoryUpdateTableModel(tableEntryList)
        config = base.updateConfigFromGui(config)
        view = CategoryUpdateWindow(tableModel, config)

        view!!.setButtonActions({
            if (updateWorker == null || updateWorker!!.isDone) {
                base.allControlsEnabled(false)

                updateWorker = UpdateCategoryWorker(this)
                updateWorker!!.execute()
                updateView()
            }
        }, {
            updateWorker?.cancel(true)
        })

        updateView()
    }

    class UpdateCategoryWorker(
        private val controller: CategoryUpdateWindowController
    ) : SwingWorker<Unit, UpdateCategoryEntry>() {

        override fun doInBackground() {
            try {
                controller.tableEntryList.filter { it.status != UpdateStatus.SUCCESS }.forEach {
                    try {
                        val httpResponse = HttpHelper(
                            CategoryRequestFactory.updateCategory(
                                controller.config.baseUrl,
                                controller.config.authentication,
                                controller.config.storeView,
                                it.category
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

        override fun process(chunks: MutableList<UpdateCategoryEntry>?) {
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