package ketchup.app.components.content
import Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.TextField
import ketchup.app.components.graphic.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking

class DescriptionComponent: TextField {
    private var toDoItemId : String
    private var model: Model
    private val api: Client

    constructor(item: TodoItem, m :Model) {
        this.prefHeight = 20.0
        this.prefWidth = 331.0
        //this.padding = 10.0

        if (item.description == "") {
            this.promptText = "Enter Details"
        } else {
            this.text = item.description
        }

        this.toDoItemId = item.id.toString()
        this.model = m
        this.api = m.api
        this.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Description to be ${this.text}")
                    val editedItem = editToDoItem(model.dbListOfAllItems, toDoItemId, this.text)
                    updateEditedItem(toDoItemId, editedItem)

                }
            }
        }
    }

    private fun editToDoItem(list: TodoList, id: String, desc: String): TodoItem {
        var item: TodoItem
        for (i in 0..model.dbListOfAllItems.list.lastIndex) {
            item = model.dbListOfAllItems.list[i]
            if (item.id == id.toInt()) {
                if(desc.trim() == "") {
                    item.description = " "
                } else {
                    item.description = desc
                }
                val editSuccess = runBlocking { api.editTodoItem(id.toInt(), item) }
                if(!editSuccess) {
                    println("Editing description for item with ID $id failed")
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