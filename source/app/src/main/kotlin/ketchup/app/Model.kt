import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import ketchup.app.components.content.*
import ketchup.app.components.graphic.CompleteComponent
import ketchup.app.components.graphic.DragComponent
import ketchup.app.components.graphic.ItemComponent
import ketchup.app.components.graphic.TitleComponent
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
    val listOfPriorities: List<String> = listOf<String>("None", "Low", "Medium", "High")

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
        this.dbListOfAllItems = dbList
        this.uiListOfAllItems.addAll(TodoListConverter(this.dbListOfAllItems))
        this.dbListOfCompletedItems = TodoList()
        this.listOfTags = mutableListOf("Academic", "Family", "Extra")
        this.listOfGroups = mutableListOf("List 1", "List 2", "List 3")
        //this.callApiToUpdateModel()
    }

    fun callApiToUpdateModel() {
        // dbListOfAllItems = TodoList(dbListOfAllItems)               // replace w API call
        // dbListOfGroups
        // this.uiListOfAllItems = TodoListConverter(dbListOfAllItems) //
        // uiListOfGroups
        // listOfTags = mutableListOf("Academic", "Family", "Extra")
        // listOfGroups = mutableListOf("List 1", "List 2", "List 3")
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
            val itemUI = ItemComponent(dbItem, this)  //Convert to UI Component
            this.uiListOfAllItems.add(itemUI)              //Update UI List
        }
    }

    private fun TodoListConverter(dbList: TodoList): ObservableList<Node> {
        val uiList = FXCollections.observableArrayList<Node>()
        for (dbItem in dbList.list) {
            var uiItem = ItemComponent(dbItem, this)
            uiItem.isExpanded = false
            uiList.add(uiItem)
        }
        return uiList
    }
}
