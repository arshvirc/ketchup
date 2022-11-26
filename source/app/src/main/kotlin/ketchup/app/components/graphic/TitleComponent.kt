package ketchup.app.components.graphic
import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.TextField
import ketchup.app.components.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking

class TitleComponent: TextField {
    private var toDoItemId : String
    private var model: Model
    private var api: Client
    constructor(item: TodoItem, model: Model) {
        this.maxHeight = 30.0
        this.maxWidth = 1.7976931348623157E308
        this.minHeight = 30.0
        this.minWidth = Double.NEGATIVE_INFINITY
        this.prefHeight = 30.0
        this.prefWidth = 102.0

        this.text = item.title
        toDoItemId = item.id.toString()
        this.model = model
        this.api = model.api


        this.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Title Field to have the following title: ${this.text}")
                    val editedItem = model.editToDoItem(toDoItemId, "title", this.text)
                    // val editedItem = editToDoItem(model.dbListOfAllItems, toDoItemId, this.text)
                    updateEditedItem(toDoItemId, editedItem)

                }
            }
        }
    }

    private fun editToDoItem(list: TodoList, id: String, title: String): TodoItem {
        var item: TodoItem
        for (i in 0..model.dbListOfAllItems.list.lastIndex) {
            item = model.dbListOfAllItems.list[i]
            if (item.id == id.toInt()) {
                item.title = title
                val editSuccess = runBlocking { api.editTodoItem(id.toInt(), item) }
                if(!editSuccess) {
                    println("Editing title for item with ID $id failed.")
                }
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