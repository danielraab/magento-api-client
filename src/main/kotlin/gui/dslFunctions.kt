package gui

import java.awt.BorderLayout
import java.awt.Container
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.CompoundBorder


fun Container.content(init: Container.() -> Unit) {
    this.init()
}


//region menu dsl function


fun JMenuBar.menu(name: String, init: JMenu.() -> Unit) {
    val menu = JMenu(name)
    add(menu)
    menu.init()
}

fun JMenu.menu(name: String, init: JMenu.() -> Unit) {
    val menu = JMenu(name)
    add(menu)
    menu.init()
}

fun JMenu.item(name: String): JMenuItem {
    val item = JMenuItem(name)
    add(item)
    return item
}
operator fun JMenuItem.plus(handler: () -> Unit): JMenuItem {
    addActionListener { handler() }
    return this
}

//endregion



fun Container.borderLayout(init: JPanel.() -> Unit): JPanel {
    val panel = JPanel()
    panel.layout = BorderLayout()
    add(panel)
    panel.init()
    return panel
}

fun Container.north(init: JPanel.() -> Unit): JComponent {
    val jPanel = JPanel()
    jPanel.init()
    add(jPanel, BorderLayout.NORTH)
    return jPanel
}

fun Container.center(init: JPanel.() -> Unit): JComponent {
    val p = JPanel()
    p.init()
    add(p, BorderLayout.CENTER)
    return p
}

fun Container.rowLayout() {
    val l = BoxLayout(this, BoxLayout.Y_AXIS)
    layout = l
}

fun Container.flowLayoutPanel(init: JPanel.() -> Unit): JPanel {
    val l = JPanel()
    val fl= FlowLayout()
    l.layout = fl
    add(l)
    l.init()
    return l
}

fun Container.boxLayoutPanel(axis:Int, init: JPanel.() -> Unit): JPanel {
    val l = JPanel()
    val bl = BoxLayout(l, axis)
    l.layout = bl
    add(l)
    l.init()
    return l
}

fun JPanel.gridLayoutPanel(rows: Int, cols: Int, init: JPanel.() -> Unit): JPanel {
    val l = JPanel()
    val gl = GridLayout(rows, cols)
    l.layout = gl
    add(l)
    l.init()
    return l
}


fun JPanel.borderPanelWithTitle(title: String, init: JPanel.() -> Unit): JPanel {
    val p = JPanel()
    val b = BorderFactory.createTitledBorder(title)
    p.border = b
    add(p)
    p.init()
    p.addSpacedBorder(3, 0,3,0)
    return p
}

fun JPanel.addSpacedBorder(all:Int) = addSpacedBorder(all, all, all, all)

fun JPanel.addSpacedBorder(top: Int, left: Int, bottom: Int, right: Int) {
    this.border = CompoundBorder(BorderFactory.createEmptyBorder(top, left, bottom, right), this.border)
}



fun JFrame.menuBar(init: JMenuBar.() -> Unit) {
    jMenuBar = JMenuBar()
    jMenuBar.init()
}