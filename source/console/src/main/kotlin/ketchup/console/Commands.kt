package ketchup.console

import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.Date

val supportedDateFormatStrings = listOf<String>("dd/MM/yyyy", "dd-MM-yyyy")

// Factory pattern
// generate a command based on the arguments passed in
object CommandFactory {
    fun createFromArgs(args: List<String>): Command = if (args.isEmpty()) {
        UndefinedCommand(args)
    } else {
        when (args[0]) {
            "a", "add" -> AddCommand(args)
            "c", "complete" -> CompleteCommand(args)
            "d", "del", "delete" -> DelCommand(args)
            "e", "edit" -> EditCommand(args)
            "h", "help" -> HelpCommand(args)
            "l", "list" -> ListCommand(args)
            "q", "quit" -> QuitCommand()
            else -> UndefinedCommand(args)
        }
    }
}

// ketchup.console.Command pattern
// represents all valid commands that can be issued by the user
// any functionality for a given command should be contained in that class

class CommandParseException(message : String) : Exception("ketchup.console.Command Parsing Error: $message.")
class InvalidPriorityException(message: String) : Exception("Invalid Priority Error: $message." )
interface Command {
    fun execute(items : TodoList)

    fun tryDateParse(dateAsString : String) : Date? {

        for (format in supportedDateFormatStrings) {
            try {
                return SimpleDateFormat(format).parse(dateAsString)
            } catch (e : Exception) {
                // don't need to do anything, move to the next format
            }
        }
        return null
    }

    fun priorityParse(priority : Int): Int {
        if(priority > 3 || priority < 0) {
            throw InvalidPriorityException("priority must be 0, 1, or 2")
        }

        return priority
    }

    // Parse flags (basic flag parsing for now)
    fun tryFlagParse (args: List<String>, item : TodoItem) : TodoItem {

        val id : Int = item.id
        var title : String = item.title
        var description : String = item.description
        var deadline : Date? = item.deadline
        var priority : Int = item.priority
        var flag : String? = null
        var tags: MutableList<String> = mutableListOf()

        for (arg in args.subList(1, args.size)) {

            if (arg[0] == '-') { // flag
                if (flag != null) {
                    // 2 flags in a row
                    throw CommandParseException("each flag should be followed by an argument")
                }
                flag = arg
            } else { // not a flag
                when (flag) {
                    "-t", "-title" -> title = arg
                    "-desc", "-description" -> description = arg
                    "-due", "-duedate", "-deadline" -> {
                        deadline = tryDateParse(arg)
                        if (deadline == null) {
                            throw CommandParseException("invalid deadline format")
                        }
                    }
                    "-p", "-priority" -> {
                        try {
                            priority = arg.toInt()
                            priority = priorityParse(priority)
                        } catch(pex: InvalidPriorityException) {
                            throw CommandParseException("priority must be 0 (no priority), 1 (low), 2 (medium), or 3 (high).")
                        } catch (ex : Exception) {
                            throw CommandParseException("invalid/missing priority value (must be an integer)")
                        }
                    }
                    "-tags" -> {
                        try {
                            var valid = true
                            if (arg[0] != '"' || arg[arg.length - 1] != '"') {
                                valid = false
                            }

                            if (!valid) {
                                throw CommandParseException("")
                            }

                            var parsed = arg.substring(1, arg.length - 1).split(',');

                            for(item in parsed) {
                                tags.add(item.trim())
                            }

                        } catch (cmd: CommandParseException) {
                            throw CommandParseException("tags must be wrapped in double quotes, seperated by chars. ex. -tags \"tag1, tag2\"")
                        } catch(ex: Exception) {
                            throw CommandParseException("Cannot parse tags.")
                        }
                    }
                    null -> {
                        println(args)
                        throw CommandParseException("unable to parse command")
                    }
                    else -> {
                        throw CommandParseException("invalid flag \"${flag}\"")
                    }
                }
                flag = null
            }
        }
        if (flag != null) {
            throw CommandParseException("the last flag was not followed by any arguments")
        } else if (title == "") {
            throw CommandParseException("an item must have a title")
        }
        return TodoItem(title, description, deadline, priority, id, tags)
    }
}

class AddCommand(val args: List<String>) : Command {
    override fun execute(items: TodoList) {
        if (args.size == 1) {
            println("Error: no details specified. Try \"help add\".")
        } else if (args.size == 2 && args[1][0] != '-') {
            items.add(args[1])
        } else {
            try {
                val item = tryFlagParse(args, TodoItem())
                items.add(item)
            } catch (c : CommandParseException) {
                println(c.message)
            }
        }
    }
}

