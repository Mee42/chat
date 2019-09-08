@file:Suppress("LeakingThis")

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import java.lang.Exception
import java.lang.IllegalStateException
import kotlin.concurrent.thread
import kotlin.system.exitProcess


val screen: TerminalScreen = DefaultTerminalFactory().createScreen()


val buffer = mutableListOf<Displayable>()

fun addNewDisplayable(str: Displayable){
    buffer.add(0,str)
    redraw()
}

val inputBuffer = mutableListOf<Char>()

var onCommand :(String) -> Unit= { println("command ran \"$it\"") }

fun bufferInputChar(input: Char){
    when {
        input.toInt() == 0x8 -> //the backspace char
            if (inputBuffer.isNotEmpty())
                inputBuffer.removeAt(inputBuffer.lastIndex)
        input.toInt() == 0xa -> { // enter
            val command = inputBuffer.fold(""){a,b -> "$a$b"}
            inputBuffer.clear()
            onCommand(command)
        }
        else -> inputBuffer.add(input)
    }
}


private fun redraw(){
    screen.clear()
    var row = screen.terminalSize.rows.minus(5)
    for (line in buffer){
        var lines = 1
        var col = 1
        for (char in line.string){
            col ++
            if (col > screen.terminalSize.columns.minus(1) || char == '\n'){
                col = 1
                lines ++
            }
        }
        row -= lines
        if (row < 0) break
        var index = 0
        loop@ for (rowStart in row .. row + lines){
            inner@ for (colStart in 1 until screen.terminalSize.columns.minus(1)){
                if (index == line.string.length) break@loop
                val char = line.string[index]
                index++
                val color = line.getColorForCharacter(index)
                if (char == '\n') break@inner
                screen.setCharacter(colStart,rowStart, TextCharacter(char).withForegroundColor(color))
            }
        }
//        screen.newTextGraphics().putString(1,row,line)
    }

    val inputLine = screen.terminalSize.rows.minus(2)
    screen.setCharacter(5,inputLine, TextCharacter('>'))
    screen.cursorPosition = TerminalPosition(6 + inputBuffer.size,inputLine)
    for ((index, char) in inputBuffer.withIndex()){
        screen.setCharacter(6 + index,inputLine, TextCharacter(char))
    }
    screen.refresh()
}

fun stopProgram():Nothing{
    screen.stopScreen(false)
    @Suppress("UNREACHABLE_CODE")
    return exitProcess(0)
}
fun startScreen(){
    screen.startScreen()
    screen.refresh()
    redraw()
    thread {
        while (true){
            try {
                val input: KeyStroke = screen.pollInput() ?: continue
                if (input.keyType == KeyType.EOF) {
                    exitProcess(0)
                }
                if (input.keyType == KeyType.PageDown) {
                    addNewDisplayable(internalErrorDis(IllegalStateException("This is an example error message")))
                    continue
                }
                if (input.character == null) continue
                bufferInputChar(input.character)
            } catch (e :Exception){
                addNewDisplayable(internalErrorDis(e))
            }
            redraw()
        }
    }
}