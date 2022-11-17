package ketchup.app.components.content

import Model
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import ketchup.console.TodoItem

class TrashComponent: ButtonBar {
    var toDoItemId : String
    var model: Model

    constructor(item: TodoItem, m: Model) {
        this.toDoItemId = item.id.toString()
        this.model = m
        this.prefHeight = 40.0
        this.prefWidth = 200.0
        var trashButton = Button("Trash")
        trashButton.setOnAction { obs ->
            run {
                println("Deleting the item with the following item: ${this.toDoItemId}")
                // Re-Add Functionality
                removeDeletedItem(toDoItemId)
            }
        }

        this.buttons.add(trashButton)
    }

    private fun removeDeletedItem( completedID: String) {
        val tempList = FXCollections.observableArrayList<Node>()
        for (item in this.model.uiListOfAllItems) {
            if ( item.id == completedID ) {
                tempList.add(item)
                println()
            }
        }
        this.model.uiListOfAllItems.removeAll(tempList)
    }
}