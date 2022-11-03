import javafx.application.Application
import java.lang.System.exit

fun main(args: Array<String>) {
    val program : Program = Program()
    var command = ""
    while(command != "1" || command != "2") {
        println("Menu\n1. Console Application\n2. Graphical Application\n3. Quit")
        command = readLine()!!
        if (command == "1") {
            program.runCLI()
            break
        } else if (command == "2") {
            Application.launch(MainApp::class.java)
            break
        } else if (command == "3") {
            exit(0)
        } else {
            println("Invalid command. Please Try Again.")
        }
    }


}
