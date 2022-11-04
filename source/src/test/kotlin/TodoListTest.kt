import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

internal class TodoListTest {
    private var list = mutableListOf<TodoItem>()
    lateinit var item: TodoItem

    fun display(item: TodoItem) {
        val id = item.id
        val title = item.title
        val description = item.description
        val timestamp = item.timestamp
        val deadline = item.deadline
        val priority = item.priority
        println("ID: $id")
        println("Title: $title")
        println("Description: $description")
        println("Timestamp: $timestamp")
        println("Deadline: $deadline")
        println("Priority: $priority")
        print("Tags: ")
    }


    @Test
    fun checkAdd() {
        // arrange
        var testList = TodoList()
        var expectedItem = TodoItem(
            id = 0, title = "Test", description = "Test description",
                deadline = Date(2001,12,24), priority = 1)


        // act
        testList.add(expectedItem)

        // assert
        assertEquals(testList.displayList(), display(expectedItem))

    }

    @Test
    fun checkRemoveIf_true() {
        // arrange
        var testList = TodoList()
        var expectedList = TodoList()
        var item1 = TodoItem(
            id = 0, title = "Test1", description = "Test description 1",
                deadline = Date(2001,12,24), priority = 1)
        var item2 = TodoItem(
            id = 1, title = "Test2", description = "Test description 2",
                deadline = Date(2002,2,12), priority = 3)
        testList.add(item1)
        testList.add(item2)
        expectedList.add(item2)

        // act
        testList.removeIf { it.id == 0 }

        // assert
        assertEquals(testList.displayList(), display(item2))
    }

    @Test
    fun checkRemoveIf_false() {
        // arrange
        var testList = TodoList()
        var expectedList = TodoList()
        var item1 = TodoItem(
            id = 0, title = "Test1", description = "Test description 1",
                deadline = Date(2001,12,24), priority = 1)
        testList.add(item1)
        expectedList.add(item1)

        // act
        testList.removeIf { it.id == 1 }

        // assert
        assertEquals(testList.displayList(), display(item1))
    }

    @Test
    fun checkEdit() {
        // arrange
        var testList = TodoList()
        var expectedList = TodoList()
        var item1 = TodoItem(
            id = 0, title = "Test1", description = "Test description 1",
                deadline = Date(2001,12,24), priority = 1)
        var item2 = TodoItem(
            id = 0, title = "Test2", description = "Test description 2",
                deadline = Date(2002,2,12), priority = 3)
        testList.add(item1)
        expectedList.add(item2)
        println(testList.displayList())

        // act
        testList.edit(item1, item2)

        // assert
        assertEquals(testList.displayList(), display(item2))
    }

    @Test
    fun checkGetById_true() {
        // arrange
        var testList = TodoList()
        var item1 = TodoItem(
            id = 0, title = "Test1", description = "Test description 1",
                deadline = Date(2001,12,24), priority = 1)
        var item2 = TodoItem(
            id = 1, title = "Test2", description = "Test description 2",
                deadline = Date(2002,2,12), priority = 3)
        testList.add(item1)
        testList.add(item2)

        // act
        var item3 = testList.getById(0)

        // assert
        if (item3 != null) {
            assertEquals(item3.printItem(), item1.printItem())
        }
    }

    @Test
    fun checkGetById_false() {
        // arrange
        var testList = TodoList()
        var item1 = TodoItem(
            id = 0, title = "Test1", description = "Test description 1",
                deadline = Date(2001,12,24), priority = 1)
        var item2 = TodoItem(
            id = 1, title = "Test2", description = "Test description 2",
                deadline = Date(2002,2,12), priority = 3)
        testList.add(item1)
        testList.add(item2)

        // act
        var item3 = testList.getById(3)

        // assert
        assertEquals(null, item3)
    }

    @Test
    fun checkDisplayList() {
        // arrange
        var testList = TodoList()
        var item1 = TodoItem(
            id = 0, title = "Test1", description = "Test description 1",
                deadline = Date(2001,12,24), priority = 1)
        testList.add(item1)

        // act and assert
        assertEquals(testList.displayList(), display(item1))
    }
}