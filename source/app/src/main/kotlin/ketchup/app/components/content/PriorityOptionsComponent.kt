package ketchup.app.components.content

import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.ComboBox
import ketchup.app.components.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking

class PriorityOptionsComponent: ComboBox<String> {
    var model: Model
    private var toDoItemId: String
    private val api: Client

    constructor(item: TodoItem, m: Model) {
        this.prefHeight = 26.0
        this.prefWidth = 120.0

        this.model = m
        this.api = m.api
        this.toDoItemId = item.id.toString()

        this.value = convertNumToPriority(item.priority)
        this.items.addAll(m.listOfPriorities)
        this.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    val newValue = convertPriorityToNum(this.value);
                    println("Proceeding to Update Priority to be $newValue")
                    model.editToDoItem(toDoItemId, "priority", newValue.toString())
                }
            }
        }
    }

    private fun convertNumToPriority(priority: Int): String {
        return when(priority) {
            0 -> "None"
            1 -> "Low"
            2 -> "Medium"
            3 -> "High"
            else -> "None"
        }
    }

    private fun convertPriorityToNum(priority: String): Int {
        return when(priority) {
            "None" -> 0
            "Low" -> 1
            "Medium" -> 2
            "High" -> 3
            else -> 0
        }
    }
}