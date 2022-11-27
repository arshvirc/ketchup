package ketchup.app.components.content

import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import kotlinx.coroutines.runBlocking

class TrashComponent: ButtonBar {
    var toDoItemId : String
    var model: Model
    var api: Client

    constructor(item: TodoItem, m: Model) {
        this.toDoItemId = item.id.toString()
        this.model = m
        this.api = m.api
        this.prefHeight = 40.0
        this.prefWidth = 200.0
        this.padding = javafx.geometry.Insets(10.0)
        var trashButton = Button("Trash")
        trashButton.setOnAction {
            run {
                println("Deleting the item with the following item: ${this.toDoItemId}")
                model.editToDoItem(toDoItemId, "delete", 0)
            }
        }

        this.buttons.add(trashButton)
    }
}