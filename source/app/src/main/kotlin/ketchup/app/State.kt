package ketchup.app

import ketchup.console.TodoItem

enum class Action {
    ADD,
    TRASH,
    EDIT_COMPLETE,
    EDIT_TITLE,
    EDIT_DESC,
    EDIT_TAGS,
    EDIT_PRIORITY,
    EDIT_DEADLINE,
    PERMA_DELETE,
    UNARCHIVE
}
class State (_action : Action, _item : TodoItem) {
    val action = _action
    val item = _item
    fun copy() : State {
        return State(action, item.copy())
    }
}