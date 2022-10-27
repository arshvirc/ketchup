import javafx.application.Application

fun main(args: Array<String>) {
    val program : Program = Program()
    program.runCLI()
    Application.launch(MainApp::class.java)
}
