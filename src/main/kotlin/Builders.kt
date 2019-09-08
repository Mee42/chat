import com.googlecode.lanterna.TextColor
import com.sun.security.auth.callback.TextCallbackHandler
import java.lang.IllegalStateException

class TextChunkBuilder {
    var text: String? = null
    var color: TextColor.ANSI = TextColor.ANSI.WHITE
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
    return this + textChunk(block)
}
fun textChunk(block: TextChunkBuilder.() -> Unit):TextChunk{
    return TextChunkBuilder().apply(block).build()
}


class CommandBuilder{
    private val names = mutableListOf<String>()
    var name: String
        set(value){ names.add(name) }
        get() = error("don't do that")


    var argState :ArgsState = ArgsState.NONE
    var argsName: String? = null
    var help: String = "no help here yet"
    var reqAdmin: Boolean = false
    var runner: ((String?) -> Unit)? = null
    fun build():Command{
//        if (names.isEmpty())
//            throw IllegalStateException("")
//        return Command(
//                names = names
//        )
        TODO("not implemented")
    }
}