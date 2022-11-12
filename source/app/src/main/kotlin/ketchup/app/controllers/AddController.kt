import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.stage.Stage

class AddController {
    private lateinit var model: Model

    @FXML
    private lateinit var inputTitle: TextArea

    @FXML
    private lateinit var inputDetail: TextArea

    @FXML
    private lateinit var inputTags: ComboBox<String>

    @FXML
    private lateinit var inputDeadline: DatePicker

    @FXML
    private lateinit var inputPriority: ComboBox<String>

    @FXML
    private lateinit var cancelButton: Button

    @FXML
    private lateinit var createButton: Button

    fun setModel(m: Model) {
        this.model = m
        inputTags.items.addAll(m.getListOfTags())
        inputPriority.items.addAll(m.getListOfPriorities())
    }

    @FXML
    private fun onButtonClicked(event: ActionEvent) {
        val source = event.source as Node
        val id = source.id
        if (id == "createButton") {
            if (inputTitle.text == null || inputTitle.text == "" || inputTitle.text == " " ) {
                println("You must have a title!")
                return
            }
            val item = GuiItem(
                inputTitle.text,
                inputDetail.text,
                inputTags.value ?: "",
                inputDeadline.value ,
                inputPriority.value ?: "0"
            )
            model.addItemToList(item)
        }
        var stage = createButton.scene.window as Stage
        stage.close()
    }
}