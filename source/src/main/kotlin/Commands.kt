import java.text.SimpleDateFormat
import java.util.Date
import kotlin.reflect.typeOf

val supportedDateFormatStrings = listOf<String>("d/M/y", "d-M-y", "yyyy/M/d", "yyyy-M-d")

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

// Command pattern
// represents all valid commands that can be issued by the user
// any functionality for a given command should be contained in that class

class CommandParseException(message : String) : Exception("Command Parsing Error: $message.")
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

    // Parse flags (basic flag parsing for now)
    fun tryFlagParse (args: List<String>, item : TodoItem) : TodoItem {

        var id : Int = item.id
        var title : String = item.title
        var description : String = item.description
        var deadline : Date? = item.deadline
        var priority : Int = item.priority
        var flag : String? = null

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
                        } catch (ex : Exception) {
                            throw CommandParseException("invalid/missing priority value (must be an integer)")
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
        return TodoItem(title, description, deadline, priority, id)
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
            val newArgs:MutableList<String> = args.toMutableList<String>();
            newArgs.removeAt(1);

            val newItem = item?.let { tryFlagParse(newArgs, it) };


            if (item != null) {
                if (newItem != null) {
                    items.edit(item, newItem)
                }
            };
        } catch (c: CommandParseException) {
            println(c.message)
        }
        // IMPLEMENT
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
            println()
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
                if(args.size > 2) {
                    throw CommandParseException("too many arguments for list. Type 'help list'")
                }
                val flag:String = args[1];
                val acceptedFlags = listOf<String>("p", "priority", "due", "duedate", "deadline")
                if(flag[0] != '-' || flag.substring(1) !in acceptedFlags) {
                    throw CommandParseException("Flag not recognized. Type 'help list'")
                }

                val temp = TodoList(items)
                temp.sort(flag.substring(1))
                temp.displayList()
            } catch (c: CommandParseException) {
                println(c.message)
            }
        }
    }
}

class HelpCommand(val args: List<String>) : Command {
    override fun execute(items: TodoList) {
        if (args.size == 1) {
            // Should probably be more detailed
            println("Usage: [add|del|list|quit]. Type \"help [command name] for detailed options.\"")
            return
        }
        println(when (args[1]) {
            "a", "add" -> "Usage: a/add \n\t-title/t [title]\n\t-desc/description [description]\n\t-due/duedate/deadline [date]\n\t-priority/p [priority]"
            "c", "complete" -> "Usage: c/complete [id]"
            "d", "del", "delete" -> "Usage: d/del/delete [id] \n"
            "e", "edit" -> "Usage: e/edit [id] \n"+
                    "\t-title/t [title]\n" +
                    "\t-desc/description [description]\n" +
                    "\t-due/duedate/deadline [date]\n" +
                    "\t-priority/p [priority]"
            "h", "help" -> "Usage: h/help [add/complete/delete/edit/list/help/list/quit]"
            "l", "list" -> "Usage: l/list (one of -p/priority or -due/duedate/deadline)"
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
        println("Undefined command: \"${args[1]}\". Try \"help\".")
    }
}

class QuitCommand() : Command {
    override fun execute(items: TodoList) {
        println("See you next time!");
    }
}