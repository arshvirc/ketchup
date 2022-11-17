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
import ketchup.console.TodoItem
import ketchup.console.TodoList


class Model() {
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
    var num = 0;

    constructor(list: ObservableList<Node>) : this() {
        this.uiListOfAllItems = list
        this.dbListOfAllItems = TodoList()
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
        this.dbListOfAllItems.add(dbItem)       // Update model list
        num++
        val itemUI = ItemComponent(dbItem, this)  //Convert to UI Component
        this.uiListOfAllItems.add(itemUI)       //Update UI List
    }

    private fun TodoListConverter(dbList: TodoList): ObservableList<Node> {
        val uiList = FXCollections.observableArrayList<Node>()
        for (dbItem in dbList.list) {
            var uiItem = ItemComponent(dbItem, this)
            uiList.add(uiItem)
        }
        return uiList
    }
}
