package cn.wuyatang.ansipipe.ui

import cn.wuyatang.ansipipe.Ansi

/**
 * The point structure.
 * @param x The x coordinate
 * @param y The y coordniate
 */
open class Point(
    open var x: Int = 0,
    open var y: Int = 0) {

    /**
     * Clone a new Point instance.
     */
    open fun clone(): Point = Point(x, y)

    /**
     * Set the Point
     */
    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    /**
     * Copy from another Point instance.
     */
    fun copy(another: Point) {
        x = another.x
        y = another.y
    }

    /**
     * Offset the point with delta (x, y)
     */
    fun offset(dx: Int = 0, dy: Int = 0){
        x += dx
        y += dy
    }

    /**
     * operator +=
     */
    operator fun plusAssign(point: Point) {
        offset(point.x, point.y)
    }

    val control: Ansi.Control get() = Ansi.Control.Pos(y, x)
}

/**
 * The rectangle structure.
 * @param x The x coordniate of the top left corner
 * @param y The y coordniate of the top left corner
 * @param w The width
 * @param h The height
 */
open class Rect(
    x: Int = 0,
    y: Int = 0,
    open var w: Int = 0,
    open var h: Int = 0) : Point(x, y){

    val endX: Int get() = x + w - 1
    val endY: Int get() = y + h - 1

    /**
     * The center of this rectangle
     */
    val center = object: Point(0, 0){
        override var x
            get() = w / 2 + x
            set(_) {}
        override var y
            get() = h / 2 + y
            set(_) {}
    }

    /**
     * Clone a new rectangle instance.
     */
    override fun clone(): Rect = Rect(x, y, w, h)

    /**
     * Copy data from another rectangle.
     */
    fun copy(another: Rect) {
        x = another.x
        y = another.y
        w = another.w
        x = another.h
    }

    /**
     * Set the rectangle.
     */
    fun set(x: Int, y: Int, w: Int, h: Int){
        this.x = x
        this.y = y
        this.w = w
        this.h = h
    }

    /**
     * Scale this rectangle's dimension.
     */
    fun scale(s: Float) {
        w = (w.toFloat() * s).toInt()
        h = (h.toFloat() * s).toInt()
    }

    /**
     * operator *=
     */
    operator fun timesAssign(s: Float) {
        scale(s)
    }

}


