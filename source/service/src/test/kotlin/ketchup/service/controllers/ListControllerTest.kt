package ketchup.service.controllers

import ketchup.console.TodoItem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.io.PrintWriter
import ketchup.service.Program


internal class ListControllerTest {
    private fun createFile(filename: String) {
        var file = File(filename)
        file.createNewFile()
        val writer = PrintWriter(file)
        writer.print("")
        writer.close()
    }
    private fun deleteFile(filename: String) {
        val file = File(filename);
        val result = file.delete()
        if(result) {
            println("$filename deleted!")
        } else {
            println("Error deleting $filename")
        }
    }

    @Test
    fun createNewListTest() {
        var dbUrl = "jdbc:sqlite:./listTest.db"
        val prog = Program(dbUrl)
        createFile("./listTest.db")
        val conn = prog.run()

        val controller = conn?.let { ListController(it) }

        if (controller != null) {
            controller.createList("new list")
            val testString = "SELECT * FROM TodoLists where title='new list'"
            val query = conn!!.createStatement()
            val result = query.executeQuery(testString)
            assertTrue(result.next())
            assertFalse(result.next())
        }

        deleteFile("./listTest.db")
    }

    @Test
    fun getAllListsTest() {
        var dbUrl = "jdbc:sqlite:./getList.db"
        var prog = Program(dbUrl)
        createFile("./getList.db")
        val conn = prog.run()

        val controller = conn?.let{ListController(it)}

        if(controller != null) {
            controller.createList("new list")
            controller.createList("another list")

            var itemController = conn?.let{ItemController(it)}

            var itemOne = TodoItem("hello", "hi", null, 3, 4)
            var itemTwo = TodoItem("hello", "bye", null, 3, 4)

            itemController.addItem(itemOne, 0)
            itemController.addItem(itemTwo, 1)

            val lists = controller.getAllLists()
            assertEquals(lists[0].id, 0)
            assertEquals(lists[1].id, 1)

            assertEquals(lists[0].list[0].title, "hello")
            assertEquals(lists[1].list[0].description, "bye")

        }
        deleteFile("./getList.db")
    }

    @Test
    fun deleteListTest() {
        var dbUrl = "jdbc:sqlite:./deleteList.db"
        var prog = Program(dbUrl)
        createFile("./deleteList.db")

        val conn = prog.run()

        val controller = conn?.let{ListController(it)}

        if(controller != null) {
           controller.createList("new list")

           // at this point, listId 0 == Main List
           // listId 1 == new list

            var itemController = conn?.let { ItemController(conn) }
            var itemOne = TodoItem("hello", "hi", null)
            var itemTwo = TodoItem("hello", "bye", null)

            itemController.addItem(itemOne, 0)
            itemController.addItem(itemTwo, 1)

            controller.deleteList(0)

            val lists = controller.getAllLists()

            assertEquals(lists.size, 1)
            assertEquals(lists[0].id, 1)

        }
        deleteFile("./deleteList.db")
    }
}