package se.plugghjalpen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Draws a [Flag] in its real proportions, centred, with a thin grey edge (white flags on white cards). */
class FlagView(ctx: Context) : View(ctx) {
    var flag: Flag? = null
        set(value) {
            field = value
            invalidate()
        }

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val edge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0x33000000
    }
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        val f = flag ?: return
        if (width == 0 || height == 0) return
        val s = min(width * 0.94f / f.w, height * 0.94f / f.h)
        val fw = f.w * s
        val fh = f.h * s
        val left = (width - fw) / 2f
        val top = (height - fh) / 2f

        if (f.drawable) {
            val d = context.getDrawable(R.drawable.ic_flag_uk) ?: return
            d.setBounds(left.toInt(), top.toInt(), (left + fw).toInt(), (top + fh).toInt())
            d.draw(canvas)
        } else {
            canvas.save()
            canvas.clipRect(left, top, left + fw, top + fh)
            for (sh in f.shapes) {
                fill.color = sh.color.toInt()
                when (sh) {
                    is Shape.Rect -> canvas.drawRect(left + sh.x * s, top + sh.y * s, left + (sh.x + sh.w) * s, top + (sh.y + sh.h) * s, fill)
                    is Shape.Circle -> canvas.drawCircle(left + sh.cx * s, top + sh.cy * s, sh.r * s, fill)
                    is Shape.Star -> star(canvas, left + sh.cx * s, top + sh.cy * s, sh.r * s)
                }
            }
            canvas.restore()
        }
        edge.strokeWidth = Ui.dp(context, 1).toFloat()
        canvas.drawRect(left, top, left + fw, top + fh, edge)
    }

    private fun star(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        path.reset()
        val inner = r * 0.382f
        for (i in 0 until 10) {
            val rad = if (i % 2 == 0) r else inner
            val a = Math.toRadians(i * 36.0)
            val x = cx + sin(a).toFloat() * rad
            val y = cy - cos(a).toFloat() * rad
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, fill)
    }
}
