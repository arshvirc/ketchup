import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class Model {
    private var toDoList : ObservableList<Node>
    private var listOfTags : MutableList<String>
    private val listOfPriorities = mutableListOf<String>("0", "1", "2", "3")

    constructor(list :ObservableList<Node>) {
        this.toDoList = list
        this.listOfTags = mutableListOf("Academic", "Family", "Extra")

    }
    fun getToDoList(): ObservableList<Node> {
        return this.toDoList
    }

    fun getListOfTags(): MutableList<String> {
        return this.listOfTags
    }

    fun getListOfPriorities(): MutableList<String> {
        return this.listOfPriorities
    }

    fun addItemToList(item: GuiItem) {
        val fxmlLoader1 = FXMLLoader(App::class.java.getResource("fxml/itemTitleBar.fxml"))
        var top = fxmlLoader1.load<HBox>()
        val fxmlLoader2 = FXMLLoader(App::class.java.getResource("fxml/itemComponent.fxml"))
        var bottom = fxmlLoader2.load<VBox>()
        var itemUI = TitledPane()
        itemUI.graphic = top
        itemUI.content = updatedVBox(item)
        this.toDoList.add(itemUI)

    }

    private fun updatedVBox(item: GuiItem) : VBox {
        var vbox = VBox()
        vbox.prefHeight = 200.0
        vbox.prefWidth = 100.0

        var details = TextArea()
        details.prefHeight = 20.0
        details.prefWidth = 331.0
        if (item.detail == "") {
            details.promptText = "Enter Details"
        } else {
            details.text = item.detail
        }
        vbox.children.add(details)

        // tagsBox start
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
        tagOptions.items.addAll(listOfTags)
        tagOptions.value = item.tags
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
        deadlineOption.value = item.deadline
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
        priorityOptions.items.addAll(listOfPriorities)
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

        return vbox
    }

}