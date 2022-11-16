import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import ketchup.console.TodoItem
import ketchup.console.TodoList


class Model() {
    // We have the UI Components:
    lateinit var uiListOfAllItems: ObservableList<Node>                     // Contains the Merged List of All Items
    lateinit var uiListOfGroups: MutableList<ObservableList<Node>>          // Contains the UI Components

    // We have the Actual Information:
    lateinit var dbListOfAllItems: TodoList                                 // Contains all TodoItems
    lateinit var dbListOfGroups: MutableList<TodoList>                      // Contains the Grouped List
    lateinit var listOfTags: MutableList<String>                            // Contains the Tags
    lateinit var listOfGroups: MutableList<String>                          // Contains the Groups
    // Other Information
    val listOfPriorities: List<String> = listOf<String>("0", "1", "2", "3")
    var num = 0;

    constructor(list: ObservableList<Node>): this() {
        this.uiListOfAllItems = list
        this.dbListOfAllItems = TodoList()
        this.listOfTags = mutableListOf("Academic", "Family", "Extra")
        this.listOfGroups = mutableListOf("List 1", "List 2", "List 3")
        //this.callApiToUpdateModel()
    }

    fun callApiToUpdateModel() {
        dbListOfAllItems = TodoList(dbListOfAllItems)               // replace w API call
        // dbListOfGroups
        this.uiListOfAllItems = TodoListConverter(dbListOfAllItems) //
        // uiListOfGroups
        listOfTags = mutableListOf("Academic", "Family", "Extra")
        listOfGroups = mutableListOf("List 1", "List 2", "List 3")

    }


    fun addItemToList(dbItem: TodoItem) {
        // Add item to Api
        this.dbListOfAllItems.add(dbItem)       // Update model list
        num++
        val itemUI = TodoItemConverter(dbItem)  //Convert to UI Component
        this.uiListOfAllItems.add(itemUI)       //Update UI List
        println(uiListOfAllItems.lastIndex)
    }
}

private fun TodoListConverter(dbList: TodoList) : ObservableList<Node> {
    val uiList = FXCollections.observableArrayList<Node>()
    for ( dbItem in dbList.list ) {
        var uiItem = TodoItemConverter(dbItem)
        uiList.add(uiItem)
    }
    return uiList
}

private fun TodoItemConverter(dbItem: TodoItem) : TitledPane {
    val fxmlLoader = FXMLLoader(App::class.java.getResource("fxml/itemUI.fxml"))
    val uiItem = fxmlLoader.load<TitledPane>()
    uiItem.id = dbItem.id.toString()
    uiItem.graphic = createItemGraphicUIComponent(dbItem)
    uiItem.content = createItemContentUIComponent(dbItem)
    println(dbItem.title)
    return uiItem
}

private fun createItemGraphicUIComponent(item: TodoItem) : HBox {
    var uiComponent = HBox()
    var dragButton = Button()
    dragButton.alignment = Pos.CENTER
    dragButton.maxHeight = 25.0
    dragButton.maxHeight = 25.0
    dragButton.minHeight = 25.0
    dragButton.minWidth = 25.0
    dragButton.isMnemonicParsing = false
    dragButton.prefHeight = 25.0
    dragButton.prefWidth = 25.0
    var dragImageView = ImageView()
    dragImageView.fitHeight = 15.0
    dragImageView.fitWidth = 40.0
    dragImageView.isPickOnBounds = true
    dragImageView.isPreserveRatio = true
    var dragImage = Image("images/drag_icon.png")
    dragImageView.image = dragImage
    dragButton.graphic =dragImageView

    var title = TextField()
    title.maxHeight = 30.0
    title.maxWidth = 1.7976931348623157E308
    title.minHeight = 30.0
    title.minWidth = Double.NEGATIVE_INFINITY
    title.prefHeight = 30.0
    title.prefWidth = 102.0
    title.text = item.title
    title.textProperty().addListener { observable, oldValue, newValue ->
        run {
            println(observable.toString())
            println("Title changed from $oldValue to $newValue")
        }
    }

    var buttonBar = ButtonBar()
    buttonBar.buttonMinWidth = 30.0
    buttonBar.maxHeight = Double.NEGATIVE_INFINITY
    buttonBar.maxWidth = Double.NEGATIVE_INFINITY
    buttonBar.minHeight = 25.0
    buttonBar.minWidth = Double.NEGATIVE_INFINITY
    buttonBar.prefHeight = 30.0
    buttonBar.prefWidth = 200.0

    var completeButton = Button()
    completeButton.alignment = Pos.CENTER
    completeButton.maxHeight = 25.0
    completeButton.maxHeight = 25.0
    completeButton.minHeight = 25.0
    completeButton.minWidth = 25.0
    completeButton.isMnemonicParsing = false
    completeButton.prefHeight = 25.0
    completeButton.prefWidth = 25.0

    var completeImageView = ImageView()
    completeImageView.fitHeight = 15.0
    completeImageView.fitWidth = 40.0
    completeImageView.isPickOnBounds = true
    completeImageView.isPreserveRatio = true
    var completeImage = Image("images/progress.png")
    completeImageView.image = completeImage
    completeButton.graphic = completeImageView
    completeButton.graphic.id = "0"

    completeButton.setOnAction { obs ->
        run {
            println(obs.toString())
            val source = obs.source as Button
            if ( source.graphic.id.toInt() == 1) {
                var completedImage = Image("images/progress.png")
                completeImageView.image = completedImage
                completeButton.graphic = completeImageView
                completeButton.graphic.id = "0"
                // UPDATE MODEL AND UPDATE DATABASE
            } else {
                println("COMPLETED TASK")
                var completedImage = Image("images/completed.png")
                completeImageView.image = completedImage
                completeButton.graphic = completeImageView
                completeButton.graphic.id = "1"
                println("Parent Id:  + ${completeButton.parent.parent.parent.id}")
                // UPDATE MODEL AND UPDATE DATABASE
            }
        }
    }

    uiComponent.children.addAll(dragButton,title,completeButton)

    return uiComponent
}

