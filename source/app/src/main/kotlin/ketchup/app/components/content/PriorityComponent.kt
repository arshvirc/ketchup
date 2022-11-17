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
        this.options = PriorityOptionsComponent(item, m.listOfPriorities)
        toDoItemId = item.id.toString()
        this.model = m

        this.options.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Description to be ${this.options.value}")
                    val editedItem = editToDoItem(model.dbListOfAllItems, toDoItemId, this.options.value)
                    updateEditedItem(toDoItemId, editedItem)

                }
            }
        }

        this.children.add(label)
        this.children.add(options)
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