package ketchup.app.components.content

import Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import ketchup.app.components.graphic.ItemComponent
import ketchup.console.TodoItem
import ketchup.console.TodoList

class PriorityComponent: HBox {
    var toDoItemId : String
    var label : Label
    var options : ComboBox<String>
    var model: Model

    constructor(item: TodoItem, m: Model) {
        this.prefHeight = 100.0
        this.prefWidth = 200.0
        this.label = LabelComponent("Priority: ")
        this.options = PriorityOptionsComponent(item, m)
        toDoItemId = item.id.toString()
        this.model = m

        this.children.add(label)
        this.children.add(options)
    }

}