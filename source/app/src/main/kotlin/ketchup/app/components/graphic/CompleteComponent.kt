package ketchup.app.components.graphic

import Model
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ketchup.console.TodoItem

class CompleteComponent: ButtonBar {
    var toDoItemId : String
    var completeButton: Button
    var model: Model

    constructor(item: TodoItem, m: Model) {
        this.toDoItemId = item.id.toString()
        this.model = m

        this.buttonMinWidth = 30.0
        this.maxHeight = Double.NEGATIVE_INFINITY
        this.maxWidth = Double.NEGATIVE_INFINITY
        this.minHeight = 25.0
        this.minWidth = Double.NEGATIVE_INFINITY
        this.prefHeight = 30.0
        this.prefWidth = 200.0


        completeButton = Button("Trash")
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
                val source = obs.source as Button
                if (source.graphic.id.toInt() == 1) {
                    println("Moving the item ${this.toDoItemId} to 'In Progress'.")
                    var completedImage = Image("images/progress.png")
                    completeImageView.image = completedImage
                    completeButton.graphic = completeImageView
                    completeButton.graphic.id = "0"
                    this.moveToBottom(toDoItemId)
                } else {
                    println("Moving the item ${this.toDoItemId} to 'Completed'.")
                    println("COMPLETED TASK")
                    var completedImage = Image("images/completed.png")
                    completeImageView.image = completedImage
                    completeButton.graphic = completeImageView
                    completeButton.graphic.id = "1"
                    println("Parent Id:  + ${completeButton.parent.parent.parent.id}")
                    val itemId = completeButton.parent.parent.parent.id
                    this.moveToBottom(toDoItemId)
                }
            }
        }
        this.buttons.add(completeButton)
    }

    private fun moveToBottom( completedID: String) {
        val tempList = FXCollections.observableArrayList<Node>()
        for (item in model.uiListOfAllItems) {
            if ( item.id == completedID ) {
                tempList.add(item)
            }
        }
        model.uiListOfAllItems.removeAll(tempList)
        model.uiListOfAllItems.addAll(tempList)
        print(model.uiListOfAllItems.lastIndex)
    }
}