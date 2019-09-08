import com.googlecode.lanterna.TextColor

enum class ArgsState { NONE,OPT,REQ }


class Command(val names: List<String>,
              val argState :ArgsState = ArgsState.NONE,
              val argsName: String? = null,
              val help: String = "no help here yet",
              val reqAdmin: Boolean = false,
              val runner: (String?) -> Unit)

val commands = mutableListOf<Command>()

fun initCommands(){
    command {
        name = "exit"
        name = "q"
        runner { stopProgram() }
        help = "exit the program"
    }
    command {
        name = "name"
        argState = ArgsState.REQ
        argsName = "name"
        help = "set your name"
        runner { username = it!! }
    }
    command {
        name = "help"
        help = "prints this help"
        runner {
            val list = mutableListOf<TextChunk>()
            list += textChunk { text = "Help Menu:\n" }
            for(command in commands){
                var argText = (" ".repeat(10) + when(command.argState){
                    ArgsState.REQ -> "<${command.argState}>"
                    ArgsState.OPT -> "(${command.argState})"
                    ArgsState.NONE -> ""
                }.padEnd(15))
                argText = if (argText.isNotBlank()){
                    argText.trimStart()
                } else {
                    " ".repeat(15)
                }
                list.addAll(textChunk {
                    color = TextColor.ANSI.GREEN
                    text = command.names.first().padEnd(10)
                }.and {
                    text = argText
                    color = TextColor.ANSI.YELLOW
                }
                    .and {
                        text = command.help
                        color = TextColor.RGB(70,70,70)
                    }.and { text = "\n" })
            }
            addNewDisplayable(StandardDisplayable(list))
        }
    }
}

fun command(block: CommandBuilder.() -> Unit) {
        commands.add(CommandBuilder().apply(block).build())
}