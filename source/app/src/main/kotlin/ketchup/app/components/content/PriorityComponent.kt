package ketchup.app.components.content

import ketchup.app.Model
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import ketchup.console.TodoItem

class PriorityComponent: HBox {
    var toDoItemId : String
    var label : Label
    var options : ComboBox<String>
    var model: Model

    constructor(item: TodoItem, m: Model, archive: Boolean) {
        this.prefHeight = 100.0
        this.prefWidth = 200.0
        this.label = LabelComponent("Priority: ")
        this.options = PriorityOptionsComponent(item, m)
        this.padding = javafx.geometry.Insets(0.0,0.0,0.0,10.0)
        toDoItemId = item.id.toString()
        this.model = m

        this.spacing = 10.0

        if(archive) {
            this.options.isDisable = true;
        }
        this.children.add(label)
        this.children.add(options)
    }

}