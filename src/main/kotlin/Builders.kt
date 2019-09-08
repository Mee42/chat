import com.googlecode.lanterna.TextColor
import com.sun.security.auth.callback.TextCallbackHandler
import java.lang.IllegalStateException

class TextChunkBuilder {
    var text: String? = null
    var color: TextColor = TextColor.ANSI.WHITE
    fun build():TextChunk{
        return TextChunk(
                text = text ?: throw IllegalStateException("no text specified"),
                color = color)
    }
}


fun TextChunk.and(block: TextChunkBuilder.() -> Unit):List<TextChunk>{
    return listOf(this,textChunk(block))
}
fun List<TextChunk>.and(block: TextChunkBuilder.() -> Unit):List<TextChunk>{
    val new = mutableListOf<TextChunk>()
    new.addAll(this)
    new.add(textChunk(block))
    return new
}
fun textChunk(block: TextChunkBuilder.() -> Unit):TextChunk{
    return TextChunkBuilder().apply(block).build()
}


class CommandBuilder{
    private val names = mutableListOf<String>()
    var name: String
        set(value){ names.add(value) }
        get() = error("don't do that")


    var argState :ArgsState = ArgsState.NONE
    var argsName: String? = null
    var help: String = "no help here yet"
    var reqAdmin: Boolean = false
    var runner: ((String?) -> Unit)? = null
    fun runner(block: (String?) -> Unit){
        runner = block
    }
    fun build():Command{
        check(names.isNotEmpty()) { "you need at least one name" }
        return Command(
                names = names,
                argState = argState,
                argsName = argsName,
                help = help,
                reqAdmin = reqAdmin,
                runner = checkNotNull(runner))
    }
}