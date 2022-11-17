package ketchup.app.components.content

import Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.ComboBox
import ketchup.app.components.graphic.ItemComponent
import ketchup.console.TodoItem
import ketchup.console.TodoList

class PriorityOptionsComponent: ComboBox<String> {
    var model: Model
    var toDoItemId: String
    constructor(item: TodoItem, m: Model) {
        this.prefHeight = 26.0
        this.prefWidth = 120.0

        this.model = m
        this.toDoItemId = item.id.toString()

        this.value = item.priority.toString()
        this.items.addAll(m.listOfPriorities)
        this.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Priority to be ${this.value}")
                    val editedItem = editToDoItem(model.dbListOfAllItems, toDoItemId, this.value)
                    updateEditedItem(toDoItemId, editedItem)

                }
            }
        }
    }
    private fun editToDoItem(list: TodoList, id: String, priority: String): TodoItem {
        var item: TodoItem
        for (i in 0..model.dbListOfAllItems.list.lastIndex) {
            item = model.dbListOfAllItems.list[i]
            if (item.id == id.toInt()) {
                item.priority = priority.toInt()
                return item
            }
        }
        item = TodoItem()
        return item
    }

    private fun updateEditedItem( completedID: String, dbItem: TodoItem) {
        val newItem = ItemComponent(dbItem, model)
        var newList = FXCollections.observableArrayList<Node>()
        newList.addAll(newItem)
        var FOUND = false
        var beforeList = FXCollections.observableArrayList<Node>()
        var oldList = FXCollections.observableArrayList<Node>()
        var afterList = FXCollections.observableArrayList<Node>()
        for (item in model.uiListOfAllItems) {
            if ( item.id == completedID ) {
                oldList.add(item)
                FOUND = true
            } else if ( FOUND ) {
                afterList.add(item)
            } else {
                beforeList.add(item)
            }
        }
        model.uiListOfAllItems.removeAll(oldList)
        model.uiListOfAllItems.removeAll(afterList)
        model.uiListOfAllItems.addAll(newList)
        model.uiListOfAllItems.addAll(afterList)
    }
}