fun main(args: Array<String>) {
    // data stored in a list internally
    // but saved in a file on exit
    val list = mutableListOf<Item>()
    val filename = "data.json"

    // load previous to-do list
    list.restore(filename)


    var command = ""
    while(command != "quit") {
        command = readLine()!!
        var commands = command.trim().split("\\s+".toRegex()).toTypedArray()
        var newCommand = CommandFactory.createFromArgs(commands);
        newCommand.execute(list);
        list.save(filename);
    }



//    // process commands
//    val command = CommandFactory.createFromArgs(args)
//    command.execute(list)
//
//    // save to-do list (json)
//    list.save(filename)
}
