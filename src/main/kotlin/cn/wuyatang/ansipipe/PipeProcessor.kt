package cn.wuyatang.ansipipe

import cn.wuyatang.ansipipe.Ansi.Key

/**
 * This interface defines a processor of Pipe.
 * The process will work tightly with the shell script.
 */
interface PipeProcessor: Ansi {

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
    fun process(raw: String, key: Key?): Boolean


    /**
     * Handle resize message from script.
     *
     * @param width The screen width in chars.
     * @param height The screen height in chars.
     *
     * @return True to continue loop, false to break.
     */
    fun resized(width: Int, height: Int): Boolean

    /**
     * Loop to read input from the string.
     */
    fun loop(){
        val resizePattern = Regex("<size ([0-9]+) ([0-9]+)>")
        while(true){
            val raw = readLine() ?: continue

            val resizeMr = resizePattern.matchEntire(raw)
            if(resizeMr != null){
                val h = resizeMr.groups[1]!!.value.toInt()
                val w = resizeMr.groups[2]!!.value.toInt()
                if(resized(w, h)){
                    continue
                } else {
                    break
                }
            }

            val key = try{
                Key.parse(raw)
            } catch (e: Exception){
                null
            }

            if(!process(raw, key)){
                break
            }
        }
    }

}

