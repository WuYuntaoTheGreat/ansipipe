package cn.wuyatang.ansipipe.ui

import cn.wuyatang.ansipipe.Ansi
import cn.wuyatang.ansipipe.Ansi.Color.*
import cn.wuyatang.ansipipe.Ansi.Control.*
import cn.wuyatang.ansipipe.Ansi.Feature.*
import cn.wuyatang.ansipipe.PipeProcessor


/**
 * The Window class.
 * @param inner The rectangle of this window. This will be used as the inner rectangle.
 * @param borderStyle A string to represent the borders, possible values are: [BORDER_ASCII], [BORDER_SLIM],
 * [BORDER_BLANK]. If this string is empty, no border will be present. Default is [BORDER_SLIM]
 *
 * @param shadowStyle A string to represent the shadow, possible values are: [BORDER_ASCII], [BORDER_SLIM],
 * [BORDER_BLANK]. If this string is empty, no shadow will be presented. Default is [BORDER_BLANK].
 *
 * @param bgFormat A [Format] object to be used as the default text style of the background of this window.
 * @param shFormat A [Format] object to be used as the shadow of the window.
 */
open class Window(
    val inner: Rect = Rect(0, 0, 40, 20),
    val borderStyle: String = BORDER_SLIM,
    val shadowStyle: String = BORDER_SLIM,
    val bgFormat: Format? = Format(Bold, White.fgBr, Blue.bgBr),
    val shFormat: Format? = Format(Black.fg, Black.bgBr)
): PipeProcessor, AnsiParagraph() {
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
        // Doing nothing.
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
        // Doing nothing.
        return true
    }

    /**
     * Companion object
     */
    companion object {
        val BORDER_SLIM = """
            ┌─┐
            │ │
            └─┘
        """.trimIndent()

        val BORDER_ASCII = """
            +-+
            | |
            +-+
        """.trimIndent()

        val BORDER_BLANK = """
            ...
            ...
            ...
        """.trimIndent().replace('.', ' ')
    }

    /**
     * Constructor,
     * @param x The x coordinate of the top left corner
     * @param y The y coordinate of the top left corner
     * @param w The (inner) width of this window
     * @param h The (inner) height of this window
     *
     * @param borderStyle A string to represent the borders, possible values are: [BORDER_ASCII], [BORDER_SLIM],
     * [BORDER_NONE]. If this string is empty, no border will be present. Default is [BORDER_SLIM]
     *
     * @param shadowStyle A character (represent as String) to define the shadow. If this string is empty, no border
     * will be presented. Default is ' ' (a space).
     *
     * @param bgFormat A [Format] object to be used as the default text style of the background of this window.
     * @param shFormat A [Format] object to be used as the shadow of the window.
     */
    constructor (
        x: Int, y: Int, w: Int, h: Int,
        borderStyle: String = BORDER_SLIM,
        shadowStyle: String = BORDER_SLIM,
        bgFormat: Format? = Format(Bold, White.fgBr, Blue.bgBr),
        shFormat: Format? = Format(Black.fg, Black.bgBr)
    ): this(
        Rect(x, y, w, h),
        borderStyle,
        shadowStyle,
        bgFormat,
        shFormat)

    /**
     * The outer dimension of this Window.
     *
     * > NOTE: This does NOT include shadows!
     */
    @Suppress("UNUSED_PARAMETER")
    val outer = object: Rect() {
        private val border = (if(borderStyle.isEmpty()) 0 else 1)

        override var x: Int
            get() = inner.x - border
            set(value) {}

        override var y: Int
            get() = inner.y - border
            set(value) {}

        override var w: Int
            get() = inner.w + border * 2
            set(value) {}

        override var h: Int
            get() = inner.h + border * 2
            set(value) {}
    }

    /**
     * Render this window.
     */
    fun render() {
        // Render shadow
        if(shadowStyle.isNotEmpty()){
            val shRect = outer.clone().apply{ offset(1, 1) }
            renderRect(shRect, shadowStyle, shFormat)
        }

        // Render background
        renderRect(outer, borderStyle, bgFormat)

        // Render contents
        val cropRect = inner.clone().apply { set(0, 0) }
        render(inner, bgFormat, cropRect)
    }

    /**
     * Render a rectangle
     * @param rect The outer rectangle to render
     * @param style The background style, possible values are [BORDER_BLANK], [BORDER_SLIM], [BORDER_ASCII] ... etc.
     * @param format The background format
     */
    fun renderRect(rect: Rect, style: String, format: Format?) {
        val walls = style
            .split('\n')
            .map {
                it
                    .toCharArray()
                    .toList()
                    .map { it.toString() }
            }
            .let {
                if(it.size >= 3){
                    it
                } else {
                    listOf(
                        listOf("", " ", ""),
                        listOf("", " ", ""),
                        listOf("", " ", "")
                    )
                }
            }

        val p = AnsiParagraph()
        (0 until rect.h).forEach { r ->
            val wl = when (r) {
                0           -> walls[0]
                rect.h - 1  -> walls[2]
                else        -> walls[1]
            }

            p.newLine() + format + wl[0] + wl[1].repeat(rect.w - 2) + wl[2]
        }

        p.render(rect)
    }
}

