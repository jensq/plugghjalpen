package se.plugghjalpen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import kotlin.math.min

/** Draws [count] copies of an emoji in neat rows (like dots on a dice), scaled to fit. */
class CountView(ctx: Context) : View(ctx) {
    private var emoji = "🍎"
    private var count = 1
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }

    fun set(emoji: String, count: Int) {
        this.emoji = emoji
        this.count = count
        contentDescription = "$count"
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (width == 0 || height == 0 || count <= 0) return
        val cols = when {
            count <= 3 -> count
            count == 4 -> 2
            else -> 3
        }
        val rows = (count + cols - 1) / cols
        val cell = min(width.toFloat() / cols, height.toFloat() / rows)
        paint.textSize = cell * 0.75f
        val yOff = -(paint.descent() + paint.ascent()) / 2f
        val top = (height - rows * cell) / 2f
        var i = 0
        for (r in 0 until rows) {
            val inRow = min(cols, count - r * cols) // last row centred
            val left = (width - inRow * cell) / 2f
            for (c in 0 until inRow) {
                canvas.drawText(emoji, left + (c + 0.5f) * cell, top + (r + 0.5f) * cell + yOff, paint)
                i++
            }
        }
    }
}
