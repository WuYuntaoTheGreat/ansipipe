@file:Suppress("unused")

package cn.wuyatang.ansipipe.demo

import cn.wuyatang.ansipipe.Ansi.Color.*
import cn.wuyatang.ansipipe.Ansi.Control.*
import cn.wuyatang.ansipipe.Ansi.Feature.*
import cn.wuyatang.ansipipe.Ansi.Key
import cn.wuyatang.ansipipe.PipeProcessor
import cn.wuyatang.ansipipe.ui.AnsiSequence
import cn.wuyatang.ansipipe.ui.Window

/**
 * The Demo processor.
 */
class Demo: PipeProcessor {
    var screenWidth:  Int = 0
    var screenHeight: Int = 0

    /**
     * Handle resize message from script.
     *
     * @param width The screen width in chars.
     * @param height The screen height in chars.
     *
     * @return True to continue loop, false to break.
     */
    override fun resized(width: Int, height: Int): Boolean {
        screenWidth  = width
        screenHeight = height
        return true
    }

    /**
     * Handle one loop of pipe process.
     * This function will:
     * 1. Read from the console 1 char.
     * 2. Then output to the console.
     *
     * @param raw The raw input string from the console.
     * @param key The input key from the console.
     *
     * @return True to continue loop, false to break.
     */
    override fun process(raw: String, key: Key?): Boolean {
        val sizeStr = "(${screenWidth}, ${screenHeight}) "
        val lenStr  = raw.length.toString()
        val keyName = key?.name ?: ""
        val inLine  = raw
            .map { c ->
                when (c) {
                    in ' '..'~' -> c.toString()
                    else -> "\\u${String.format("%04X", c.toInt())}"
                }
            }
            .joinToString("")
            .let { "\"$it\"" }

        val dlgTxt = Format(White.fgBr)
        val grnTxt = Format(Green.fgBr)
        val redTxt = Format(Bold, Red.fgBr)

        val content = AnsiSequence()
        content + dlgTxt + "Window: " + grnTxt + sizeStr    + "\n"
        content + dlgTxt + "Length: " + dlgTxt + lenStr     + "\n"
        content + dlgTxt + "Input:  " + redTxt + inLine     + "\n"
        content + dlgTxt + "Key:    " + grnTxt + keyName    + "\n"

        val window = Window(20, 20, 40, 10)

        print(Save)

        window.render()
        window.render(content)

//        pen + save
//
//        pen.center(-20, -5)
//        pen.block(40, 10, dlgTxt)
//
//        pen.offset(2, 1)
//        pen.d() + dlgTxt { "Window: " } + grnTxt { sizeStr  }
//        pen.d() + dlgTxt { "Length: " } + dlgTxt { lenStr   }
//        pen.d() + dlgTxt { "Input:  " } + redTxt { inLine   }
//        pen.d() + dlgTxt { "Key:    " } + dlgTxt { keyName  }
//
//        pen + restore

        print(Restore)
        return true
    }
}

/**
 * The main entrance.
 */
fun main(args: Array<String>){
    Demo().loop()
}


