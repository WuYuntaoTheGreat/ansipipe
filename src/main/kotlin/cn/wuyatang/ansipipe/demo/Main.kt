@file:Suppress("unused")

package cn.wuyatang.ansipipe.demo

import cn.wuyatang.ansipipe.Ansi.Color.*
import cn.wuyatang.ansipipe.Ansi.Control.*
import cn.wuyatang.ansipipe.Ansi.Feature.bold
import cn.wuyatang.ansipipe.Ansi.Key
import cn.wuyatang.ansipipe.PipePen
import cn.wuyatang.ansipipe.PipeProcessor

class Demo: PipeProcessor() {

    /**
     * Handle one loop of pipe process.
     * This function will:
     * 1. Read from the console 1 char.
     * 2. Then output to the console.
     *
     * @param raw The raw input string from the console.
     * @param key The input key from the console.
     * @param pen The PipePen object.
     *
     * @return True to continue loop, false to break.
     */
    override fun process(raw: String, key: Key?, pen: PipePen): Boolean {
        var p = pen
        val sizeStr = "(${pen.w}, ${pen.h}) "
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

        val dlgTxt = arrayOf(white.fgBr, blue.bg)

        p + save

        p.center(-20, -5)
        p.block(40, 10, *dlgTxt)

        p.offset(2, 2)
        p++ + fmt(*dlgTxt)  + "Window: " + fmt() + fmt(green.fgBr, blue.bg)     + sizeStr   + fmt()
        p++ + fmt(*dlgTxt)  + "Length: " + fmt() + fmt(*dlgTxt)                 + lenStr    + fmt()
        p++ + fmt(*dlgTxt)  + "Input:  " + fmt() + fmt(bold, red.fgBr, blue.bg) + inLine    + fmt()
        p++ + fmt(*dlgTxt)  + "Key:    " + fmt() + fmt(*dlgTxt)                 + keyName   + fmt()

        p + restore

        return true
    }
}

/**
 * The main entrance.
 */
fun main(args: Array<String>){
    Demo().loop()
}


