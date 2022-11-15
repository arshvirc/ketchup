import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.PrintWriter
import java.sql.DriverManager
import ketchup.service.Program
import ketchup.console.TodoItem
import ketchup.service.controllers.AbstractTest

class ProgramTest : AbstractTest() {
    // Creates a new empty file

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
        prog.close()
        deleteFile("./setupDBTest.db")
    }
}