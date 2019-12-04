@file:Suppress("ClassName", "unused", "EnumEntryName")

package cn.wuyatang.ansipipe

/**
 *
 */
interface Ansi {
    companion object {
        const val ESC = '\u001B'

        private fun featuresToString(vararg features: Feature): String =
            features
                .map { it.v }
                .sorted()
                .map { it.toString() }
                .let { if(it.isEmpty()) listOf("0") else it }
                .joinToString(";")
    }

    sealed class Feature(val v: Int) {
        object reset       : Feature(0)
        object bold        : Feature(1)
        object faint       : Feature(2)
        object italic      : Feature(3)
        object underline   : Feature(4)
        object blink       : Feature(5)

        class raw(v: Int): Feature(v)
    }

    enum class Color(private val c: Int){
        black   (0),
        red     (1),
        green   (2),
        brown   (3),
        blue    (4),
        magenta (5),
        cyan    (6),
        white   (7),
        ;
        val fg      = Feature.raw(c + 30)
        val bg      = Feature.raw(c + 40)
        val fgBr    = Feature.raw(c + 90)
        val bgBr    = Feature.raw(c + 100)
    }

    /**
     * The ANSI control
     */
    sealed class Control(val v: String) {
        override fun toString(): String = v

        operator fun plus(next: String): String {
            return v + next
        }

        class up        (n: Int = 1): Control("$ESC[${n}A")
        class down      (n: Int = 1): Control("$ESC[${n}B")
        class right     (n: Int = 1): Control("$ESC[${n}C")
        class left      (n: Int = 1): Control("$ESC[${n}D")
        class lineDown  (n: Int = 1): Control("$ESC[${n}E")
        class lineUp    (n: Int = 1): Control("$ESC[${n}F")
        class col       (n: Int = 1): Control("$ESC[${n}G")
        class pos       (row: Int, col: Int): Control("$ESC[${row};${col}H")

        object clrDown      : Control("$ESC[0J")
        object clrUp        : Control("$ESC[1J")
        object clrAll       : Control("$ESC[2J")
        object clrFull      : Control("$ESC[3J")

        object clrLRight    : Control("$ESC[0K")
        object clrLLeft     : Control("$ESC[1K")
        object clrLine      : Control("$ESC[2K")

        object save         : Control("${ESC}7") // Control("$ESC[s")
        object restore      : Control("${ESC}8") // Control("$ESC[u")

        class fmt(vararg features: Feature): Control("${ESC}[${ featuresToString(*features) }m")
    }

    /**
     * Keys from console
     */
    sealed class Key(val name: String) {
        object Home     : Key("<Home>")
        object Insert   : Key("<Ins>")
        object Delete   : Key("<Del>")
        object End      : Key("<End>")
        object PgUp     : Key("<PgUp>")
        object PgDn     : Key("<PgDn>")

        object F0       : Key("F0")

        object F1       : Key("F1")
        object F2       : Key("F2")
        object F3       : Key("F3")
        object F4       : Key("F4")

        object F5       : Key("F5")
        object F6       : Key("F6")
        object F7       : Key("F7")
        object F8       : Key("F8")
        object F9       : Key("F9")
        object F10      : Key("F10")
        object F12      : Key("F12")

        object Up       : Key("<Up>")
        object Down     : Key("<Down>")
        object Right    : Key("<Right>")
        object Left     : Key("<Left>")

        object Keypad5  : Key("Keypad5")

        object Esc      : Key("<ESC>")
        object Tab      : Key("<TAB>")
        object BS       : Key("<BS>")
        object Return   : Key("<CR>")

        class Normal(val ch: Char) : Key(ch.toString())
        class AltKey(val ch: Char) : Key("Alt-$ch")

        companion object {
            fun parseKey(line: String): Key? {
                Regex("$ESC\\[(.+)").matchEntire(line)
                    ?.let { ret ->
                        val key = ret.groups[1]!!.value
                        when (key) {
                            "1~", "7~", "H" -> return Home
                            "2~"            -> return Insert
                            "3~"            -> return Delete

                            "4~", "8~", "F" -> return End
                            "5~"            -> return PgUp
                            "6~"            -> return PgDn

                            "10~"           -> return F0

                            "11~", "1P"     -> return F1
                            "12~", "1Q"     -> return F2
                            "13~", "1R"     -> return F3
                            "14~", "1S"     -> return F4

                            "15~"           -> return F5
                            "17~"           -> return F6
                            "18~"           -> return F7
                            "19~"           -> return F8
                            "20~"           -> return F9
                            "21~"           -> return F10
                            "24~"           -> return F12

                            "A"             -> return Up
                            "B"             -> return Down
                            "C"             -> return Right
                            "D"             -> return Left
                            else -> {}
                        }
                    }
                Regex("$ESC(..)").matchEntire(line)
                    ?.let { ret ->
                        val key = ret.groups[1]!!.value
                        when (key) {
                            "OP"            -> return F1
                            "OQ"            -> return F2
                            "OR"            -> return F3
                            "OS"            -> return F4
                            else -> {}
                        }
                    }
                Regex("$ESC(.)").matchEntire(line)
                    ?.let { ret ->
                        return AltKey(ret.groups[1]!!.value[0])
                    }

                return when (line) {
                    "\u007F"    -> BS
                    "\u0009"    -> Tab
                    "\n"        -> Return
                    "<CR>"      -> Return
                    "$ESC"      -> Esc

                    else -> Normal(line[0])
                }
            }
        }
    }

//    /**
//     * Format a string with color and features.
//     */
//    fun formatFeatures(origin: String, vararg features: Feature): String {
//        val fstr = features
//            .map {
//                it.v
//            }
//            .sorted()
//            .map {
//                it.toString()
//            }
//            .joinToString(";")
//        return "$ESC[${fstr}m${origin}$ESC[0m"
//    }
//
//    /**
//     *
//     */
//    fun String.fmt(vararg features: Feature): String {
//        return formatFeatures(this, *features)
//    }
}

