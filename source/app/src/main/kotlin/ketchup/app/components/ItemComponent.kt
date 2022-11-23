package ketchup.app.components

import Model
import javafx.geometry.Bounds
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.TitledPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import ketchup.app.components.content.*
import ketchup.app.components.graphic.CompleteComponent
import ketchup.app.components.graphic.DragComponent
import ketchup.app.components.graphic.TitleComponent
import ketchup.console.TodoItem


class ItemComponent: TitledPane {
    var model :Model
    constructor(dbItem: TodoItem, model: Model) {
        id = dbItem.id.toString()
        this.model = model
        graphic = GraphicComponent(dbItem, model)
        content = ContentComponent(dbItem, model)

        this.setOnMouseEntered {
            border = (Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii(10.0), null)))
        }
        this.setOnMouseDragEntered {
            border = (Border(BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii(10.0), null)))
        }
        this.setOnMouseDragExited {
            border = (Border(BorderStroke(null, null, null, null)))
            padding = Insets(0.0)
        }

        this.setOnMouseExited {
            border = (Border(BorderStroke(null, null, null, null)))
            padding = Insets(0.0)
        }
        this.setOnMouseDragReleased {
            println("setOnMouseDragReleased2")
            model.dragInitiated = false
            model.moveItems(model.draggedItemId,this.id)
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
                    padding = Insets(10.0)
                    model.dragTop = false;
                    model.dragBottom = true;
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
                    model.dragTop = true;
                    model.dragBottom = false;
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
        children.addAll(dragButton, title, complete)
    }
}

class ContentComponent: VBox {
    constructor(dbItem: TodoItem, m: Model) {
        id = dbItem.id.toString()
        prefHeight = 200.0
        prefWidth = 100.0
        this.setSpacing(10.0)
        var description = DescriptionComponent(dbItem, m)
        var tags = TagsComponent(dbItem, m)
        var deadline = DeadlineComponent(dbItem, m)
        var priority = PriorityComponent(dbItem, m)
        var trash = TrashComponent(dbItem, m)
        children.addAll(description, tags, deadline, priority, trash)
    }
}