package cn.wuyatang.ansipipe.ui

import cn.wuyatang.ansipipe.Ansi
import cn.wuyatang.ansipipe.PipeProcessor

/**
 * The singleton screen object.
 * This stores information of the terminal screen.
 */
open class Screen: Window(
    borderStyle = "",
    shadowStyle = "",
    bgFormat = null), PipeProcessor  {

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
    override fun process(raw: String, key: Ansi.Key?): Boolean {
        // TODO: parse
        return true
    }

    /**
     * Handle resize message from script.
     *
     * @param width The screen width in chars.
     * @param height The screen height in chars.
     *
     * @return True to continue loop, false to break.
     */
    override fun resized(width: Int, height: Int): Boolean {
        inner.w = width
        inner.h = height
        return true
    }
}

