import controller.BaseController
import gui.BasicWindow
import java.awt.EventQueue


fun main() {

    EventQueue.invokeLater {
        val frame = BasicWindow()
        val baseController = BaseController(frame)
        baseController.initController()
    }
}