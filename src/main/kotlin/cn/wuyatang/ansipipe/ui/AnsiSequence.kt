package cn.wuyatang.ansipipe.ui

import cn.wuyatang.ansipipe.Ansi.Control.*
import java.lang.Integer.max
import java.lang.Integer.min
import java.lang.StringBuilder

/**
 * Sequence of formatted strings.
 */
class AnsiSequence() {

    /**
     * A section of formatted string.
     */
    data class FormattedString(
        val format: Format?,
        val string: String)

    private var currentFormat: Format? = null
    private val lines = mutableListOf<MutableList<FormattedString>>()


    /**
     * Max width, in characters, of this sequence.
     */
    val width: Int get() = lines.map { l -> l.sumBy { it.string.length } }.max() ?: 0

    /**
     * Max height (rows), in characters, of this sequence.
     */
    val height: Int get() = lines.size

    /**
     * Append a string
     */
    fun append(string: String): AnsiSequence {
        string.split('\n').forEachIndexed { r, str ->
            val line = if(r > 0 || lines.size == 0){
                mutableListOf<FormattedString>().apply {
                    lines.add(this)
                }
            } else {
                lines.last()
            }

            if(line.lastOrNull()?.format?.equals(currentFormat) == true) {
                line[line.size - 1] = FormattedString(currentFormat, line.last().string + str)
            } else {
                line.add(FormattedString(currentFormat, str))
            }
        }
        return this
    }


    /**
     * Append a format.
     */
    fun append(fmt: Format?): AnsiSequence {
        if(fmt != null) {
            currentFormat = if (fmt == Format.reset) {
                null
            } else {
                currentFormat?.merge(fmt) ?: fmt
            }
        }
        return this
    }

    /*
     * Operator: +
     */
    operator fun plus(str: String ): AnsiSequence = append(str)
    operator fun plus(fmt: Format?): AnsiSequence = append(fmt)

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

            val cMin = cropRect?.x ?: 0
            val cMax = cropRect?.endX ?: line.sumBy { it.string.length }

            var c = 0
            line.forEach { section ->
                if(c >= cMax){
                    return@forEach
                }
                if((c + section.string.length) > cMin){
                    (section.format?.extend(defaultFormat) ?: Format.reset).let{ print(it.v) }
                    print(section.string.substring(max(cMin - c, 0), min(cMax - c + 1, section.string.length)))
                }

                c += section.string.length
            }

            Format.reset.render()
            at.offset(dy = 1)
        }
    }
}


