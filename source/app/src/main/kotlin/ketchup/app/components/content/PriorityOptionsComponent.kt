package ketchup.app.components.content

import javafx.scene.control.ComboBox
import ketchup.console.TodoItem

class PriorityOptionsComponent: ComboBox<String> {
    constructor(item: TodoItem, list: List<String>) {
        this.prefHeight = 26.0
        this.prefWidth = 120.0
        this.value = item.priority.toString()
        this.items.addAll(list)
        this.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Tags Field")
                    // Write Code
                }
            }
        }
    }
}