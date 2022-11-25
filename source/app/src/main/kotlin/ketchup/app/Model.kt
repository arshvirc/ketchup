package ketchup.app

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.TitledPane
import ketchup.app.components.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.app.ktorclient.TodoItemResponse
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking
import java.util.*


class Model() {
    // Api Fields
    private val apiUrl = "http://127.0.0.1:3000"
    val api = Client(apiUrl)

    // Ui Fields
    lateinit var displayList: ObservableList<Node>
    lateinit var uiListOfAllItems: ObservableList<Node>
    var uiListOfTags = mutableListOf<ObservableList<Node>>()

    // Item Versions
    var dbListOfAllItems =  TodoList()                                // Contains all TodoItems
    var listOfTags: MutableList<String> = mutableListOf<String>()

    // Fixed Information
    val listOfPriorities: List<String> = listOf<String>("None", "Low", "Medium", "High")

    // Drag Global Information (Try to find a better version of these)
    var dragInitiated = false
    lateinit var draggedItemId : String
    var dragTop = false
    var dragBottom = false

    constructor(list: ObservableList<Node>) : this() {
        val mainList = runBlocking { api.getListById(0)?.list ?: mutableListOf<TodoItemResponse>() }
        for(item in mainList) {
            val date = item.deadline?.let { Date(it.toLong()) }
            val newItem = TodoItem(id = item.id, title = item.title, description = item.description,
                priority = item.priority, completion = item.completion, deadline = date)
            dbListOfAllItems.addItem(newItem)
        }
        this.uiListOfAllItems = list
        this.uiListOfAllItems.addAll(todoListConverter(this.dbListOfAllItems))

        val apiTags = runBlocking { api.getAllTags() }
        for( tag in apiTags) listOfTags.add(tag)
        sortUiLists()
    }


    // addItemToList(dbItem: TodoItem)
    fun addItemToList(dbItem: TodoItem) {
        val itemId = runBlocking { api.createTodoItem(0, dbItem) }
        if (itemId == -1) {
            println("Adding TodoItem failed.")
        } else {
            dbItem.id = itemId
            println("New item id: ${dbItem.id}")
            this.dbListOfAllItems.addItem(dbItem)       // Update model list
            var itemUI = ItemComponent(dbItem, this)  //Convert to UI Component
            itemUI.isExpanded = false
            this.uiListOfAllItems.add(itemUI)              //Update UI List
        }
    }

    // todoListConverter(dbList: TodoList):
    private fun todoListConverter(dbList: TodoList): ObservableList<Node> {
        val uiList = FXCollections.observableArrayList<Node>()
        for (dbItem in dbList.list) {
            var uiItem = ItemComponent(dbItem, this)
            uiItem.isExpanded = false
            uiList.add(uiItem)
        }
        return uiList
    }


    /* findItemById(id) returns the index of the item in the given string or -1 if not in the list */
    private fun findItemById(id: String): Int {
        for ((counter, item) in uiListOfAllItems.withIndex()) {
            if (item.id == id) {
                return counter;
            }
        }
        return -1
    }


    private fun sortUiLists() {
        uiListOfTags.removeAll { (it as ItemComponent).item.id > 0 }
        for ( (counter, group) in listOfTags.withIndex()) {
            val listWithGroup = uiListOfAllItems.filter {
                (it as ItemComponent).item.tags.contains(group)
            }
            val obsList = FXCollections.observableArrayList<Node>()
            for (item in listWithGroup) {
                obsList.add(item)
            }
            uiListOfTags.add(obsList)
        }
    }

    fun moveItems(srcId: String, destId: String) {
        var srcIndex = findItemById(srcId)
        var destIndex = findItemById(destId);
        println("(srcId, destId): ($srcId, $destId)")
        println("(srcIndex, destIndex): ($srcIndex, $destIndex)")
        var gap = FXCollections.observableArrayList<Node>()
        var after = FXCollections.observableArrayList<Node>()
        var moveItem = TitledPane() as Node
        if ( srcIndex == destIndex) {
            println("Useless Call")
        } else if ( srcIndex < destIndex) {
            for ((counter, item) in uiListOfAllItems.withIndex()) {
                if (counter < srcIndex) {
                    // keep in the list
                } else if ( counter == srcIndex ) {
                    moveItem = item
                } else if ( counter < destIndex ) {
                    gap.add(item)
                } else if ( counter == destIndex ) {
                    if (this.dragTop) {
                        after.add(item)
                    } else {
                        gap.add(item)
                    }
                } else {
                    after.add(item)
                }
            }
            uiListOfAllItems.removeAll(moveItem)
            uiListOfAllItems.removeAll(gap)
            uiListOfAllItems.removeAll(after)
            uiListOfAllItems.addAll(gap)
            uiListOfAllItems.addAll(moveItem)
            uiListOfAllItems.addAll(after)
        } else {
            for ((counter, item) in uiListOfAllItems.withIndex()) {
                if (counter < destIndex) {
                    // keep in the list
                } else if ( counter == destIndex ) {
                    if (this.dragTop) {
                        gap.add(item)
                    } else {
                        // keep in the list
                    }
                } else if ( counter < srcIndex ) {
                    gap.add(item)
                } else if ( counter == srcIndex ) {
                    moveItem = item
                } else {
                    after.add(item)
                }
            }
            uiListOfAllItems.removeAll(gap)
            uiListOfAllItems.removeAll(moveItem)
            uiListOfAllItems.removeAll(after)
            uiListOfAllItems.addAll(moveItem)
            uiListOfAllItems.addAll(gap)
            uiListOfAllItems.addAll(after)
        }

    }
}
