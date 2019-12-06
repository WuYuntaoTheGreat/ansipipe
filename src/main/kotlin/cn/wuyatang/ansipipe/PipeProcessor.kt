package cn.wuyatang.ansipipe

import cn.wuyatang.ansipipe.Ansi.Key
import java.lang.Exception

/**
 * This interface defines a processor of Pipe.
 * The process will work tightly with the shell script.
 */
abstract class PipeProcessor: Ansi {
    val pen = PipePen()

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
    abstract fun process(raw: String, key: Key?, pen: PipePen): Boolean

    /**
     * Loop to read input from the string.
     */
    fun loop(){
        val inputReg = Regex("<size ([0-9]+) ([0-9]+)>")
        while(true){
            val raw = readLine() ?: continue

            if(inputReg.matchEntire(raw)?.let { mt ->
                    pen.h = mt.groups[1]!!.value.toInt()
                    pen.w = mt.groups[2]!!.value.toInt()
                    true
            } == true) continue

            val key = try{
                Key.parse(raw)
            } catch (e: Exception){
                null
            }
            if(!process(raw, key, pen)){
                break
            }
        }
    }

}

