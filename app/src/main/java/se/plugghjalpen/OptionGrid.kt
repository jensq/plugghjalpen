package se.plugghjalpen

import android.content.Context
import android.view.View
import android.view.ViewGroup
import kotlin.math.min

/**
 * Lays out its children as equally sized cells, choosing the number of columns that gives the
 * biggest cells for the space available. So three clocks become a column on a phone held upright
 * and a row on a tablet or a phone on its side.
 *
 * Each cell's width/height ratio is kept between [minAspect] and [maxAspect], and its height is
 * at most [maxCellHeightPx].
 */
class OptionGrid(
    ctx: Context,
    private val minAspect: Float,
    private val maxAspect: Float,
    private val gapPx: Int,
    private val maxCellHeightPx: Int = Int.MAX_VALUE
) : ViewGroup(ctx) {
    private var cols = 1
    private var cellW = 0
    private var cellH = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalW = MeasureSpec.getSize(widthMeasureSpec)
        val totalH = MeasureSpec.getSize(heightMeasureSpec)
        val w = totalW - paddingLeft - paddingRight
        val h = totalH - paddingTop - paddingBottom
        val n = childCount
        var bestArea = -1f
        if (n > 0) for (c in 1..n) {
            val r = (n + c - 1) / c
            val availW = (w - (c - 1) * gapPx).toFloat() / c
            val availH = (h - (r - 1) * gapPx).toFloat() / r
            if (availW <= 0f || availH <= 0f) continue
            var cw = availW
            var ch = min(availH, maxCellHeightPx.toFloat())
            if (cw > ch * maxAspect) cw = ch * maxAspect // not too wide
            if (ch > cw / minAspect) ch = cw / minAspect // not too tall
            val area = cw * ch
            if (area > bestArea) {
                bestArea = area
                cols = c
                cellW = cw.toInt()
                cellH = ch.toInt()
            }
        }
        for (i in 0 until n) {
            getChildAt(i).measure(
                MeasureSpec.makeMeasureSpec(cellW, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(cellH, MeasureSpec.EXACTLY)
            )
        }
        setMeasuredDimension(totalW, totalH)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val n = childCount
        if (n == 0) return
        val rows = (n + cols - 1) / cols
        val gridH = rows * cellH + (rows - 1) * gapPx
        val w = r - l - paddingLeft - paddingRight
        val h = b - t - paddingTop - paddingBottom
        var y = paddingTop + (h - gridH) / 2
        for (row in 0 until rows) {
            val inRow = min(cols, n - row * cols) // last row may be shorter: centre it
            val rowW = inRow * cellW + (inRow - 1) * gapPx
            var x = paddingLeft + (w - rowW) / 2
            for (k in 0 until inRow) {
                val v: View = getChildAt(row * cols + k)
                v.layout(x, y, x + cellW, y + cellH)
                x += cellW + gapPx
            }
            y += cellH + gapPx
        }
    }
}
