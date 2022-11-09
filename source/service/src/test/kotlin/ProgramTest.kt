import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.PrintWriter
import java.sql.DriverManager
import ketchup.service.Program
import ketchup.console.TodoItem

class ProgramTest {
    // Creates a new empty file
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
    // Testing setting up an empty DB
    fun setupDBTest() {
        val dbURL = "jdbc:sqlite:./setupDBTest.db"
        val prog = Program(dbURL)
        val dbFile = File("./setupDBTest.db")
        createBlankFile(dbFile)
        prog.setupDB()
        // Should be 5 tables

        val conn = DriverManager.getConnection(dbURL)
        val query = conn!!.createStatement()

        val queryStrings = listOf("SELECT * FROM sqlite_schema WHERE type='table' AND name='TodoLists';",
                            "SELECT * FROM sqlite_schema WHERE type='table' AND name='Program';",
                            "SELECT * FROM sqlite_schema WHERE type='table' AND name='TodoItems';",
                            "SELECT * FROM sqlite_schema WHERE type='table' AND name='Tags';",
                            "SELECT * FROM sqlite_schema WHERE type='table' AND name='ItemTags';")
        for (q in queryStrings) {
            val result = query.executeQuery(q)
            assertTrue(result.next())
        }
        deleteFile("./setupDBTest.db")
    }

    @Test
    fun addControllerTestOneItemNoTags() {
        val dbURL = "jdbc:sqlite:./AddControllerTest.db"
        val prog = Program(dbURL)
        val dbFile = File("./AddControllerTest.db")
        createBlankFile(dbFile)
        prog.setupDB()
        val itemTitle = "my first task"
        val item = TodoItem(title = itemTitle)
        prog.AddItemController(item)
        val conn = DriverManager.getConnection(dbURL)
        val testQueryString = "SELECT * FROM TodoItems WHERE title='my first task'"
        val query = conn!!.createStatement()
        val result = query.executeQuery(testQueryString)
        assertTrue(result.next())
        assertFalse(result.next()) // should be one row
        deleteFile("./AddControllerTest.db")
    }

    @Test
    fun addControllerTestOneItemWithTags() {
        val dbURL = "jdbc:sqlite:./AddControllerTest.db"
        val prog = Program(dbURL)
        val dbFile = File("./AddControllerTest.db")
        createBlankFile(dbFile)
        prog.setupDB()

        val itemTitle = "my first task"
        val item = TodoItem(title = itemTitle)

        val tags = listOf("cs 346", "blue", "cs 350")
        for (tag in tags) {
            item.addTag(tag)
        }

        prog.AddItemController(item)
        val conn = DriverManager.getConnection(dbURL)
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

        deleteFile("./AddControllerTest.db")
    }
}