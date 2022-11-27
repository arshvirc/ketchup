package ketchup.app

import ketchup.console.TodoItem

enum class Action {
    ADD, EDIT, DELETE, COMPLETE
}
class State (_action : Action, _item : TodoItem) {
    val action = _action
    val item = _item
}