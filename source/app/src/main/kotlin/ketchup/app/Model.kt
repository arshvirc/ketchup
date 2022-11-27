package ketchup.app

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import ketchup.app.components.ItemComponent
import ketchup.app.controllers.MainController
import ketchup.app.ktorclient.Client
import ketchup.app.ktorclient.TodoItemResponse
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking
import java.time.*
import java.util.*
import kotlin.collections.ArrayDeque
import ketchup.app.State
import ketchup.app.Action


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

    // For undo/redo
    var undoStack : ArrayDeque<State> = ArrayDeque<State>()
    var redoStack : ArrayDeque<State> = ArrayDeque<State>()

    var dragInitiated = false
    lateinit var draggedItemId : String
    var dragTop = false
    var dragBottom = false

    fun undo() {
        if (undoStack.isEmpty()) return
        val lastState = undoStack.removeLast()
        redoStack.addLast(lastState)
        if (lastState.action == Action.ADD) {
            deleteItemFromList(lastState.item.id.toString(), false)
        } else if (lastState.action == Action.EDIT) {
            println("Not implemented yet!")
        } else if (lastState.action == Action.DELETE) {
            addItemToList(lastState.item, false)
        } else { // lastState.action == Action.COMPLETE
            println("Not implemented yet!")
        }
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val nextState = redoStack.removeLast()
        undoStack.addLast(nextState)
        if (nextState.action == Action.ADD) {
            addItemToList(nextState.item, false)
        } else if (nextState.action == Action.EDIT) {
            println("Not implemented yet!")
        } else if (nextState.action == Action.DELETE) {
            deleteItemFromList(nextState.item.id.toString(), false)
        } else { // nextState.action == Action.COMPLETE
            println("Not implemented yet!")
        }
    }

    constructor(list: ObservableList<Node>) : this() {}
    constructor(list: ObservableList<Node>, c: MainController) : this() {
        previousController = c
        displayList = list
        displayState = "All Tasks"

        val mainList = runBlocking { api.getListById(0)?.list ?: mutableListOf<TodoItemResponse>() }
        for(item in mainList) {
            val date = item.deadline?.let { Date(it.toLong()) }
            val tags = item.tags ?: mutableListOf()
            val newItem = TodoItem(id = item.id, title = item.title, description = item.description,
                priority = item.priority, completion = item.completion, deadline = date, tags = tags)
            dbListOfAllItems.addItem(newItem)
        }

        val apiTags = runBlocking { api.getAllTags() }
        for( tag in apiTags) listOfTags.add(tag)
        uiListOfAllItems.addAll(todoListConverter(this.dbListOfAllItems))
        refreshDisplayedList()
    }

    /*  Critical Function: refreshDisplayedList()
     *  Used by:
     */
    fun refreshDisplayedList() {
        val itemComponents = uiListOfAllItems.map { it as ItemComponent }
        var temp = Date(System.currentTimeMillis())
        val today = atStartOfDay(temp)
        displayList.clear()
        when (displayState) {
            "All Tasks" -> displayList.addAll(uiListOfAllItems)
            "Today" -> {
                val filteredList = itemComponents.filter {
                    it.item.deadline?.equals(today) ?: false
                }
                displayList.addAll(filteredList)
            }
            "Upcoming" -> {
                val filteredList = itemComponents.filter {
                    it.item.deadline?.after(today) ?: false
                }
                displayList.addAll(filteredList)
            }
            else -> {
                val filteredList = itemComponents.filter { it.item.tags.contains(displayState) }
                displayList.addAll(filteredList)
            }
        }
    }

    /*  Critical Function: addItemToList(dbItem)
     *      dbItem -> the item that is converted into an ItemComponent
     *  Used by: Constructor and Add Controller
     */
    fun addItemToList(dbItem: TodoItem, changeState : Boolean = true) {
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
            refreshDisplayedList()
            if (changeState) {
                undoStack.addLast(State(Action.ADD, dbItem))
                redoStack.clear()
            }
        }
    }

    /*  Critical Function: todoListConverter(dbList)
     *      dbList -> the list that is converted into a uiList
     *  Used by: Constructor
     */
    private fun todoListConverter(dbList: TodoList): ObservableList<Node> {
        val uiList = FXCollections.observableArrayList<Node>()
        for (dbItem in dbList.list) {
            var uiItem = ItemComponent(dbItem, this)
            uiList.add(uiItem)
        }
        return uiList
    }

    /*  Critical Function: moveItem(itemId, destId, moveAboveDest)
     *      itemId -> the id of the item you want to move
     *      destId -> helps you find the index to move the item to
     *      moveAboveDest -> true or false
     *  used by event drag listeners
     */
    fun moveItem(itemId: String, destId: String, moveAboveDest: Boolean) {
        var fromIndex = findUiIndexById(itemId)
        var destIndex = findUiIndexById(destId)
        if (moveAboveDest) destIndex -= 1  //adjusts it so we item[1], item[2], item[3], ...  item[destIndex], movedItem
        if (fromIndex == destIndex) {
            println("Misclicked")
            return
        }
        var itemComponents = displayList.map { it as ItemComponent }
        var list1 : List<ItemComponent>
        var tempList: List<ItemComponent>
        var list2 = mutableListOf<ItemComponent>()
        var list3: List<ItemComponent>
        var item = itemComponents[fromIndex]

        if ( fromIndex < destIndex) {
            list1 = itemComponents.slice(0 until fromIndex)
            tempList = itemComponents.slice(fromIndex + 1 ..destIndex)
            list3 = itemComponents.slice(destIndex + 1 ..itemComponents.lastIndex)
            list2.addAll(tempList)
            list2.add(item)
        } else {
            list1 = itemComponents.slice(0 .. destIndex)
            tempList = itemComponents.slice(destIndex + 1 until fromIndex)
            list3 = itemComponents.slice(fromIndex + 1 ..itemComponents.lastIndex)
            list2.add(item)
            list2.addAll(tempList)
        }
        displayList.clear()
        displayList.addAll(list1)
        displayList.addAll(list2)
        displayList.addAll(list3)
    }

    fun deleteItemFromList(id : String, changeState : Boolean = true) {
        // Remove from database
        val deleteSuccess = runBlocking { api.deleteTodoItem(id.toInt()) }
        if (!deleteSuccess) {
            println("Deleting item with ID ${id} failed.")
            return
        }
        var idx = 0
        var item = TodoItem()
        for (i in 0..uiListOfAllItems.size) {
            if (uiListOfAllItems[i].id == id) {
                idx = i
                item = (uiListOfAllItems[i] as ItemComponent).item
                break
            }
        }
        uiListOfAllItems.removeAt(idx)
        if (changeState) {
            undoStack.addLast(State(Action.DELETE, item))
            redoStack.clear()
        }
    }



    /*  Critical Function: editToDoItem(id, field, change)
     *      id -> the id of the item to be edited
     *      field -> the item's field that needs to be changed
     *      newVal -> the updated value
     *  used by event listeners
     */
    fun editToDoItem(id: String, field: String, newVal: Any, changeState : Boolean = true) {
        var item = TodoItem()
        for (i in 0.. dbListOfAllItems.list.lastIndex) {
            item = dbListOfAllItems.list[i]
            if (item.id == id.toInt()) {
                break
            }
        }

        if (changeState) {
            if (field == "delete") {
                undoStack.addLast(State(Action.DELETE, item))
                redoStack.clear()
            } else {
                undoStack.addLast(State(Action.EDIT, item))
                redoStack.clear()
            }
        }

        when (field) {
            "title" -> {
                var title = newVal as String
                item.title = title
            }
            "desc" -> {
                var desc = newVal as String
                if (desc.trim() == "") {
                    item.description = " "
                } else {
                    item.description = desc
                }
            }
            "tags" -> {
                var mutableTags = mutableListOf<String>()
                var tags = newVal as ObservableList<String>
                for (item in tags) mutableTags.add(item)
                item.tags = mutableTags
            }
            "priority" -> {
                var priority = newVal as String
                item.priority = priority.toInt()
            }
            "deadline" -> {
                var deadline = newVal as LocalDate?
                if(deadline != null) {
                    val instant = Instant.from(deadline.atStartOfDay(ZoneId.systemDefault()))
                    val date = Date.from(instant)
                    item.deadline = date
                } else {
                    item.deadline = null;
                }
            }
            "delete" -> {}
            else -> return error("Wrong Field Value")
        }
        if (field != "delete") {
            val editSuccess = runBlocking { api.editTodoItem(id.toInt(), item) }
            if (!editSuccess) {
                println("Editing tags for item with ID $id failed")
            }
        } else {
            val deleteSuccess = runBlocking { api.deleteTodoItem(id.toInt()) }
            if(!deleteSuccess) {
                println("Deleting item with ID $id failed.")
            }
        }
        // UPDATE UI Part Now
        val index = findUiIndexById(id)
        val list1 = uiListOfAllItems.slice(0 until index)
        val list2 = uiListOfAllItems.slice(index+1 .. uiListOfAllItems.lastIndex)
        val uiItem = ItemComponent(item, this)

        uiListOfAllItems.clear()
        uiListOfAllItems.addAll(list1)
        if (field != "delete") uiListOfAllItems.add(uiItem)
        uiListOfAllItems.addAll(list2)
        refreshDisplayedList()
    }

    /*  Helper Function: findUiIndexById(id)
     *      id -> the id of the item to be found
     *  used by event listeners; returns -1 if not found
     */
    private fun findUiIndexById(id: String): Int {
        val item = displayList.find { (it as ItemComponent).item.id == id.toInt() }
        return displayList.indexOf(item)
    }
}


// HELPER FUNCTIONS
private fun atStartOfDay(date: Date): Date? {
    val localDateTime: LocalDateTime = dateToLocalDateTime(date)
    val startOfDay: LocalDateTime = localDateTime.with(LocalTime.MIN)
    return localDateTimeToDate(startOfDay)
}

private fun dateToLocalDateTime(date: Date): LocalDateTime {
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
}

private fun localDateTimeToDate(localDateTime: LocalDateTime): Date? {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
}
