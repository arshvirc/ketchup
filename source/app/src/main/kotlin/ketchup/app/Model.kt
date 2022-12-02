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
import java.io.File


class Model() {
    enum class URFlag {
        UNDO,
        REDO,
        NEITHER
    }

    // Api Fields
    private val apiUrl = "http://127.0.0.1:3000"
    val api = Client(apiUrl)
    lateinit var previousController: MainController

    var onEditableField = false;

    // Ui Fields
    lateinit var displayList: ObservableList<Node>                                                    // Displayed List
    lateinit var displayState: String                                                                 // displayState
    var uiListOfAllItems: ObservableList<Node> = FXCollections.observableArrayList<Node>()            // Master List

    var archiveList: ObservableList<Node> = FXCollections.observableArrayList<Node>()

    // Item Versions
    var dbListOfAllItems =  TodoList()
    var dbArchiveList = TodoList()
    var listOfTags: MutableList<String> = mutableListOf<String>()

    var selectedItemId = -1;

    // Fixed Information
    val listOfPriorities: List<String> = listOf<String>("None", "Low", "Medium", "High")

    // For undo/redo
    var undoStack : ArrayDeque<State> = ArrayDeque<State>()
    var redoStack : ArrayDeque<State> = ArrayDeque<State>()

    var dragInitiated = false
    lateinit var draggedItemId : String
    var dragTop = false
    var dragBottom = false
    private var theme : String = "default"
    private var saveFileName = "model.json"
    private val saveFile = File(saveFileName)

    fun getTheme() : String { return theme }
    fun setTheme(name : String) {
        this.theme = name
        saveFile.writeText(theme)
        // this is quite bug-prone, so be sure to fix!
    }

    private fun undoRedoEdit(action : Action, item : TodoItem, flag: URFlag) {
        val id = item.id.toString()
        when (action) {
            Action.EDIT_TITLE -> editToDoItem(id, action, item.title, flag)
            Action.EDIT_DESC -> editToDoItem(id, action, item.description, flag)
            Action.EDIT_TAGS -> editToDoItem(id, action, item.tags, flag)
            Action.EDIT_PRIORITY -> editToDoItem(id, action, item.priority, flag)
            // Issue: deadline can go from being non-null to being null and vice versa through undos/redos. How do we account for
            // this? As a temporary solution I've changed the parameter type for editToDoItem from Any to Any?, but this is
            // probably not ideal in general.
            Action.EDIT_DEADLINE ->  editToDoItem(id, action, item.deadline, flag)
            Action.EDIT_COMPLETE -> editToDoItem(id, action, item.completion, flag)
            else -> println("Not implemented yet!")
        }
    }
    fun undo() {
        if (undoStack.isEmpty()) return
        val lastState = undoStack.removeLast()
        val action = lastState.action
        val lastItem = lastState.item
        val id = lastItem.id.toString()

        when (action) {
            Action.ADD -> permaDeleteItem(id, URFlag.UNDO)
            Action.TRASH -> unarchiveItem(id, URFlag.UNDO)
            Action.UNARCHIVE -> trashItem(id, URFlag.UNDO)
            Action.PERMA_DELETE -> addItemToList(lastItem, URFlag.UNDO)
            else -> undoRedoEdit(action, lastItem, URFlag.UNDO)
        }
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val nextState = redoStack.removeLast()
        val action = nextState.action
        val nextItem = nextState.item
        val id = nextItem.id.toString()

        when (action) {
            Action.ADD -> addItemToList(nextItem, URFlag.REDO)
            Action.TRASH -> trashItem(id, URFlag.REDO)
            Action.UNARCHIVE -> unarchiveItem(id, URFlag.REDO)
            Action.PERMA_DELETE -> permaDeleteItem(id, URFlag.REDO)
            else -> undoRedoEdit(action, nextItem, URFlag.REDO)
        }
    }
    
    /*
    Critical Function: loadState()
    - makes the api calls and populates uiListOfAllitems and archiveList
     */
    private fun loadState() {
        if (saveFile.exists()) {
            theme = saveFile.readText()
        } else {
            theme = "default"
        }
        val mainList = runBlocking { api.getListById(0)?.list ?: mutableListOf<TodoItemResponse>() }
        val archive = runBlocking { api.getArchive()?.list ?: mutableListOf<TodoItemResponse>() }
        uiListOfAllItems.clear()
        archiveList.clear()
        dbListOfAllItems = TodoList()
        dbArchiveList = TodoList()

        mainList.forEach { item ->
            val date = item.deadline?.let { Date(it.toLong()) }
            val tags = item.tags ?: mutableListOf()
            val newItem = TodoItem(id = item.id, title = item.title, description = item.description,
                priority = item.priority, completion = item.completion, deadline = date, tags = tags)
            dbListOfAllItems.addItem(newItem)
        }

        archive.forEach{item ->
            val date = item.deadline?.let { Date(it.toLong()) }
            val tags = item.tags ?: mutableListOf()
            val newItem = TodoItem(id = item.id, title = item.title, description = item.description,
                priority = item.priority, completion = item.completion, deadline = date, tags = tags)
            dbArchiveList.addItem(newItem)
        }

        listOfTags.clear()

        val apiTags = runBlocking { api.getAllTags() }
        for( tag in apiTags) listOfTags.add(tag)
        uiListOfAllItems.addAll(todoListConverter(this.dbListOfAllItems, false))
        archiveList.addAll(todoListConverter(this.dbArchiveList, true))
    }

