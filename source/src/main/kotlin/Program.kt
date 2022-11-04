import javafx.application.Application
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.*
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Program(private var dbURL: String = "jdbc:sqlite:data.db", list: TodoList = TodoList()) {
    private var theList : TodoList = list
    private var conn: Connection? = null

    val format = Json { encodeDefaults = true }

    private fun connect() {
        try {
            conn = DriverManager.getConnection(dbURL)
            println("Connection to SQLite has been established.")
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    // run a query to determine if the DB is empty
    private fun DBIsEmpty() : Boolean {
        if (conn == null) return false
        val query = conn!!.createStatement()
        val emptyDBTestQuery = "SELECT * FROM sqlite_master WHERE type = 'table'"
        val result = query.executeQuery(emptyDBTestQuery)
        return result.next() == false
    }
    fun setupDB() {
        connect()
        if (conn == null) {
            println("Unknown Error: could not establish connection to database. Exiting.")
            return
        }
        if (!DBIsEmpty()) return
        val query = conn!!.createStatement()

        // Create Program table
        // Create TodoLists table
        // Create TodoItems table
        // Create ItemTags table
        // Create Tags table
        // Initialize TodoLists and Program table
        val queryStrings = listOf<String>(
            "CREATE TABLE TodoLists(list_id int PRIMARY KEY, maxItemID int);",
            "CREATE TABLE Program(list_id int, FOREIGN KEY(list_id) REFERENCES TodoLists(list_id));",
            "CREATE TABLE TodoItems(" +
                                    "item_id int PRIMARY KEY," +
                                    "title TEXT," +
                                    "description TEXT," +
                                    "timestamp DATETIME," +
                                    "deadline DATETIME," +
                                    "priority int," +
                                    "list_id int," +
                                    "completion BIT," +
                                    "FOREIGN KEY(list_id) REFERENCES TodoLists(list_id)" +
                                    ");",
            "CREATE TABLE Tags(name varchar(255) PRIMARY KEY, color int);",
            "CREATE TABLE ItemTags(" +
                                    "item_id int, " +
                                    "tag varchar(255)," +
                                    "FOREIGN KEY(item_id) REFERENCES TodoItems(item_id)" +
                                    "FOREIGN KEY(tag) REFERENCES Tags(name)" +
                                    ");",
            "INSERT INTO TodoLists(list_id, maxItemID) VALUES (\"0\", \"0\")",
            "INSERT INTO Program(list_id) VALUES (\"0\")"
            )

        for (queryString in queryStrings) {
            try {
                query.execute(queryString)
            } catch (ex : SQLException) {
                println(ex.message)
            }
        }
    }

    // Load data from database
    private fun load() {
        // Load Todolist from DB
        // this should be done in another ticket!
        /*
        try {
            val query = conn!!.createStatement()
            // configure max item id
            val findProgramQueryString = "SELECT maxItemID FROM TodoLists WHERE list_id='0'"
            var result = query.executeQuery(findProgramQueryString)
            result.next()
            theList.maxItemID = result.getInt("maxItemID")

            // fetch items and add to list
            val getItemsQueryString = "SELECT * FROM TodoItems WHERE list_id='0'"
            result = query.executeQuery(getItemsQueryString)
            while (result.next()) {
                val id = result.getInt("item_id")
                val title = result.getString("title")
                val description = result.getString("description")
                val deadline = result.getDate("deadline")
                val timestamp = result.getDate("timestamp")
                val priority = result.getInt("priority")
                val completion = result.getBoolean("completion")
                val item = TodoItem(title, description, deadline, priority, id, timestamp)

                if (completion) {
                    item.completeTask()
                }

                // add tags (skip for now)
                theList.add(item)
            }

        } catch (ex : SQLException) {
            println(ex.message)
        }
        */
    }

    private fun save() {
        /*
        // all items/tags should be saved on individual edits. We just need to update the maxItemID
        //   for our TodoList
        val query = conn!!.createStatement()
        val queryString = "UPDATE TodoList SET maxItemID = ${theList.maxItemID} WHERE list_id=0"
        query.execute(queryString)
        */
    }

    fun AddItemController(item : TodoItem) {
        // assume item has already been added to the TodoList
        // Potential issues:
        // - Parsing the date
        // Real issues:
        // - Tags
        // - Error handling
        try {
            if (conn != null) {
                val query = conn!!.createStatement()
                // Add the actual item
                val addItemQueryString = "INSERT INTO TodoItems(item_id, title, description, timestamp, deadline, priority, list_id) " +
                                  "VALUES (" +
                                            "\"${item.id.toString()}\", " +
                                            "\"${item.title}\", " +
                                            "\"${item.description}\", " +
                                            "\"${item.timestamp.toString()}\"," +
                                            "\"${item.deadline.toString()}\", " +
                                            "\"${item.priority.toString()}\", " +
                                            "\"0\");"
                query.execute(addItemQueryString)
                // Add tags
                for (tag in item.tags) {
                    // Search for the tag in the tags table.
                    // If it exists, just attach the item to the tag.
                    // If it doesn't exist, add it to the tags table.
                    val searchTagQueryString = "SELECT name FROM Tags WHERE name=\"${tag}\";"
                    val result = query.executeQuery(searchTagQueryString)
                    if (result.next() == false) { // if the tag doesn't exist, add it
                        val addTagQueryString = "INSERT INTO Tags(name, color) VALUES (\"${tag}\", \"0\");" // 0 as the "color" for now
                        query.execute(addTagQueryString)
                    }
                    // Add item-tag pairing
                    val addPairQueryString = "INSERT INTO ItemTags(item_id, tag) VALUES (\"${item.id}\", \"${tag}\");"
                    query.execute(addPairQueryString)
                }
            }
        } catch (ex : SQLException) {
            println(ex.message)
        }
    }
    /*
    private fun EditItemController() {
        try {
            if (conn != null) {
                // template; to be implemented
            }
        } catch (ex : SQLException) {
            println(ex.message)
        }
    }
     */

    /*
    // skeleton implementation for now
    private fun DeleteItemController(id : Int) {
        try {
            if (conn != null) {
                val query = conn!!.createStatement()
                // Delete from TodoItems
                val deleteItemString = "DELETE FROM TodoItems WHERE item_id = ${id};"
                query.execute(deleteItemString)
                // Delete from tag-item pairs
                val deletePairString = "DELETE FROM ItemTags WHERE item_id = ${id};"
                query.execute(deletePairString)
            }
        } catch (ex : SQLException) {
            println(ex.message)
        }
    }
    */


    private fun runCLI() {
        //load(saveFilePath)
        var command = ""
        println("Ketchup 0.2 by CS 346 Team 205")
        println("Welcome!")
        println("For help, type \"help\".")
        while (command != "quit" && command != "q") {
            print("(Ketchup) ")
            command = readLine()!!
            val regex = Regex("([^\"]\\S*|\".+?\")\\s*")
            val matches = regex.findAll(command)
            var commands = matches.map { it.groupValues[1] }.toList()
            var newCommand = CommandFactory.createFromArgs(commands)
            newCommand.execute(theList)
        }
        //save(saveFilePath)
    }

    fun run(args: Array<String>) {
        setupDB()
        load()
        var command = ""
        while (command != "1" || command != "2") {
            println("Menu\n1. Console Application\n2. Graphical Application\n3. Quit")
            command = readLine()!!
            if (command == "1") {
                runCLI()
                break
            } else if (command == "2") {
                Application.launch(MainApp::class.java)
                break
            } else if (command == "3") {
                System.exit(0)
            } else {
                println("Invalid command. Please Try again.")
            }
        }
        save()
        conn?.close()
    }
}