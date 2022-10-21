import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File
import java.nio.file.Files

class Program {
    private var theList : TodoList = TodoList()
    private var saveFilePath : String = "./saveFileName.json"
    private var saveFile = File(saveFilePath)

    val format = Json { encodeDefaults = true }

    fun runCLI() {
        theList = Json.decodeFromString(saveFile.readText())
        var command = ""
        while (command != "quit" && command != "q") {
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