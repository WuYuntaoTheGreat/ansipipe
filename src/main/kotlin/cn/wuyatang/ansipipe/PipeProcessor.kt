package cn.wuyatang.ansipipe

/**
 * This interface defines a processor of Pipe.
 * The process will work tightly with the shell script.
 */
interface PipeProcessor: Ansi {

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
    fun process(width: Int, height: Int, input: String) : String?

    /**
     *
     */
    fun loop(){
        val inputReg = Regex("([0-9]+) +([0-9]+) *:(.*)")
        while(true) {
            val input = readLine() ?: continue
            val mt = inputReg.matchEntire(input) ?: continue

            val height = mt.groups[1]!!.value.toInt()
            val width  = mt.groups[2]!!.value.toInt()
            val inLine = mt.groups[3]!!.value

            print(process(width, height, inLine))
        }
    }

}

