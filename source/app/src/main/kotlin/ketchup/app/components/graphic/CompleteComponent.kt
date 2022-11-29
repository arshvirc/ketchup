package ketchup.app.components.graphic

import javafx.collections.FXCollections
import javafx.geometry.Bounds
import ketchup.app.Model
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ketchup.console.TodoItem
import kotlinx.coroutines.runBlocking

class CompleteComponent: Button  {
    var model: Model
    var imageView = ImageView()
    var image: Image
    constructor(item: TodoItem, m : Model) {
        this.alignment = Pos.CENTER
        this.maxHeight = 25.0
        this.maxHeight = 25.0
        this.minHeight = 25.0
        this.minWidth = 25.0
        this.isMnemonicParsing = false
        this.prefHeight = 25.0
        this.prefWidth = 25.0
        this.model = m

        image = if(!item.completion) {
            Image("images/progress.png")
        } else {
            Image("images/completed.png")
        }

        imageView.fitHeight = 15.0
        imageView.fitWidth = 40.0
        imageView.isPickOnBounds = true
        imageView.isPreserveRatio = true
        imageView.image = image
        this.graphic = imageView

        this.setOnAction { obs ->
            run {
                val source = obs.source as Button
                if (item.completion) {
                    // Setting completion to "false"
                    println("Moving the item ${item.id} to 'In Progress'.")
                    image = Image("images/progress.png")
                    imageView.image = image
                    graphic = imageView
                    graphic.id = "0"

                    item.completion = false
                    item.printItem()
                    val editSuccess = runBlocking { m.api.editTodoItem(item.id, item) }
                    if(!editSuccess) {
                        println("Uncompleting item with ID ${item.id} failed")
                    }

                    // TODO: remember to change this to this.moveToBottomUnCompleted once the code has been refactored
                    
                    this.moveToBottom(item.id.toString())
                } else {
                    // Setting completion to "true"

                    println("Moving the item ${item.id} to 'Completed'.")
                    println("COMPLETED TASK")

                    item.completeTask()
                    item.printItem()
                    val editSuccess = runBlocking { m.api.editTodoItem(item.id, item) }
                    if(!editSuccess) {
                        println("Uncompleting item with ID ${item.id} failed")
                    }

                    image = Image("images/completed.png")
                    imageView.image = image
                    graphic = imageView
                    graphic.id = "1"
                    println("Parent Id:  + ${this.parent.parent.parent.id}")
                    val itemId = this.parent.parent.parent.id
                    this.moveToBottom(itemId.toString())
                }
            }
        }
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

    /* TODO: this is the code for repositioning the item upon "uncompleting" it

    private fun moveToBottomUnCompleted( completedID: String) {
        val completed = FXCollections.observableArrayList<Node>()
        for (item in model.uiListOfAllItems) {
            if ( (item as ItemComponent).item.completion && item.id != completedID) {
                completed.add(item)
            }
        }
        model.uiListOfAllItems.removeAll(completed)
        model.uiListOfAllItems.addAll(completed)


     */
}