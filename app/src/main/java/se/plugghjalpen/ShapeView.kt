package se.plugghjalpen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Draws one filled shape (circle, square, triangle, star or heart) in a colour. */
class ShapeView(ctx: Context) : View(ctx) {
    private var kind = ShapeKind.CIRCLE
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    fun set(kind: ShapeKind, color: Int) {
        this.kind = kind
        paint.color = color
        contentDescription = kind.label
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (width == 0 || height == 0) return
        val s = min(width, height) * 0.8f // the shape fits in an s × s box
        val cx = width / 2f
        val cy = height / 2f
        path.reset()
        when (kind) {
            ShapeKind.CIRCLE -> path.addCircle(cx, cy, s / 2f, Path.Direction.CW)
            ShapeKind.SQUARE -> {
                val h = s * 0.42f
                path.addRect(cx - h, cy - h, cx + h, cy + h, Path.Direction.CW)
            }
            ShapeKind.TRIANGLE -> {
                val h = s * 0.866f // equilateral
                path.moveTo(cx, cy - h / 2f)
                path.lineTo(cx + s / 2f, cy + h / 2f)
                path.lineTo(cx - s / 2f, cy + h / 2f)
                path.close()
            }
            ShapeKind.STAR -> {
                val r = s / 2f
                val c = cy + r * 0.08f // optical centre
                for (i in 0 until 10) {
                    val rad = if (i % 2 == 0) r else r * 0.42f
                    val a = Math.toRadians(i * 36.0)
                    val x = cx + sin(a).toFloat() * rad
                    val y = c - cos(a).toFloat() * rad
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
            }
            ShapeKind.HEART -> {
                val bottom = cy + s * 0.46f
                path.moveTo(cx, bottom)
                path.cubicTo(cx - s * 0.2f, cy + s * 0.28f, cx - s * 0.52f, cy + s * 0.08f, cx - s * 0.5f, cy - s * 0.18f)
                path.cubicTo(cx - s * 0.48f, cy - s * 0.44f, cx - s * 0.12f, cy - s * 0.5f, cx, cy - s * 0.26f)
                path.cubicTo(cx + s * 0.12f, cy - s * 0.5f, cx + s * 0.48f, cy - s * 0.44f, cx + s * 0.5f, cy - s * 0.18f)
                path.cubicTo(cx + s * 0.52f, cy + s * 0.08f, cx + s * 0.2f, cy + s * 0.28f, cx, bottom)
                path.close()
            }
        }
        canvas.drawPath(path, paint)
    }
}
