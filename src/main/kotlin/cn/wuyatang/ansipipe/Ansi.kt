@file:Suppress("unused", "CanBeParameter")

package cn.wuyatang.ansipipe

/**
 *
 */
interface Ansi {
    companion object {
        const val ESC = '\u001B'
//        const val ESC = 'E'
    }

    /*
     * Output
     */
    enum class Color(private val c: Int){
        Black   (0),
        Red     (1),
        Green   (2),
        Brown   (3),
        Blue    (4),
        Magenta (5),
        Cyan    (6),
        White   (7),
        ;
        val fg      = Feature.Raw(c + 30)
        val bg      = Feature.Raw(c + 40)
        val fgBr    = Feature.Raw(c + 90)
        val bgBr    = Feature.Raw(c + 100)
    }

    sealed class Feature(val v: Int) {
        object Reset       : Feature(0)
        object Bold        : Feature(1)
        object Faint       : Feature(2)
        object Italic      : Feature(3)
        object Underline   : Feature(4)
        object Blink       : Feature(5)

        class Raw(v: Int): Feature(v)
    }

    /**
     * The ANSI control
     */
    sealed class Control(open val v: String): Comparable<Control> {

        override fun compareTo(other: Control): Int = v.compareTo(other.v)

        override fun toString(): String = v

        fun render() = print(v)

        class Up        (n: Int = 1): Control("$ESC[${n}A")
        class Down      (n: Int = 1): Control("$ESC[${n}B")
        class Right     (n: Int = 1): Control("$ESC[${n}C")
        class Left      (n: Int = 1): Control("$ESC[${n}D")
        class LineDown  (n: Int = 1): Control("$ESC[${n}E")
        class LineUp    (n: Int = 1): Control("$ESC[${n}F")
        class Col       (n: Int = 1): Control("$ESC[${n}G")
        class Pos       (row: Int, col: Int): Control("$ESC[${row};${col}H")

        object ClearDown    : Control("$ESC[0J")
        object ClearUp      : Control("$ESC[1J")
        object ClearAll     : Control("$ESC[2J")
        object ClearFull    : Control("$ESC[3J")

        object ClearLRight  : Control("$ESC[0K")
        object ClearLLeft   : Control("$ESC[1K")
        object ClearLine    : Control("$ESC[2K")

        object Save         : Control("${ESC}7") // Control("$ESC[s")
        object Restore      : Control("${ESC}8") // Control("$ESC[u")

        /**
         * The character color and style control.
         * @param st The style feature.
         * @param fg The foreground feature.
         * @param bg The background feature.
         */
        class Format(val st: Feature?,
                     val fg: Feature?,
                     val bg: Feature?): Control(
            listOfNotNull(st, fg, bg)
                .map { it.v }
                .joinToString(";")
                .let { if (it.isEmpty()) "0" else it }
                .let { "$ESC[${it}m"} ) {

            companion object {
                val reset = Format()
            }

            /**
             * True if this is a format reset.
             */
            val isReset: Boolean get() = st == Feature.Reset

            /*
             * Private constructor to server vararg constructor.
             * @param salt to avoid constructor signature duplication.
             */
            private constructor(salt: Int, tuple: Array<Feature?>): this(tuple[0], tuple[1], tuple[2])

            /**
             *
             * @param features variable length parameter, each represent a color or style. If [Feature.Reset] is
             * present as parameter, other colors or styles will be ignored, and this control will become Reset control
             */
            constructor(vararg features: Feature): this( 1,
                features.let { vag ->
                    if(vag.isEmpty() || vag.contains(Feature.Reset)){
                        return@let arrayOf<Feature?>(Feature.Reset, null, null)
                    }
                    val ret = arrayOf<Feature?>(null, null, null)
                    vag.forEach {
                        when (it.v) {
                            in 1..10                -> ret[0] = it
                            in 30..39, in 90..99    -> ret[1] = it
                            in 40..49, in 100..109  -> ret[2] = it
                        }
                    }
                    return@let ret
                }
            )

            fun clone(): Format = Format(
                st = this.st,
                fg = this.fg,
                bg = this.bg )

            fun merge(next: Format): Format = Format(
                st = next.st ?: this.st,
                fg = next.fg ?: this.fg,
                bg = next.bg ?: this.bg )

            fun extend(origin: Format?): Format {
                return origin?.merge(this) ?: this
            }


        }
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
            fun parse(line: String): Key? {
                Regex("$ESC\\[(.+)").matchEntire(line)?.let { ret ->
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
                Regex("$ESC(..)").matchEntire(line)?.let { ret ->
                    val key = ret.groups[1]!!.value
                    when (key) {
                        "OP"            -> return F1
                        "OQ"            -> return F2
                        "OR"            -> return F3
                        "OS"            -> return F4
                        else -> {}
                    }
                }
                Regex("$ESC(.)").matchEntire(line)?.let { ret ->
                    return AltKey(ret.groups[1]!!.value[0])
                }

                return when (line) {
                    "\u007F"    -> BS
                    "\u0009"    -> Tab
                    "\n"        -> Return
                    "<CR>"      -> Return
                    "$ESC"      -> Esc
                    "<SP>"      -> Normal(' ')
                    "<TAB>"     -> Normal('\t')
                    else -> Normal(line[0])
                }
            }
        }
    }
}

