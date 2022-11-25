package ketchup.app.components.content

import Model
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import ketchup.console.TodoItem
import org.controlsfx.control.CheckComboBox

class TagsComponent: HBox {
    var toDoItemId : String
    var label : Label
    var options : CheckComboBox<String>

    constructor(item: TodoItem, m: Model) {
        this.prefHeight = 100.0
        this.prefWidth = 200.0
        this.label = LabelComponent("Tags: ")
        this.padding = javafx.geometry.Insets(0.0,0.0,0.0,10.0)
        this.options = TagsOptionsComponent(item, m)
        this.options.checkModelProperty().addListener{ e, o, n ->
            println("checked something off")
        }

        toDoItemId = item.id.toString()
        this.children.add(label)
        this.children.add(options)
    }
}