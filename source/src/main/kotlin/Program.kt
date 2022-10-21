import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.*
import java.io.File
import java.nio.file.Files

class Program {
    private var theList : TodoList = TodoList()
    private var saveFilePath : String = "./tasks.json"
    private var saveFile = File(saveFilePath)
    val format = Json { encodeDefaults = true }

    fun runCLI() {
        var fileCreated : Boolean = saveFile.createNewFile()
        if (fileCreated) {
            println("No save file found. Creating new file.")
        } else {
            val fileContents : String = saveFile.readText()
            try {
                theList = Json.decodeFromString(fileContents)
            } catch (decodingException : Exception) {
                // empty catch, don't need to do anything
            }
        }

        var command = ""
        println("Ketchup 0.1 by CS 346 Team 205")
        println("Welcome!")
        println("For help, type \"help\".")
        while (command != "quit" && command != "q") {
            print("(Ketchup) ")
            command = readLine()!!
            val regex = Regex("([^\"]\\S*|\".+?\")\\s*")
            val matches = regex.findAll(command)
            var commands = matches.map{it.groupValues[1]}.toList()
    //        println(commands)
            var newCommand = CommandFactory.createFromArgs(commands)
            newCommand.execute(theList)
            // just test encoding for now
            val JsonList = format.encodeToString(this.theList)
            saveFile.writeText(JsonList)
        }

    }
}