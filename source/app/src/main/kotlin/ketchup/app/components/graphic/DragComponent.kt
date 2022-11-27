package ketchup.app.components.graphic

import javafx.geometry.Bounds
import ketchup.app.Model
import javafx.geometry.Pos
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

        this.setOnMouseDragReleased {
            val gestureSource = it.gestureSource as DragComponent
            val sourceId = gestureSource.parent.parent.parent.id
            var boundsInScene: Bounds = this.localToScene(this.boundsInLocal)
            var halfPoint = boundsInScene.maxY - 0.5 * boundsInScene.height
            var releasedAbove = false
            if (it.sceneY < halfPoint) {
                releasedAbove = true
            }
            model.moveItem(sourceId,this.parent.parent.parent.id, releasedAbove)
        }
        this.setOnDragDetected {
            startFullDrag()
        }
    }
}