package se.plugghjalpen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** An analog clock face: short dark hour hand, long blue minute hand. */
class ClockView(ctx: Context) : View(ctx) {
    var time = ClockTime(12, 0)
        set(value) {
            field = value
            contentDescription = ClockQuiz.phrase(value)
            invalidate()
        }

    private val face = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Ui.CARD; style = Paint.Style.FILL }
    private val rim = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Ui.ACCENT; style = Paint.Style.STROKE }
    private val tick = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeCap = Paint.Cap.ROUND }
    private val numbers = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Ui.TEXT
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val hourHand = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Ui.TEXT; strokeCap = Paint.Cap.ROUND }
    private val minuteHand = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Ui.PRIMARY; strokeCap = Paint.Cap.ROUND }
    private val hub = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Ui.ACCENT }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val outer = min(width, height) / 2f
        if (outer <= 0f) return
        val rimW = outer * 0.07f
        val r = outer - rimW / 2f

        canvas.drawCircle(cx, cy, r, face)
        rim.strokeWidth = rimW
        canvas.drawCircle(cx, cy, r, rim)

        // minute ticks, longer and darker at the hours
        val inner = r - rimW / 2f
        for (i in 0 until 60) {
            val a = Math.toRadians(i * 6.0)
            val isHour = i % 5 == 0
            tick.color = if (isHour) Ui.TEXT else Ui.BORDER
            tick.strokeWidth = if (isHour) outer * 0.03f else outer * 0.015f
            val len = if (isHour) outer * 0.1f else outer * 0.05f
            val s = sin(a).toFloat()
            val c = cos(a).toFloat()
            val r1 = inner - outer * 0.03f
            val r2 = r1 - len
            canvas.drawLine(cx + s * r1, cy - c * r1, cx + s * r2, cy - c * r2, tick)
        }

        numbers.textSize = outer * 0.21f
        val numR = inner - outer * 0.27f
        val textOffset = (numbers.descent() + numbers.ascent()) / 2f
        for (n in 1..12) {
            val a = Math.toRadians(n * 30.0)
            canvas.drawText(n.toString(), cx + sin(a).toFloat() * numR, cy - cos(a).toFloat() * numR - textOffset, numbers)
        }

        // the hour hand moves gradually between the numbers, like on a real clock
        val hourDeg = ((time.hour % 12) + time.minute / 60.0) * 30.0
        val minuteDeg = time.minute * 6.0
        hourHand.strokeWidth = outer * 0.085f
        minuteHand.strokeWidth = outer * 0.055f
        hand(canvas, cx, cy, hourDeg, outer * 0.45f, hourHand)
        hand(canvas, cx, cy, minuteDeg, outer * 0.76f, minuteHand)
        canvas.drawCircle(cx, cy, outer * 0.07f, hub)
    }

    private fun hand(canvas: Canvas, cx: Float, cy: Float, deg: Double, len: Float, p: Paint) {
        val a = Math.toRadians(deg)
        canvas.drawLine(cx, cy, cx + sin(a).toFloat() * len, cy - cos(a).toFloat() * len, p)
    }
}
