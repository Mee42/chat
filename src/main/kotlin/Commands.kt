import com.googlecode.lanterna.TextColor
import java.security.MessageDigest
import kotlin.experimental.and

enum class ArgsState { NONE,OPT,REQ }


class Command(val names: List<String>,
              val argState :ArgsState = ArgsState.NONE,
              val argsName: String? = null,
              val help: String = "no help here yet",
              val reqAdmin: Boolean = false,
              val runner: (String?) -> Unit)

val commands = mutableListOf<Command>()

fun byteArrayToHexString(b: ByteArray): String {
    var result = ""
    for (i in b.indices) {
        result += ((b[i] and 0xff.toByte()) + 0x100).toString(16).substring(1)
    }
    return result
}

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
        name = "login"
        name = "l"
        argState = ArgsState.REQ
        argsName = "password"
        help = "sign in"
        runner { hash = hash(it!!) }
    }
    command {
        name = "logout"
        name = "out"
        name = "o"
        help = "log out"
        runner { hash = "   " }
    }
    command {
        name = "people"
        name = "p"
        help = "list registered people"
        runner {
            addNewDisplayable(StandardDisplayable(listOf(textChunk {
                this.text = """
                    Carson: 081
                """.trimIndent()
            })))
        }
    }
    command {
        name = "clear"
        name = "c"
        runner {
            buffer.clear()
        }
        help = "clear the screen. may help lag"
    }
    command {
        name = "info"
        name = "i"
        help = "get info about accounts"
        runner {
            addNewDisplayable(StandardDisplayable(listOf(textChunk {
                this.text = "Accounts are very simple:\n\n" +
                    "sign up with :login <password>. " +
                    "Your password will go through a non-reversible hash algorithm to produce 3 hex digits. " +
                    "these three hex digits are displayed next to your name. " +
                    "If you want to prove your identity, tell the owner of this chat network *in person* your hex digits. " +
                    "You can view the list of registered people with :people. " +
                    "You can sign out with :signout."
            })))
        }
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