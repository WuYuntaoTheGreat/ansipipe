package cn.wuyatang.ansipipe

import cn.wuyatang.ansipipe.Ansi.Control
import cn.wuyatang.ansipipe.Ansi.Control.*
import cn.wuyatang.ansipipe.Ansi.Color.*
import java.util.*

/**
 * The pen object.
 * @param w The width of the screen
 * @param h The height of the screen
 */
class PipePen(val w: Int, val h: Int) {
    val cx: Int get() = w / 2
    val cy: Int get() = h / 2

    var x: Int = 1
    var y: Int = 1

    val stack = LinkedList<Pair<Int, Int>>()

    fun push(): PipePen {
        stack.push(Pair(x, y))
        return this
    }

    fun pop(): PipePen {
        stack.pop().let {
            x = it.first
            y = it.second
        }
        this + pos(y, x)
        return this
    }

    /**
     * Set the coordinate of the pen.
     */
    operator fun invoke(x: Int, y: Int): PipePen {
        this.x = x
        this.y = y
        this + pos(y, x)
        return this
    }

    operator fun invoke(): PipePen {
        this + pos(y, x)
        return this
    }

    fun offset(offX: Int = 0, offY: Int = 0): PipePen {
        this.x = x + offX
        this.y = y + offY
        this + pos(y, x)
        return this
    }

    fun center(offX: Int = 0, offY: Int = 0): PipePen {
        this.x = cx + offX
        this.y = cy + offY
        this + pos(y, x)
        return this
    }

    operator fun inc(): PipePen {
        y++
        this + pos(y, x)
        return this
    }

    operator fun plus (s: String): PipePen {
        print(s)
        return this
    }

    operator fun plus (c: Control): PipePen {
        print(c.v)
        return this
    }

    companion object {
        val DEFAULT_BORDERS_SHADOW = """
            +-+.
            |.|#
            +-+#
            .###
        """.trimIndent()

        val DEFAULT_BLANK = """
            ....
            ....
            ....
            ....
        """.trimIndent()

        val DEFAULT_BLANK_SHADOW = """
            ....
            ...#
            ...#
            .###
        """.trimIndent()
    }

    fun block(w: Int, h: Int, vararg features: Ansi.Feature) {
        block(w, h, DEFAULT_BORDERS_SHADOW, *features)
    }

    /**
     * Draw a button
     * @param w The width of this block
     * @param h The height of this block
     * @param borders If true draw a text border
     * @param features The features used to render the block
     */
    fun block(w: Int, h: Int,
              borders: String ,
              vararg features: Ansi.Feature
    ){

        val walls = borders.split("\n").map { it.toCharArray().map{ c -> c.toString() }.toList() }
        (0 until h).forEach { r ->
            val wall = when(r){
                0       -> walls[0]
                h - 1   -> walls[2]
                else    -> walls[1]
            }
            val line = (wall[0] + wall[1].repeat(w - 2) + wall[2]).replace('.', ' ')
            this + pos(y + r, x)
            this + fmt(*features) + line + fmt()
            if(wall[3] == "#"){
                this + fmt(black.bgBr) + " " + fmt()
            }
        }
        if(walls[3][1] == "#") {
            this + pos(y + h, x + 1)
            this + fmt(black.bgBr) + " ".repeat(w) + fmt()
        }
    }
}

