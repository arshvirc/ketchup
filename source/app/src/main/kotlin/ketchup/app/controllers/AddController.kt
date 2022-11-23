import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.stage.Stage
import ketchup.console.TodoItem
import java.time.Instant
import java.time.ZoneId
import java.util.*

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
        inputTags.items.addAll(m.listOfTags)
        inputPriority.items.addAll(m.listOfPriorities)
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

            val local = inputDeadline.value
            val instant = Instant.from(local.atStartOfDay(ZoneId.systemDefault()))
            val date = Date.from(instant)

            if (inputDetail.text == null || inputDetail.text == "") inputDetail.text = " ";
            if (inputPriority.value == null) { inputPriority.value = "0"}

            // TODO: Convert deadline from LocalDate to Date

            val item = TodoItem(
                title = inputTitle.text,
                description = inputDetail.text,
                priority = inputPriority.value.toInt(),
                completion = false,
                timestamp = Date(System.currentTimeMillis()),
                deadline = date
            )

            model.addItemToList(item)
        }
        var stage = createButton.scene.window as Stage
        stage.close()
    }
}