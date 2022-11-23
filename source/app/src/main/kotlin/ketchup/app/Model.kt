import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.TitledPane
import javafx.scene.layout.Pane
import ketchup.app.components.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.app.ktorclient.TodoItemResponse
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking
import java.util.*


class Model() {
    val apiUrl = "http://127.0.0.1:3000"
    val api = Client(apiUrl)
    // We have the UI Components:
    lateinit var uiListOfAllItems: ObservableList<Node>                     // Contains the Merged List of All Items
    lateinit var uiListOfGroups: MutableList<ObservableList<Node>>          // Contains the UI Components

    // We have the Actual Information:
    lateinit var dbListOfAllItems: TodoList                                 // Contains all TodoItems
    lateinit var dbListOfGroups: MutableList<TodoList>                      // Contains the Grouped List
    lateinit var dbListOfCompletedItems: TodoList                           // Contains all CompletedItems
    lateinit var listOfTags: MutableList<String>                            // Contains the Tags
    lateinit var listOfGroups: MutableList<String>                          // Contains the Groups

    // Other Information
    val listOfPriorities: List<String> = listOf<String>("0", "1", "2", "3")


    //
    var dragInitiated = false
    lateinit var draggedItemId : String
    var dragTop = false
    var dragBottom = false

    constructor(list: ObservableList<Node>) : this() {
        val mainList = runBlocking { api.getListById(0)?.list ?: mutableListOf<TodoItemResponse>() }

        val dbList = TodoList()
        for(item in mainList) {
            val date = item.deadline?.let { Date(it.toLong()) }
            val newItem = TodoItem(id = item.id, title = item.title,
                description = item.description, priority = item.priority, completion = item.completion,
                deadline = date)
            dbList.addItem(newItem)
        }
//        dbList.displayList()

        this.uiListOfAllItems = list
        // Add Space
        this.dbListOfAllItems = dbList
        this.uiListOfAllItems.addAll(TodoListConverter(this.dbListOfAllItems))
        this.dbListOfCompletedItems = TodoList()
        this.listOfTags = mutableListOf("Academic", "Family", "Extra")
        this.listOfGroups = mutableListOf("List 1", "List 2", "List 3")
    }

    fun addItemToList(dbItem: TodoItem) {
        // Add item to Api
        val itemId = runBlocking { api.createTodoItem(0, dbItem) }
        if(itemId == -1) {
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

    private fun TodoListConverter(dbList: TodoList): ObservableList<Node> {
        val uiList = FXCollections.observableArrayList<Node>()
        for (dbItem in dbList.list) {
            var uiItem = ItemComponent(dbItem, this)
            uiItem.isExpanded = false
            uiList.add(uiItem)
            // Add Space
        }
        return uiList
    }

    fun moveItems(srcId: String, destId: String) {
        var srcIndex = 0
        var destIndex = 0;
        for ((counter, item) in uiListOfAllItems.withIndex()) {
            if (item.id == srcId) {
                srcIndex = counter;
            }
            if (item.id == destId) {
                destIndex = counter;
            }
        }
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
