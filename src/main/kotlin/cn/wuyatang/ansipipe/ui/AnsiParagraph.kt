package cn.wuyatang.ansipipe.ui

import cn.wuyatang.ansipipe.Ansi.Control.*
import java.lang.Integer.max
import java.lang.Integer.min


/**
 * A section of formatted string.
 */
data class FormattedString(
    val format: Format?,
    val string: String)

/**
 * A line of ANSI chars.
 */
class AnsiLine {
    private val line = mutableListOf<FormattedString>()
    private var currentFormat: Format? = null

    val length: Int get() = line.sumBy { it.string.length }

    /**
     * Append a string to this line
     *
     * > Note: if there is a line end char ( <CR> ), it will throw an exception.
     */
    fun append(str: String): AnsiLine {
        if(line.lastOrNull()?.format?.equals(currentFormat) == true) {
            line[line.size - 1] = FormattedString(currentFormat, line.last().string + str)
        } else {
            line.add(FormattedString(currentFormat, str))
        }
        return this
    }

    /**
     * Append a format.
     */
    fun append(fmt: Format?): AnsiLine {
        when {
            fmt == null     -> {} // If append null, nothing will be changed
            fmt.isReset     -> currentFormat = null
            else            -> currentFormat = fmt
        }
        return this
    }

    /*
     * Operator: +
     */
    operator fun plus(str: String ): AnsiLine = append(str)
    operator fun plus(fmt: Format?): AnsiLine = append(fmt)

    /**
     * Render a portion of this line.
     * @param start Start index of portion to render, (inclusive)
     * @param end End index of portion to render, (exclusive)
     *
     * @param defaultFormat If format is not specified, partially not specified, this default format will be used.
     */
    fun render(start: Int? = null,
               end  : Int? = null,
               defaultFormat: Format? = null){
        var c = 0
        val iStart = start ?: 0
        val iEnd = end ?: length
        line.forEach { portion ->
            if(c >= iEnd){
                return@forEach
            }
            if((c + portion.string.length) > iStart){
                (portion.format ?: Format.reset).extend(defaultFormat).render()
                print(portion.string.substring(
                    max(iStart - c, 0),
                    min(iEnd - c + 1, portion.string.length)))
            }
            c += portion.string.length
        }
        println()

        Format.reset.render()
    }
}

/**
 * A paragraph with ANSI chars.
 */
open class AnsiParagraph {

    private val lines = mutableListOf<AnsiLine>()


    /**
     * Max width, in characters, of this sequence.
     */
    val width: Int get() = lines.map { l -> l.length }.max() ?: 0

    /**
     * Max height (rows), in characters, of this sequence.
     */
    val height: Int get() = lines.size

    /**
     * Append a (multiple line) string to this paragraph.
     */
    fun addParagraph(string: String): AnsiParagraph {
        string.split('\n').forEachIndexed { r, str ->
            val line = if(r > 0 || lines.size == 0){
                AnsiLine().apply {
                    lines.add(this)
                }
            } else {
                lines.last()
            }
        }
        return this
    }

    /**
     * Append and return a new ANSI line.
     */
    fun newLine(): AnsiLine = AnsiLine().apply {
        lines.add(this)
    }

    fun nl(): AnsiLine = newLine()

    /**
     * Clear this sequence, making it empty.
     */
    fun clear() = lines.clear()

    /**
     * Render this sequence to screen.
     * @param startPoint The start point where to render this sequence.
     * @param defaultFormat If format is not specified, partially not specified, this default format will be used.
     * @param cropRect If set, the text will be cropped, using coordinates of this rectangle, in characters, starting
     * from 0 (zero). a cropRect with value (x: 1, y: 2, w: 5, h: 3) means from row 1 (the second row), to row 3 (the
     * fourth row), from column 2 (the third column) to column 5 (the sixth column) of the content of this sequence will
     * be used.
     *
     */
    fun render(startPoint: Point,
               defaultFormat: Format? = null,
               cropRect: Rect? = null ) {
        val at = startPoint.clone()
        val rMin = cropRect?.y ?: 0
        val rMax = cropRect?.endY ?: height

        for(r in rMin..rMax){
            at.control.render()
            val line = lines.getOrNull(r) ?: continue

            line.render(cropRect?.x, cropRect?.endX, defaultFormat)
            //
            at.offset(dy = 1)
        }
    }
}


