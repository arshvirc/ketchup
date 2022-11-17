package ketchup.app.components.graphic

import Model
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.TitledPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import ketchup.app.components.content.*
import ketchup.console.TodoItem
import ketchup.console.TodoList

class ItemComponent: TitledPane {

    constructor(dbItem: TodoItem, model: Model) {
        id = dbItem.id.toString()
        graphic = GraphicComponent(dbItem, model)
        content = ContentComponent(dbItem, model)
    }
}

class GraphicComponent: HBox {
    constructor(dbItem: TodoItem, m: Model) {
        id = dbItem.id.toString()
        var dragButton = DragComponent(m)
        var title = TitleComponent(dbItem, m)
        var complete = CompleteComponent(dbItem, m)
        children.addAll(dragButton, title, complete)
    }
}

class ContentComponent: VBox {
    constructor(dbItem: TodoItem, m: Model) {
        id = dbItem.id.toString()
        prefHeight = 200.0
        prefWidth = 100.0
        var description = DescriptionComponent(dbItem, m)
        var tags = TagsComponent(dbItem, m)
        var deadline = DeadlineComponent(dbItem, m)
        var priority = PriorityComponent(dbItem, m)
        var trash = TrashComponent(dbItem, m)
        children.addAll(description, tags, deadline, priority, trash)
    }
}