private fun createItemContentUIComponent(item: TodoItem) : VBox {
    var vbox = VBox()
    vbox.prefHeight = 200.0
    vbox.prefWidth = 100.0

    var details = TextField()
    details.prefHeight = 20.0
    details.prefWidth = 331.0
    if (item.description == "") {
        details.promptText = "Enter Details"
    } else {
        details.text = item.description
    }
    details.textProperty().addListener { observable, oldValue, newValue ->
        run {
            println(observable.toString())
            println("textfield3 changed from $oldValue to $newValue")
        }
    }

    vbox.children.add(details)

    var tagsBox = HBox()
    tagsBox.prefHeight = 100.0
    tagsBox.prefWidth = 200.0

    var tagName = Label()
    tagName.prefHeight = 17.0
    tagName.prefWidth = 70.0
    tagName.text = "Tags: "
    tagsBox.children.add(tagName)

    var tagOptions = ComboBox<String>()
    tagOptions.prefHeight = 26.0
    tagOptions.prefWidth = 117.0
    tagOptions.value = "Not Implemented Yet"
    tagsBox.children.add(tagOptions)
    // tagsBox end

    // deadlineBox start
    var deadlineBox = HBox()
    deadlineBox.prefHeight = 100.0
    deadlineBox.prefWidth = 200.0

    var deadlineName = Label()
    deadlineName.prefHeight = 17.0
    deadlineName.prefWidth = 71.0
    deadlineName.text = "Deadline: "
    deadlineBox.children.add(deadlineName)

    var deadlineOption = DatePicker()
    deadlineOption.prefHeight = 26.0
    deadlineOption.prefWidth = 120.0
    // deadlineOption.value = item.deadline
    deadlineBox.children.add(deadlineOption)
    // deadlineBox end

    // priorityBox start
    var priorityBox = HBox()
    priorityBox.prefHeight = 100.0
    priorityBox.prefWidth = 200.0

    var priorityName = Label()
    priorityName.prefHeight = 17.0
    priorityName.prefWidth = 71.0
    priorityName.text = "Priority: "
    priorityBox.children.add(priorityName)

    var priorityOptions = ComboBox<String>()
    priorityOptions.prefHeight = 26.0
    priorityOptions.prefWidth = 120.0
    //priorityOptions.items.addAll(listOfPriorities)
    priorityOptions.value = item.priority.toString()
    priorityBox.children.add(priorityOptions)
    // deadlineBox end

    var buttonBar = ButtonBar()
    buttonBar.prefHeight = 40.0
    buttonBar.prefWidth = 200.0

    var button = Button()
    button.text = "Trash"
    buttonBar.buttons.add(button)
    vbox.children.add(tagsBox)
    vbox.children.add(deadlineBox)
    vbox.children.add(priorityBox)
    vbox.children.add(buttonBar)
    button.setOnAction { obs ->
        run {
            val id = button.parent.parent.parent.parent.parent.id
            println("$id ${button.parent.parent.parent.parent.parent}")
        }
    }

    return vbox
}
