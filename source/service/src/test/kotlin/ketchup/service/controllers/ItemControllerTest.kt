package ketchup.service.controllers

import ketchup.console.TodoItem
import ketchup.service.Program
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.io.PrintWriter
import java.sql.DriverManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

internal class ItemControllerTest {
    private fun createBlankFile(f : File) {
        f.createNewFile()
        // clear file contents
        val writer = PrintWriter(f)
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
    fun addControllerTestOneItemNoTags() {
        val dbURL = "jdbc:sqlite:./AddControllerTest.db"
        val prog = Program(dbURL)
        val dbFile = File("./AddControllerTest.db")
        createBlankFile(dbFile)

        val conn = prog.run()

        val controller = conn?.let{ ItemController(it)}

        if(controller != null) {
            val title = "my first task"
            val desc = "a description"
            val deadline = Date(1672019404)
            val priority = 2
            val item = TodoItem(title, desc, deadline, priority)
            val id = controller.addItem(item, 0)

            val testQueryString = "SELECT * FROM TodoItems WHERE item_id = ${id}"
            val query = conn!!.createStatement()
            val result = query.executeQuery(testQueryString)
            assertTrue(result.next())
            assertTrue(result.getString("title") == title)
            assertTrue(result.getString("description") == desc)
            assertTrue(result.getString("deadline") == deadline.toString())
            assertTrue(result.getInt("priority") == priority)
            assertFalse(result.next()) // should be one row
        }

        deleteFile("./AddControllerTest.db")
        prog.close()
    }

    @Test
    fun addControllerTestOneItemWithTags() {
        val dbURL = "jdbc:sqlite:./AddControllerTest.db"
        val prog = Program(dbURL)
        val dbFile = File("./AddControllerTest.db")
        createBlankFile(dbFile)

        val conn = prog.run()

        val controller = conn?.let{ ItemController(it) }

        if (controller != null) {
            val itemTitle = "my first task"
            val item = TodoItem(title = itemTitle)

            val tags = listOf("cs 346", "blue", "cs 350")
            for (tag in tags) {
                item.addTag(tag)
            }

            val id = controller.addItem(item, 0)

            val query = conn!!.createStatement()
            val itemFinderQueryString = "SELECT * FROM TodoItems WHERE title='my first task'"
            var result = query.executeQuery(itemFinderQueryString)
            assertTrue(result.next())
            // get the item id from the result
            assertTrue(id == result.getInt("item_id"))
            assertFalse(result.next()) // should be one item
            // use the id to look up tags in the tags table
            val pairFinderQueryString = "SELECT tag FROM ItemTags WHERE item_id='${id}'"
            result = query.executeQuery(pairFinderQueryString)
            var i = 0
            while (result.next()) {
                assertTrue(result.getString("tag") == tags[i])
                ++i
            }
            assertTrue(i == tags.size)
        }

        deleteFile("./AddControllerTest.db")
        prog.close()
    }

    @Test
    fun editControllerTestOneItemNoTags() {
        val dbURL = "jdbc:sqlite:./EditControllerTest.db"
        val prog = Program(dbURL)
        val dbFile = File("./EditControllerTest.db")
        createBlankFile(dbFile)

        val conn = prog.run()
        val controller = conn?.let{ ItemController(it) }

        if (controller != null) {
            val query = conn!!.createStatement()

            // add item
            val itemTitle = "my first task"
            val itemDescription = "a cool task that's not annoying at all"
            val itemDeadline = Date( 	1672019403)
            val itemPriority = 3
            val item = TodoItem(itemTitle, itemDescription, itemDeadline, itemPriority)
            val id = controller.addItem(item, 0)

            // second item to replace
            val newTitle = "NEW TITLE"
            val newDesc = "NEW DESCRIPTION"
            val newDeadline = Date(1672019404)
            val newPriority = 2
            val newItem = TodoItem(newTitle, newDesc, newDeadline, newPriority, id = id)

            controller.editItem(newItem)
            // find new item
            val findItemQueryString = "SELECT * FROM TodoItems WHERE item_id = ${id}"
            val result = query.executeQuery(findItemQueryString)

            result.next()
            assertTrue(result.getString("title") == newTitle)
            assertTrue(result.getString("description") == newDesc)
            assertTrue(result.getString("deadline") == newDeadline.toString())
            assertTrue(result.getInt("priority") == newPriority)
        }

        deleteFile("./EditControllerTest.db")
        prog.close()
    }

    @Test
    fun editControllerTestOneItemRemoveTags() {
        val dbURL = "jdbc:sqlite:./EditControllerTest.db"
        val prog = Program(dbURL)
        val dbFile = File("./EditControllerTest.db")
        createBlankFile(dbFile)

        val conn = prog.run()
        val controller = conn?.let{ ItemController(it) }

        if (controller != null) {
            val query = conn!!.createStatement()

            // add item
            val itemTitle = "my first task"
            val itemDescription = "a cool task that's not annoying at all"
            val itemDeadline = Date( 	1672019403)
            val itemPriority = 3
            val item = TodoItem(itemTitle, itemDescription, itemDeadline, itemPriority)

            val tags = listOf("cs 346", "blue", "cs 350")
            for (tag in tags) {
                item.addTag(tag)
            }
            val id = controller.addItem(item, 0)

            // second item to replace
            val newTitle = "NEW TITLE"
            val newDesc = "NEW DESCRIPTION"
            val newDeadline = Date(1672019404)
            val newPriority = 2
            val newItem = TodoItem(newTitle, newDesc, newDeadline, newPriority, id = id)

            controller.editItem(newItem)
            val pairFinderQueryString = "SELECT tag FROM ItemTags WHERE item_id='${id}'"
            val result = query.executeQuery(pairFinderQueryString)

            assertFalse(result.next()) // should be no more tags
        }

        deleteFile("./EditControllerTest.db")
        prog.close()
    }

    @Test
    fun editControllerTestOneItemAddTags() {
        val dbURL = "jdbc:sqlite:./EditControllerTest.db"
        val prog = Program(dbURL)
        val dbFile = File("./EditControllerTest.db")
        createBlankFile(dbFile)

        val conn = prog.run()
        val controller = conn?.let{ ItemController(it) }

        if (controller != null) {
            val query = conn!!.createStatement()

            // add item
            val itemTitle = "my first task"
            val itemDescription = "a cool task that's not annoying at all"
            val itemDeadline = Date( 	1672019403)
            val itemPriority = 3
            val item = TodoItem(itemTitle, itemDescription, itemDeadline, itemPriority)

            val tags = listOf("cs 346", "blue", "cs 350")
            for (tag in tags) {
                item.addTag(tag)
            }
            val id = controller.addItem(item, 0)

            // second item to replace
            val newTitle = "NEW TITLE"
            val newDesc = "NEW DESCRIPTION"
            val newDeadline = Date(1672019404)
            val newPriority = 2
            val newItem = TodoItem(newTitle, newDesc, newDeadline, newPriority, id = id)

            val newTags = listOf("cs 346", "blue", "cs 350", "intellij", "kotlin")
            for (tag in newTags) {
                newItem.addTag(tag)
            }

            controller.editItem(newItem)
            val pairFinderQueryString = "SELECT tag FROM ItemTags WHERE item_id='${id}'"
            val result = query.executeQuery(pairFinderQueryString)
            var i = 0
            while (result.next()) {
                assertTrue(result.getString("tag") == newTags[i])
                ++i
            }
            assertTrue(i == newTags.size)
        }

        deleteFile("./EditControllerTest.db")
        prog.close()
    }

    @Test
    fun deleteItemTest() {
        val dbUrl = "jdbc:sqlite:./deleteItem.db"
        val prog = Program(dbUrl)
        val dbFile = File("./deleteItem.db")
        createBlankFile(dbFile)

        val conn = prog.run()

        val controller = conn?.let { ItemController(it) }

        if(controller != null) {
            val itemTitle = "my first task"
            val item = TodoItem(title = itemTitle)


            val tags = listOf("cs 346", "blue", "cs 350")
            for (tag in tags) {
                item.addTag(tag)
            }

            val item2 = TodoItem(title="my second title")


            controller.addItem(item, 0)
            // itemId == 1
            controller.addItem(item2, 0)
            // itemId == 2

            controller.deleteItem(1)

            val listController = conn?.let { ListController(it) }

            if(listController != null) {
                val list = listController.getAllLists()
                assertEquals(list.size, 1)
                assertEquals(list[0].list[0].id, 2)
            }

            controller.deleteItem(2)

            if(listController != null) {
                val list = listController.getAllLists()
                assertEquals(list.size, 0)
            }
        }

        deleteFile("./deleteItem.db")
        prog.close()
    }
}