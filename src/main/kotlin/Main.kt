import magentoAPIClient.BaseController
import magentoAPIClient.BaseWindow
import java.awt.EventQueue


fun main() {

    EventQueue.invokeLater {
        val frame = BaseWindow()
        val baseController = BaseController(frame)
        baseController.initController()
    }
}