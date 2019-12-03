@file:Suppress("unused")

package cn.wuyatang.ansipipe.demo

import cn.wuyatang.ansipipe.Ansi.Control.*
import cn.wuyatang.ansipipe.Ansi.Color.*
import cn.wuyatang.ansipipe.Ansi.Feature.*
import cn.wuyatang.ansipipe.Ansi.Key
import cn.wuyatang.ansipipe.PipeProcessor

class Demo: PipeProcessor {

    /**
     * Handle one loop of pipe process.
     * This function will:
     * 1. Read from the console 1 char.
     * 2. Then return a line of output.
     *
     * @param width : The width, in characters, of the terminal
     * @param height : The height, in characters, of the terminal
     * @param input : The raw input string from the terminal, shall be only one key press.
     *
     * @return The output to the shell script.
     */
    override fun process(width: Int, height: Int, input: String): String? {
        val sizeStr = "($width, $height) "
        val lenStr  = input.length.toString()
        val key     = Key.parseKey(input)?.name ?: ""
        val inLine  = input.map { c ->
            when (c) {
                in ' '..'~' -> c.toString()
                else -> "\\u${String.format("%04X", c.toInt())}"
            }
        }.joinToString("")

        val shdw = arrayOf(black.bgBr)
        val dlgTxt = arrayOf(white.fgBr, blue.bg)

        var r: Int = height / 2 - 5
        var c: Int = width / 2 - 20

        var ret = "" //+ clrAll
        ret += save + ""
        ret += pos(r++, c) + "                                            ".fmt(*dlgTxt)
        ret += pos(r++, c) + " +----(Press Ctrl-C to exit)--------------+ ".fmt(*dlgTxt)
        ret += pos(r++, c) + " |                                        | ".fmt(*dlgTxt) + " ".fmt(*shdw)
        ret += pos(r++, c) + " |                                        | ".fmt(*dlgTxt) + " ".fmt(*shdw)
        ret += pos(r++, c) + " |                                        | ".fmt(*dlgTxt) + " ".fmt(*shdw)
        ret += pos(r++, c) + " |                                        | ".fmt(*dlgTxt) + " ".fmt(*shdw)
        ret += pos(r++, c) + " |                                        | ".fmt(*dlgTxt) + " ".fmt(*shdw)
        ret += pos(r++, c) + " |                                        | ".fmt(*dlgTxt) + " ".fmt(*shdw)
        ret += pos(r++, c) + " +----------------------------------------+ ".fmt(*dlgTxt) + " ".fmt(*shdw)
        ret += pos(r++, c) + "                                            ".fmt(*dlgTxt) + " ".fmt(*shdw)
        ret += pos(r, c + 1) + "                                            ".fmt(*shdw)

        r = height / 2 - 5 + 3
        c = width / 2 - 20 + 3
        ret += pos(r++, c) + "Window: ".fmt(*dlgTxt) + sizeStr.fmt(green.fgBr, blue.bg)
        ret += pos(r++, c) + "Length: ".fmt(*dlgTxt) + lenStr.fmt(*dlgTxt)
        ret += pos(r++, c) + "Input:  ".fmt(*dlgTxt) + "\"$inLine\"".fmt(bold, red.fgBr, blue.bg)
        ret += pos(r++, c) + "Key:    ".fmt(*dlgTxt) + key.fmt(*dlgTxt)
        ret += pos(r, c)
        ret += restore + ""

        return ret
    }
}

/**
 * The main entrance.
 */
fun main(args: Array<String>){
    Demo().loop()
}


