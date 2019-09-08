import com.googlecode.lanterna.TextColor
import java.lang.RuntimeException

abstract class Displayable {
    abstract val string:String
    abstract fun getColorForCharacter(index: Int): TextColor
}
data class TextChunk(val text: String,val color: TextColor)


class StandardDisplayable(private val chunks :List<TextChunk>): Displayable(){
    constructor(vararg chunks: TextChunk):this(chunks.toList())

    override val string: String = chunks.fold(""){a,b -> a + b.text }

    override fun getColorForCharacter(index: Int): TextColor {
        var j = 0
        for (chunk in chunks){
            if (chunk.text.length + j > index-1)
                return chunk.color
            j += chunk.text.length
        }
        return TextColor.ANSI.RED
    }
}

fun userErrorDis(errorMessage: String):Displayable = StandardDisplayable(
        TextChunk("Error: ",TextColor.ANSI.RED),
        TextChunk(errorMessage,TextColor.ANSI.YELLOW)
)
fun warningDis(warning: String):Displayable = StandardDisplayable(
        TextChunk("Warning: ",TextColor.ANSI.YELLOW),
        TextChunk(warning,TextColor.ANSI.WHITE)
)

fun internalErrorDis(throwable: Throwable):Displayable = internalErrorDis(throwable.javaClass.name + " " + throwable.message)
fun internalErrorDis(text: String):Displayable = StandardDisplayable(
        TextChunk("Internal Error: ",TextColor.ANSI.YELLOW),
        TextChunk(text,TextColor.ANSI.WHITE)
)
abstract class DisplayableException(message: String? = null):RuntimeException(message){
    abstract fun toDisplayable():Displayable
}

class UserErrorException(override val message: String):DisplayableException(message){
    override fun toDisplayable() = userErrorDis(message)
}

class Message(private val user: String, message: String) :Displayable() {
    override val string: String = "$user: $message"

    override fun getColorForCharacter(index: Int): TextColor.ANSI {
        return when {
            index - 1== user.length -> TextColor.ANSI.RED
            index - 1 < user.length -> TextColor.ANSI.YELLOW
            else -> TextColor.ANSI.GREEN
        }
    }
}
