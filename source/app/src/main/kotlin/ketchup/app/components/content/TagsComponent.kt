package ketchup.app.components.content

import Model
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import ketchup.console.TodoItem

class TagsComponent: HBox {
    var toDoItemId : String
    var label : Label
    var options : ComboBox<String>

    constructor(item: TodoItem, m: Model) {
        this.prefHeight = 100.0
        this.prefWidth = 200.0
        this.label = LabelComponent("Tags: ")
        this.options = TagsOptionsComponent(item, m)
        toDoItemId = item.id.toString()
        this.children.add(label)
        this.children.add(options)
    }
}