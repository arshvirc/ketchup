package ketchup.app.components.graphic

import Model
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class DragComponent: Button  {
    var model: Model
    var imageView = ImageView()
    var image = Image("images/drag_icon.png")
    constructor(m : Model) {
        this.alignment = Pos.CENTER
        this.maxHeight = 25.0
        this.maxHeight = 25.0
        this.minHeight = 25.0
        this.minWidth = 25.0
        this.isMnemonicParsing = false
        this.prefHeight = 25.0
        this.prefWidth = 25.0
        this.model = m

        imageView.fitHeight = 15.0
        imageView.fitWidth = 40.0
        imageView.isPickOnBounds = true
        imageView.isPreserveRatio = true
        imageView.image = image
        this.graphic = imageView
        this.setOnAction {
            println("setOnAction")
        }

        this.setOnMousePressed {
            //isMouseTransparent = true
            model.draggedItemId = this.parent.parent.parent.id
        }
        this.setOnMouseReleased {
            //isMouseTransparent = false
        }
        this.setOnMouseDragReleased {
            println("setOnMouseDragReleased1")
            model.moveItems(model.draggedItemId,this.parent.parent.parent.id)
        }
        this.setOnDragDetected {
            startFullDrag()
            println("setOnDragDetected")
        }
    }

    private fun manualSortList(model:Model) {
        var draggedItem = model.draggedItemId
        var nearItem = this.parent.parent.parent.id
        var listIndexDragged = 0
        var foundIndexDragged = 0;
        for ((counter, item) in model.uiListOfAllItems.withIndex()) {
            if (item.id == draggedItem) {
                listIndexDragged = counter;
            }
            if (item.id == nearItem) {
                foundIndexDragged = counter;
            }
        }
        var before : List<Node>
        var movingItem : List<Node>
        var space  : List<Node>
        var after  : List<Node>
        if ( listIndexDragged < foundIndexDragged) {
            before = model.uiListOfAllItems.slice(0 until listIndexDragged)
            movingItem = model.uiListOfAllItems.take(listIndexDragged) // this is going to be moving
            if (model.dragTop) {
                space = model.uiListOfAllItems.slice(listIndexDragged + 1 until foundIndexDragged)
                after = model.uiListOfAllItems.slice(foundIndexDragged..model.uiListOfAllItems.lastIndex)
            } else {
                space = model.uiListOfAllItems.slice(listIndexDragged + 1..foundIndexDragged)
                after = model.uiListOfAllItems.slice(foundIndexDragged + 1..model.uiListOfAllItems.lastIndex)
            }
        } else {
            before = model.uiListOfAllItems.slice(0 until listIndexDragged)
            movingItem = model.uiListOfAllItems.take(listIndexDragged) // this is going to be moving
            if (model.dragTop) {
                space = model.uiListOfAllItems.slice(listIndexDragged + 1 until foundIndexDragged)
                after = model.uiListOfAllItems.slice(foundIndexDragged..model.uiListOfAllItems.lastIndex)
            } else {
                space = model.uiListOfAllItems.slice(listIndexDragged + 1..foundIndexDragged)
                after = model.uiListOfAllItems.slice(foundIndexDragged + 1..model.uiListOfAllItems.lastIndex)
            }
        }
        model.uiListOfAllItems.removeAll(before)
        model.uiListOfAllItems.removeAll(movingItem)
        model.uiListOfAllItems.removeAll(space)
        model.uiListOfAllItems.removeAll(after)

        model.uiListOfAllItems.addAll(before)
        model.uiListOfAllItems.addAll(space)
        model.uiListOfAllItems.addAll(movingItem)
        model.uiListOfAllItems.addAll(after)
    }

}