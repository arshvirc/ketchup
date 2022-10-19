fun main(args: Array<String>) {
    // data stored in a list internally
    // but saved in a file on exit
    val list = TodoList()

    var command = ""
    while(command != "quit") {
        command = readLine()!!
        val regex = Regex("([^\"]\\S*|\".+?\")\\s*")
        val matches = regex.findAll(command)
        var commands = matches.map{it.groupValues[1]}.toList()
//        println(commands)
        var newCommand = CommandFactory.createFromArgs(commands);
        newCommand.execute(list);
    }
}
