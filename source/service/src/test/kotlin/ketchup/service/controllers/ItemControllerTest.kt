package ketchup.service.controllers

import ketchup.console.TodoItem
import ketchup.service.Program
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.io.PrintWriter
import java.sql.DriverManager

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
            val itemTitle = "my first task"
            val item = TodoItem(title = itemTitle)
            controller.addItem(item)

            val testQueryString = "SELECT * FROM TodoItems WHERE title='my first task'"
            val query = conn!!.createStatement()
            val result = query.executeQuery(testQueryString)
            assertTrue(result.next())
            assertFalse(result.next()) // should be one row
        }

        deleteFile("./AddControllerTest.db")
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

            controller.addItem(item)

            val query = conn!!.createStatement()
            val itemFinderQueryString = "SELECT * FROM TodoItems WHERE title='my first task'"
            var result = query.executeQuery(itemFinderQueryString)
            assertTrue(result.next())
            // get the item id from the result
            val id = result.getInt("item_id")
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
    }
}