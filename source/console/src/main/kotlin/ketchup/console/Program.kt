package ketchup.console

class Program(list: TodoList = TodoList()) {
    private var theList : TodoList = list

    fun runCLI() {
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
}