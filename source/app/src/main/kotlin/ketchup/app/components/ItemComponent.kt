package ketchup.app.components

import ketchup.app.Model
import javafx.geometry.Bounds
import javafx.geometry.Insets
import javafx.scene.control.TitledPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import ketchup.app.components.content.*
import ketchup.app.components.graphic.CompleteComponent
import ketchup.app.components.graphic.DragComponent
import ketchup.app.components.graphic.TitleComponent
import ketchup.console.TodoItem


class ItemComponent: TitledPane {
    var model : Model
    var item : TodoItem

    constructor(dbItem: TodoItem, model: Model) {
        this.id = dbItem.id.toString()
        this.model = model
        this.item = dbItem
        this.graphic = GraphicComponent(dbItem, model)
        this.content = ContentComponent(dbItem, model)
        this.userData = dbItem
        this.isExpanded = false

        this.setOnMouseEntered {
            border = (Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii(10.0), null)))
        }

        this.setOnMouseDragExited {
            border = (Border(BorderStroke(null, null, null, null)))
            padding = Insets(0.0)
        }

        this.setOnMouseExited {
            border = (Border(BorderStroke(null, null, null, null)))
            padding = Insets(0.0)
        }

        this.setOnMousePressed{
            this.model.chooseSelectedItem(item.id)
        }


        this.setOnMouseDragReleased {
            val gestureSource = it.gestureSource as DragComponent
            val sourceId = gestureSource.parent.parent.parent.id
            var boundsInScene: Bounds = this.localToScene(this.boundsInLocal)
            var halfPoint = boundsInScene.maxY - 0.5 * boundsInScene.height
            var releasedAbove = false
            if (it.sceneY < halfPoint) {
                releasedAbove = true
            }
            model.moveItem(sourceId,this.id, releasedAbove)
        }
        this.setOnMouseDragOver {
            if (it.source != it.gestureSource ) {
                var boundsInScene: Bounds = this.localToScene(this.boundsInLocal)
                var halfPoint = boundsInScene.maxY - 0.5 * boundsInScene.height
                if (it.sceneY >= halfPoint) {
                    border = Border(
                        BorderStroke(
                            Color.RED,
                            Color.RED,
                            Color.BLACK,
                            Color.RED,
                            BorderStrokeStyle.NONE,
                            BorderStrokeStyle.NONE,
                            BorderStrokeStyle.SOLID,
                            BorderStrokeStyle.NONE,
                            CornerRadii.EMPTY,
                            BorderWidths(5.0),
                            Insets.EMPTY
                        )
                    )
                    padding = Insets(10.0, 0.0, 10.0, 0.0)
                } else {
                    border = Border(
                        BorderStroke(
                            Color.BLACK,
                            Color.RED,
                            Color.BLACK,
                            Color.RED,
                            BorderStrokeStyle.SOLID,
                            BorderStrokeStyle.NONE,
                            BorderStrokeStyle.NONE,
                            BorderStrokeStyle.NONE,
                            CornerRadii.EMPTY,
                            BorderWidths(5.0),
                            Insets.EMPTY
                        )
                    )
                }
            }
        }
    }
}

class GraphicComponent: HBox {
    constructor(dbItem: TodoItem, m: Model) {
        id = dbItem.id.toString()
        var dragButton = DragComponent(m)
        var title = TitleComponent(dbItem, m)
        title.minWidth = 400.0
        var complete = CompleteComponent(dbItem, m)
        children.addAll(dragButton, complete, title)
    }


}


class ContentComponent: VBox {
    constructor(dbItem: TodoItem, m: Model) {
        id = dbItem.id.toString()
        prefHeight = 200.0
        prefWidth = 100.0
        this.spacing = 10.0
        var description = DescriptionComponent(dbItem, m)
        var tags = TagsComponent(dbItem, m)
        var deadline = DeadlineComponent(dbItem, m)
        var priority = PriorityComponent(dbItem, m)
        var trash = TrashComponent(dbItem, m)
        children.addAll(description, tags, deadline, priority, trash)
    }
}