class EditCommand(val args: List<String>) : Command {
    override fun execute(items: TodoList) {
        if (args.size == 1) {
            println("Error: specify a search query for the item you want to edit.")
            return
        }
        try {
            val itemId = args[1].toInt()
            val item = items.getById(itemId)
            val newArgs:MutableList<String> = args.toMutableList<String>()
            newArgs.removeAt(1)

            val newItem = item?.let { tryFlagParse(newArgs, it) }


            if (item != null) {
                if (newItem != null) {
                    items.edit(item, newItem)
                }
            } else {
                println("Item not found.")
            }
        } catch (c: CommandParseException) {
            println(c.message)
        } catch (e : NumberFormatException) {
            println("Specify the ID of the item you want to edit.")
        } catch (e : Exception) {
            println("Unknown error.")
        }
    }
}

// Remove an item from the to-do list.
class DelCommand(val args: List<String>) : Command {
    override fun execute(items: TodoList) {
        if (args.size == 1) {
            println("Error: specify a search query for the item(s) you want to delete")
            return
        }
        try {
            val toDeleteID = args[1].toInt()
            items.removeIf { it.id == toDeleteID }
        } catch (e : Exception) {
            // Then it's a string, so we're searching by title
            items.removeIf { it.title == args[1] }
        }
    }
}

class ListCommand(val args: List<String>) : Command {
    override fun execute(items: TodoList) {
        if(args.size == 1) {
            items.displayList()
        } else {
            try {
                if(args.size == 2) {
                    val flag:String = args[1]
                    val sortFlags = listOf<String>("p", "priority", "due", "duedate", "deadline")
                    if(flag[0] != '-' || flag.substring(1) !in sortFlags) {
                        throw CommandParseException("Flag not recognized. Type 'help list'")
                    }
                    val temp = TodoList(items)
                    temp.sort(flag.substring(1))
                    temp.displayList()
                } else if(args.size == 3) {
                    val flag:String = args[1]
                    val filterFlags = listOf<String>("f", "filter")
                    if(flag[0] != '-' || flag.substring(1) !in filterFlags) {
                        throw CommandParseException("Flag not recognized. Type 'help list'")
                    }

                    val tags = args[2]

                    var valid: Boolean = true;
                    if (tags[0] != '"' || tags[tags.length - 1] != '"') {
                        valid = false;
                    }

                    if (!valid) {
                        throw CommandParseException("tags must be wrapped in double quotes, seperated by chars. ex. list -f \"tag1, tag2\"")
                    }

                    var parsed: MutableList<String> = tags.substring(1, tags.length - 1).split(',').map { it.trim() } as MutableList<String>;


                    val temp = TodoList(items)
                    temp.filter(parsed)
                    temp.displayList()
                } else {
                    throw CommandParseException("too many arguments. Type 'help list'")
                }
            } catch (c: CommandParseException) {
                println(c.message)
            }
        }
    }
}

class HelpCommand(val args: List<String>) : Command {
    override fun execute(items: TodoList) {
        if (args.size == 1) {
            println("Type \"help [command name]\" for detailed options. Available commands:")
            println("add:       add a new item to the to-do list")
            println("complete:  mark a task as complete")
            println("delete:    delete a task from the to-do list")
            println("edit:      edit an existing task")
            println("help:      show help menu")
            println("list:      display all tasks")
            println("quit:      exit the application")
            return
        }
        println(when (args[1]) {
            "a", "add" -> "Usage: a/add \n\t-title/t [title]\n\t-desc/description [description]\n\t-due/duedate/deadline [date]\n\t-priority/p [priority]"
            "c", "complete" -> "Usage: c/complete [id]"
            "d", "del", "delete" -> "Usage: d/del/delete [id]"
            "e", "edit" -> "Usage: e/edit [id] \n"+
                    "\t-title/t [title]\n" +
                    "\t-desc/description [description]\n" +
                    "\t-due/duedate/deadline [date]\n" +
                    "\t-priority/p [priority]"
            "h", "help" -> "Usage: h/help [add/complete/delete/edit/list/help/list/quit]"
            "l", "list" -> "Usage: l/list (one of -p/priority or -due/duedate/deadline to sort by either field)"
            "q", "quit" -> "Usage: q/quit"
            else -> "Undefined command \"${args[1]}."
        })
    }
}

class CompleteCommand(val args : List<String>) : Command {
    override fun execute(items: TodoList) {
        if (args.size == 1) {
            println("Error: specify a search query for the item(s) you want to complete.")
            return
        }
        try {
            val toCompleteID = args[1].toInt()
            items.completeIf { it.id == toCompleteID }
        } catch (e : Exception) {
            // Then it's a string, so we're searching by title
            items.completeIf { it.title == args[1] }
        }
    }
}

class UndefinedCommand(val args : List<String>) : Command {
    override fun execute(items: TodoList) {
        if(args.isEmpty()) {
            println("Undefined command: Try \"help\"")
        } else {
            println("Undefined command: \"${args[0]}\". Try \"help\".")
        }
    }
}

class QuitCommand() : Command {
    override fun execute(items: TodoList) {
        println("See you next time!")
    }
}