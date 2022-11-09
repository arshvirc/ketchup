package ketchup.service.controllers

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
}