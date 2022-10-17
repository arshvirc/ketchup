class TodoList(name: String, description: String) {
    private val list = mutableListOf<ToDoItem>()

    fun add(item: ToDoItem) {
        list.add(item)
    }

    fun remove(item: ToDoItem) {
        list.remove(item)
    }

    fun complete(item: ToDoItem) {
         list[list.indexOf(item)].completeTask()
    }

    fun edit(oldItem: ToDoItem, newItem: ToDoItem) {
         list[list.indexOf(oldItem)] = newItem
    }

    fun displayList() {
        for(i in list) {
            i.printItem()
            println('\n')
        }
    }

    // fun sort()
    // fun filter()
}