    constructor(list: ObservableList<Node>, c: MainController) : this() {
        previousController = c
        displayList = list
        displayState = "All Tasks"

        loadState()
        refreshDisplayedList()
    }

    /*  Critical Function: refreshDisplayedList()
     *  Used by:
     */
    fun refreshDisplayedList() {
        loadState()

        val itemComponents = uiListOfAllItems.map { it as ItemComponent }
        val archiveComponents = archiveList.map{ it as ItemComponent }
        var temp = Date(System.currentTimeMillis())
        val today = atStartOfDay(temp)
        var filteredList: List<ItemComponent>
        displayList.clear()
        when (displayState) {
            "All Tasks" -> {
                filteredList = itemComponents.filter {
                    it.item.id > 0
                }
            }
            "Today" -> {
                filteredList = itemComponents.filter {
                    it.item.deadline?.equals(today) ?: false
                }
            }
            "Upcoming" -> {
                filteredList = itemComponents.filter {
                    it.item.deadline?.after(today) ?: false
                }
            }
            "Trash" -> {
                filteredList = archiveComponents
            }
            "Completed" -> {
                filteredList = itemComponents.filter {
                    it.item.completion
                }
            }
            else -> {
                filteredList = itemComponents.filter { it.item.tags.contains(displayState) }
            }
        }

        val completedItems = filteredList.filter { it.item.completion }
        val unCompletedItems = filteredList.filter { !it.item.completion }

        displayList.addAll(unCompletedItems)
        displayList.addAll(completedItems)
    }

    /*  Critical Function: addItemToList(dbItem)
     *      dbItem -> the item that is converted into an ItemComponent
     *  Used by: Constructor and Add Controller
     */
    fun addItemToList(dbItem: TodoItem, flag : URFlag = URFlag.NEITHER) {
        val itemId = runBlocking { api.createTodoItem(0, dbItem) }
        if (itemId == -1) {
            println("Adding TodoItem failed.")
        } else {
            dbItem.id = itemId
            println("New item id: ${dbItem.id}")
            this.dbListOfAllItems.addItem(dbItem.copy())       // Update model list
            var itemUI = ItemComponent(dbItem.copy(), this, false)  //Convert to UI Component
            itemUI.isExpanded = false
            this.uiListOfAllItems.add(itemUI)              //Update UI List
            refreshDisplayedList()

            if (flag == URFlag.UNDO) {
                redoStack.addLast(State(Action.PERMA_DELETE, dbItem.copy()))
            } else if (flag == URFlag.REDO) {
                undoStack.addLast(State(Action.ADD, dbItem.copy()))
            } else {
                undoStack.addLast(State(Action.ADD, dbItem.copy()))
                redoStack.clear()
            }
        }
    }

    fun trashItem(id : String, flag : URFlag = URFlag.NEITHER) {
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

        if (flag == URFlag.UNDO) {
            redoStack.addLast(State(Action.UNARCHIVE, item.copy()))
        } else if (flag == URFlag.REDO) {
            undoStack.addLast(State(Action.TRASH, item.copy()))
        } else {
            undoStack.addLast(State(Action.TRASH, item.copy()))
            redoStack.clear()
        }
        uiListOfAllItems.removeAt(idx)
        refreshDisplayedList()
    }

