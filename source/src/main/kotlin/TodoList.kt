class TodoList(name: string, description: string) {
    val list = mutableListOf</*TodoItem*/>()

    fun setName(newName:string) {
        name = newName
    }

    fun setDescription(newDescription: string) {
        description = newDescription
    }

    fun add(item: /*TodoItem*/) {
        list.add(item)
    }

    fun remove(item: /*TodoItem*/) {
        list.remove(item)
    }

    fun complete(item: /*TodoItem*/) {
        // list[list.indexOf(item)].complete()
    }

    fun edit(oldItem: /*TodoItem*/, newItem: /*TodoItem*/) {
        // list[list.indexOf(oldItem)] = newItem
    }

    // fun sort()
    // fun filter()
}