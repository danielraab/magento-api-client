package selectionTable.controller

import selectionTable.view.SelectionTableJFrame

class SelectionTableController(private val view: SelectionTableJFrame) {

    fun initController() {

        view.isVisible = true
        view.pack()
//        view.size = Dimension(300, 300)
    }

}