    /*  Critical Function: todoListConverter(dbList)
     *      dbList -> the list that is converted into a uiList
     *  Used by: Constructor
     */
    private fun todoListConverter(dbList: TodoList, archive: Boolean): ObservableList<Node> {
        val uiList = FXCollections.observableArrayList<Node>()
        for (dbItem in dbList.list) {
            var uiItem = ItemComponent(dbItem, this, archive)
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

    /*  Critical Function: editToDoItem(id, field, change)
     *      id -> the id of the item to be edited
     *      field -> the item's field that needs to be changed
     *      newVal -> the updated value
     *  used by event listeners
     */
    fun editToDoItem(id: String, action : Action, newVal: Any?, flag : URFlag = URFlag.NEITHER) {
        var item = TodoItem()
        var index = 0
        for (i in 0.. dbListOfAllItems.list.lastIndex) {
            if (dbListOfAllItems.list[i].id == id.toInt()) {
                index = i
                item = dbListOfAllItems.list[i]
                break
            }
        }
        val oldItem = item.copy()
        var changed = false

        when (action) {
            Action.EDIT_TITLE -> {
                var title = newVal as String
                if (title != item.title) {
                    item.title = title
                    changed = true
                }
            }
            Action.EDIT_DESC -> {
                var desc = newVal as String
                if (item.description != desc) {
                    changed = true
                }
                if (desc.trim() == "") {
                    item.description = " "
                } else {
                    item.description = desc
                }
            }
            Action.EDIT_TAGS -> {
                var mutableTags = mutableListOf<String>()
                var tags = newVal as MutableList<String>
                for (t in tags) {
                    mutableTags.add(t)
                    if (!changed && item.tags.firstOrNull({it == t}) == null) {
                        changed = true
                    }
                }
                item.tags = mutableTags
            }
            Action.EDIT_PRIORITY -> {
                var p = item.priority
                if (newVal is String) {
                    item.priority = newVal.toInt()
                } else {
                    item.priority = newVal as Int
                }

                if (p != item.priority) {
                    changed = true
                }
            }
            Action.EDIT_DEADLINE -> {
                var d = item.deadline
                if (newVal is Date) {
                    item.deadline = newVal
                } else {
                    var deadline = newVal as LocalDate?
                    if (deadline != null) {
                        val instant = Instant.from(deadline.atStartOfDay(ZoneId.systemDefault()))
                        val date = Date.from(instant)
                        item.deadline = date
                    } else {
                        item.deadline = null;
                    }
                }
                if (item.deadline != d) {
                    changed = true
                }
            }

            Action.EDIT_COMPLETE -> {
                var status = newVal as Boolean
                item.completion = status
                changed = true
            }

            else -> {
                return error("Wrong Field Value")
            }
        }
        if (!changed) {
            return
        }
        if (flag == URFlag.UNDO) {
            redoStack.addLast(State(action, oldItem))
        } else if (flag == URFlag.REDO) {
            undoStack.addLast(State(action, oldItem))
        } else {
            undoStack.addLast(State(action, oldItem))
            redoStack.clear()
        }

        if(displayState != "Trash") {
            val editSuccess = runBlocking { api.editTodoItem(id.toInt(), item) }
            if (!editSuccess) {
                println("Editing tags for item with ID $id failed")
            }
            // UPDATE UI Part Now
            val uiItem = uiListOfAllItems.find { (it as ItemComponent).item.id == id.toInt() }
            uiListOfAllItems[index] = ItemComponent(item, this, false)
            refreshDisplayedList()
        }
    }

    fun unarchiveItem(id: String, flag: URFlag = URFlag.NEITHER) {
        val success = runBlocking {
            api.unarchiveItem(id.toInt())
        }
        refreshDisplayedList()
        var idx = 0
        var item = TodoItem()
        for (i in 0..uiListOfAllItems.size) {
            if (uiListOfAllItems[i].id == id) {
                idx = i
                item = (uiListOfAllItems[i] as ItemComponent).item
                break
            }
        }

        if (flag == URFlag.UNDO) {
            redoStack.addLast(State(Action.TRASH, item.copy()))
        } else if (flag == URFlag.REDO) {
            undoStack.addLast(State(Action.UNARCHIVE, item.copy()))
        } else {
            undoStack.addLast(State(Action.UNARCHIVE, item.copy()))
            redoStack.clear()
        }
    }

    fun permaDeleteItem(id: String, flag: URFlag = URFlag.NEITHER) {

        var idx = 0
        var item = TodoItem()
        for (i in 0..uiListOfAllItems.size) {
            if (uiListOfAllItems[i].id == id) {
                idx = i
                item = (uiListOfAllItems[i] as ItemComponent).item
                break
            }
        }

        val success = runBlocking { api.deleteTodoItem(id.toInt()) }
        val success2 = runBlocking { api.deleteTodoItem(id.toInt()) }
        refreshDisplayedList()

        if (flag == URFlag.UNDO) {
            redoStack.add(State(Action.ADD, item.copy()))
        } else if (flag == URFlag.REDO) {
            undoStack.add(State(Action.PERMA_DELETE, item.copy()))
        } // else, do nothing! perma-deleting items should not be undoable
    }

    /*  Helper Function: findUiIndexById(id)
     *      id -> the id of the item to be found
     *  used by event listeners; returns -1 if not found
     */
    private fun findUiIndexById(id: String): Int {
        val item = displayList.find { (it as ItemComponent).item.id == id.toInt() }
        return displayList.indexOf(item)
    }

    fun chooseSelectedItem(id: Int) {
        selectedItemId = id
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
