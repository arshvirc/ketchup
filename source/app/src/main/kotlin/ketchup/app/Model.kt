package ketchup.app

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.TitledPane
import ketchup.app.components.ItemComponent
import ketchup.app.controllers.MainController
import ketchup.app.ktorclient.Client
import ketchup.app.ktorclient.TodoItemResponse
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class Model() {
    // Api Fields
    private val apiUrl = "http://127.0.0.1:3000"
    val api = Client(apiUrl)
    lateinit var previousController: MainController

    // Ui Fields
    lateinit var displayList: ObservableList<Node>                                                    // Displayed List
    lateinit var displayState: String                                                                 // displayState
    var uiListOfAllItems: ObservableList<Node> = FXCollections.observableArrayList<Node>()            // Master List

    // Item Versions
    var dbListOfAllItems =  TodoList()
    var listOfTags: MutableList<String> = mutableListOf<String>()

    // Fixed Information
    val listOfPriorities: List<String> = listOf<String>("None", "Low", "Medium", "High")

    // Drag Global Information (Try to find a better version of these)
    lateinit var draggedItemId : String
    var dragTop = false
    var dragBottom = false

    constructor(list: ObservableList<Node>, c: MainController) : this() {
        val mainList = runBlocking { api.getListById(0)?.list ?: mutableListOf<TodoItemResponse>() }
        for(item in mainList) {
            val date = item.deadline?.let { Date(it.toLong()) }
            val tags = item.tags ?: mutableListOf()
            val newItem = TodoItem(id = item.id, title = item.title, description = item.description,
                priority = item.priority, completion = item.completion, deadline = date, tags = tags)
            dbListOfAllItems.addItem(newItem)
        }
        previousController = c

        val apiTags = runBlocking { api.getAllTags() }
        for( tag in apiTags) listOfTags.add(tag)
        displayList = list
        uiListOfAllItems.addAll(todoListConverter(this.dbListOfAllItems))

        // Todo: Add functionality to recover what was the last screen display list
        // val displayType = api.call
        // displayListByType(displayType)

        // hard coded to display all items
        displayState = "All Tasks"
        displayListByType(displayState)
    }

    fun displayListByType(type: String) {
        displayList.removeAll{ (it as ItemComponent).item.id > 0 }
        when (type) {
            "All Tasks" -> {
                displayList.addAll(uiListOfAllItems)
            }
            "Today" -> {
                // TODO Implement this

            }
            "Upcoming" -> {
                // TODO Implement so that stuff not today comes up in order of deadline
                println("$type - Not Implemented")
            }
            else -> {
                val filteredList : ObservableList<Node>  = FXCollections.observableArrayList<Node>()
                for ( item in uiListOfAllItems) {
                    var component = item as ItemComponent
                    if ( component.item.tags.contains(type)) {
                        filteredList.add(component)
                    }
                }
                println(filteredList)
                println(displayList)
                displayList.addAll(filteredList)
                println(displayList)
            }
        }
    }

    // addItemToList(dbItem: TodoItem)
    fun addItemToList(dbItem: TodoItem) {
        val itemId = runBlocking { api.createTodoItem(0, dbItem) }
        if (itemId == -1) {
            println("Adding TodoItem failed.")
        } else {
            dbItem.id = itemId
            println("New item id: ${dbItem.id}")
            this.dbListOfAllItems.addItem(dbItem)             // Update model list
            var itemUI = ItemComponent(dbItem, this)  //Convert to UI Component
            itemUI.isExpanded = false
            uiListOfAllItems.add(itemUI)              //Update UI List
            displayListByType(displayState)
        }
    }

    // todoListConverter(dbList: TodoList):
    private fun todoListConverter(dbList: TodoList): ObservableList<Node> {
        val uiList = FXCollections.observableArrayList<Node>()
        for (dbItem in dbList.list) {
            dbItem.printItem()
            var uiItem = ItemComponent(dbItem, this)
            uiItem.isExpanded = false
            uiList.add(uiItem)
        }
        return uiList
    }


    /* findItemById(id) returns the index of the item in the given string or -1 if not in the list */
    private fun findItemByIdForDrag(id: String): Int {
        for ((counter, item) in displayList.withIndex()) {
            if (item.id == id) {
                return counter;
            }
        }
        return -1
    }

    fun moveItemsForDrag(srcId: String, destId: String) {
        var srcIndex = findItemByIdForDrag(srcId)
        var destIndex = findItemByIdForDrag(destId);
        println("(srcId, destId): ($srcId, $destId)")
        println("(srcIndex, destIndex): ($srcIndex, $destIndex)")
        var gap = FXCollections.observableArrayList<Node>()
        var after = FXCollections.observableArrayList<Node>()
        var moveItem = TitledPane() as Node
        if ( srcIndex == destIndex) {
            println("Useless Call")
        } else if ( srcIndex < destIndex) {
            for ((counter, item) in displayList.withIndex()) {
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
            displayList.removeAll(moveItem)
            displayList.removeAll(gap)
            displayList.removeAll(after)
            displayList.addAll(gap)
            displayList.addAll(moveItem)
            displayList.addAll(after)
        } else {
            for ((counter, item) in displayList.withIndex()) {
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
            displayList.removeAll(gap)
            displayList.removeAll(moveItem)
            displayList.removeAll(after)
            displayList.addAll(moveItem)
            displayList.addAll(gap)
            displayList.addAll(after)
        }

    }

    fun editToDoItem(id: String, field: String, change: Any): TodoItem {
        var item = TodoItem()
        for (i in 0.. dbListOfAllItems.list.lastIndex) {
            item = dbListOfAllItems.list[i]
            if (item.id == id.toInt()) {
                break
            }
        }
        when (field) {
            "title" -> {
                var title = change as String
                item.title = title
            }
            "desc" -> {
                var desc = change as String
                if (desc.trim() == "") {
                    item.description = " "
                } else {
                    item.description = desc
                }
            }
            "tags" -> {
                var mutableTags = mutableListOf<String>()
                var tags = change as ObservableList<String>
                for (item in tags) mutableTags.add(item)
                item.tags = mutableTags
            }
            "priority" -> {
                var priority = change as Int
                item.priority = priority
            }
            "deadline" -> {
                var deadline = change as LocalDate?
                if(deadline != null) {
                    val instant = Instant.from(deadline.atStartOfDay(ZoneId.systemDefault()))
                    val date = Date.from(instant)
                    item.deadline = date   /* Update this to include  */
                } else {
                    item.deadline = null;
                }
            }
            else -> return error("Wrong Field Value")
        }
        val editSuccess = runBlocking { api.editTodoItem(id.toInt(), item) }
        if(!editSuccess) {
            println("Editing tags for item with ID $id failed")
        }
        return item
    }
}
