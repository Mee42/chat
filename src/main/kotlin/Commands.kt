
enum class ArgsState { NONE,OPT,REQ }


class Command(val names: List<String>,
              val argState :ArgsState = ArgsState.NONE,
              val argsName: String? = null,
              val help: String = "no help here yet",
              val reqAdmin: Boolean = false,
              val runner: (String?) -> Unit)

val commands = mutableListOf(

        Command(
                names = listOf("exit","q"),
                runner = { stopProgram() },
                help = "exit the program"
        ),
        Command(
                names = listOf("name"),
                argsName = "name",
                argState = ArgsState.REQ,
                help = "set your name",
                runner = { name = it!! }
        ),
        Command(
                names = listOf("help"),
                help = "prints this help",
                runner = {
                        val list = mutableListOf<TextChunk>()
                        list += textChunk { text = "Help Menu:\n" }
//                        for(command in commands){
//
//                        }
                }
        )
)

fun command(block: CommandBuilder.() -> Unit) {

}