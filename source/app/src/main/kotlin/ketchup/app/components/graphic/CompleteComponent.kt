package ketchup.app.components.graphic

import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import kotlinx.coroutines.runBlocking

class CompleteComponent: ButtonBar {
    var toDoItemId : String
    var completeButton: Button
    var model: Model
    val api: Client

    constructor(item: TodoItem, m: Model) {
        this.toDoItemId = item.id.toString()
        this.model = m
        this.api = m.api


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
        var completeImage : Image
        if(!item.completion) {
            completeImage = Image("images/progress.png")
        } else {
            completeImage = Image("images/completed.png")
        }
        completeImageView.image = completeImage
        completeButton.graphic = completeImageView
        completeButton.graphic.id = "0"

        completeButton.setOnAction { obs ->
            run {
                val source = obs.source as Button
                if (source.graphic.id.toInt() == 1) {
                    // Setting completion to "false"
                    println("Moving the item ${this.toDoItemId} to 'In Progress'.")
                    var completedImage = Image("images/progress.png")
                    completeImageView.image = completedImage
                    completeButton.graphic = completeImageView
                    completeButton.graphic.id = "0"

                    item.completion = false
                    item.printItem()
                    val editSuccess = runBlocking { api.editTodoItem(item.id, item) }
                    if(!editSuccess) {
                        println("Uncompleting item with ID ${item.id} failed")
                    }

                    this.moveToBottom(toDoItemId)
                } else {
                    // Setting completion to "true"

                    println("Moving the item ${this.toDoItemId} to 'Completed'.")
                    println("COMPLETED TASK")

                    item.completeTask()
                    item.printItem()
                    val editSuccess = runBlocking { api.editTodoItem(item.id, item) }
                    if(!editSuccess) {
                        println("Uncompleting item with ID ${item.id} failed")
                    }

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
//        print(model.uiListOfAllItems.lastIndex)
    }
}