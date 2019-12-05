package cn.wuyatang.ansipipe

import cn.wuyatang.ansipipe.Ansi.Feature
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

    fun d(n: Int = 1) : PipePen = offset(offY =  n)
    fun u(n: Int = 1) : PipePen = offset(offY = -n)
    fun l(n: Int = 1) : PipePen = offset(offX = -n)
    fun r(n: Int = 1) : PipePen = offset(offX =  n)

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

    operator fun plus (s: String): PipePen {
        print(s)
        return this
    }

    operator fun plus (c: Control): PipePen {
        print(c.v)
        return this
    }

    companion object {
        val BLOCK_BORDERS_SHADOW = """
            +-+.
            |.|#
            +-+#
            .###
        """.trimIndent().replace('.', ' ')

        val BLOCK_BLANK = """
            ....
            ....
            ....
            ....
        """.trimIndent().replace('.', ' ')

        val BLOCK_BLANK_SHADOW = """
            ....
            ...#
            ...#
            .###
        """.trimIndent().replace('.', ' ')
    }

    /*
     * Convenient function.
     */
    fun block(w: Int, h: Int, vararg features: Feature): PipePen = block(w, h, fea(*features), BLOCK_BORDERS_SHADOW)
    fun block(w: Int, h: Int, border: String, vararg features: Feature) = block(w, h, fea(*features), border)

    /**
     * Draw a button
     * @param w The width of this block
     * @param h The height of this block
     * @param borders If true draw a text border
     * @param feature The features used to render the block
     * @return this pen
     */
    fun block(w: Int,
              h: Int,
              feature: fea,
              borders: String = BLOCK_BORDERS_SHADOW ): PipePen {
        val walls = borders.split("\n").map { it.toCharArray().map{ c -> c.toString() }.toList() }
        (0 until h).forEach { r ->
            val wl = when(r){
                0       -> walls[0]
                h - 1   -> walls[2]
                else    -> walls[1]
            }
            val line = (wl[0] + wl[1].repeat(w - 2) + wl[2])
            this + pos(y + r, x)
            this + feature + line + fea()
            if(wl[3] == "#"){
                this + fea(black.bgBr) + " " + fea()
            }
        }
        val wl = walls[3]
        if(wl.contains("#")){
            this + pos(y + h, x + 1)
            this + fea(black.bgBr) + " ".repeat(w) + fea()
        }
        return this
    }
}

