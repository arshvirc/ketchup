package ketchup.app.ktorclient

import ketchup.console.TodoItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*


internal class ClientTest {
    private val url = "http://127.0.0.1:4040"
    private val api: Client = Client(url)

    @Test
    fun addAndGetItemTest() = runBlocking {
        val item = TodoItem(title="testItem", description="awesomeDescription")
        val itemId = api.createTodoItem(0, item)

        val receivedItem = api.getTodoItem(itemId)
        println(receivedItem)
        if(receivedItem != null) {
            val item = receivedItem.item
            assertEquals(item.title, "testItem")
            assertEquals(item.description, "awesomeDescription")
        }
    }

    @Test
    fun createListTest() = runBlocking {
        val newListName = "new list"
        val success = api.createList(newListName)
        assert(success != -1)

        val anotherOne = api.createList("new list two")
        assert(anotherOne != -1)
    }

    @Test
    fun getAllListsTest() = runBlocking {
        val listTwo = "listTwo"
        val listTwoId = api.createList(listTwo)

        val itemOne = TodoItem("hello", "hi", priority = 3)
        val itemTwo = TodoItem("helloTwo", "hi", priority = 2)

        api.createTodoItem(0, itemOne)
        api.createTodoItem(listTwoId, itemTwo)

        val allLists = api.getAllLists()

        if (allLists != null) {
            assert(allLists.lists.size == 2)
        }
    }

    @Test
    fun deleteTodoItemTest() = runBlocking {
        val itemOne = TodoItem("test Item", "test desc")
        val itemOneId = api.createTodoItem(0, itemOne)

        assert(itemOneId != -1)

        val deleteSuccess = api.deleteTodoItem(itemOneId)
        assert(deleteSuccess)
    }

    @Test
    fun editTodoItemTest() = runBlocking {
        val itemOne = TodoItem("test edit item", "test description lols",
            deadline = Date(1668718311738))

        val itemOneId = api.createTodoItem(0, itemOne)


        val newItem = TodoItem("test edited item", "test edited description",
            deadline = Date(1668718311738))

        val editSuccess = api.editTodoItem(itemOneId, newItem)

        val editedItem = api.getTodoItem(itemOneId)

        if (editedItem != null) {
            assertEquals(editedItem.item.title, newItem.title)
            assertEquals(editedItem.item.description, newItem.description)
        } else {
            println("item not edited?")
        }
    }

    @Test
    fun getListByIdTest() = runBlocking {
        val newListName = "Hello New List"
        val newListId = api.createList(newListName)

        val listResponse = api.getListById(newListId)

        if (listResponse != null) {
            assertEquals(newListName, listResponse.name)
            assertEquals(listResponse.size, 0)
        } else {
            println("list not found. check ur code")
        }
    }

    @Test
    fun deleteListById() = runBlocking {
        val newListName = "Hello New List"
        val newListId = api.createList(newListName)

        assert(newListId != -1)

        val deleteSuccess = api.deleteListById(newListId)
        assert(deleteSuccess)
    }
}