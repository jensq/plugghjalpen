package se.plugghjalpen

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.view.View
import kotlin.math.max

/**
 * Draws Swedish coins and notes (simplified, not copies of the real designs): silver 1 and 2 kr,
 * gold 5 and 10 kr with their real relative sizes, and the purple 20 kr note.
 * Everything is scaled to fit and centred, wrapping onto several rows when needed.
 */
class MoneyView(ctx: Context) : View(ctx) {
    var pieces: List<Int> = emptyList()
        set(value) {
            field = value
            contentDescription = "${value.sum()} kronor"
            invalidate()
        }

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val edge = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private class Spec(val w: Float, val h: Float) // in units of the 2 kr diameter

    private fun spec(v: Int) = when (v) {
        1 -> Spec(0.86f, 0.86f)   // 19.5 mm
        2 -> Spec(1f, 1f)         // 22.75 mm
        5 -> Spec(1.04f, 1.04f)   // 23.75 mm
        10 -> Spec(0.9f, 0.9f)    // 20.5 mm
        else -> Spec(2.3f, 1.2f)  // note
    }

    private class Placed(val v: Int, val x: Float, val y: Float, val w: Float, val h: Float)

    /** Flow layout at unit size [u]; returns null if it does not fit. */
    private fun layout(u: Float, maxW: Float, maxH: Float): List<Placed>? {
        val gap = u * 0.18f
        val rows = mutableListOf<MutableList<Int>>()
        var rowW = 0f
        for (v in pieces) {
            val w = spec(v).w * u
            if (w > maxW) return null
            if (rows.isEmpty() || rowW + gap + w > maxW) {
                rows += mutableListOf(v)
                rowW = w
            } else {
                rows.last() += v
                rowW += gap + w
            }
        }
        val rowHeights = rows.map { r -> r.maxOf { spec(it).h * u } }
        val totalH = rowHeights.sum() + gap * (rows.size - 1)
        if (totalH > maxH) return null
        val out = mutableListOf<Placed>()
        var y = (height - totalH) / 2f
        rows.forEachIndexed { i, r ->
            val w = r.sumOf { (spec(it).w * u).toDouble() }.toFloat() + gap * (r.size - 1)
            var x = (width - w) / 2f
            for (v in r) {
                val s = spec(v)
                out += Placed(v, x, y + (rowHeights[i] - s.h * u) / 2f, s.w * u, s.h * u)
                x += s.w * u + gap
            }
            y += rowHeights[i] + gap
        }
        return out
    }

    override fun onDraw(canvas: Canvas) {
        if (pieces.isEmpty() || width == 0 || height == 0) return
        val maxW = width * 0.96f
        val maxH = height * 0.96f
        var u = max(maxH / 1.2f, 1f)
        var placed: List<Placed>? = null
        while (u > 4f) {
            placed = layout(u, maxW, maxH)
            if (placed != null) break
            u *= 0.94f
        }
        for (p in placed ?: return) {
            if (p.v >= 20) note(canvas, p) else coin(canvas, p)
        }
    }

    private fun coin(c: Canvas, p: Placed) {
        val cx = p.x + p.w / 2f
        val cy = p.y + p.h / 2f
        val r = p.w / 2f
        val gold = p.v == 5 || p.v == 10
        val light = if (gold) 0xFFF6D77E.toInt() else 0xFFEEF0F3.toInt()
        val dark = if (gold) 0xFFC2922A.toInt() else 0xFFA4ABB5.toInt()
        fill.shader = LinearGradient(cx - r, cy - r, cx + r, cy + r, light, dark, Shader.TileMode.CLAMP)
        c.drawCircle(cx, cy, r, fill)
        fill.shader = null
        edge.color = if (gold) 0xFF9C7116.toInt() else 0xFF7E8692.toInt()
        edge.strokeWidth = r * 0.08f
        c.drawCircle(cx, cy, r * 0.96f, edge)
        edge.strokeWidth = r * 0.03f
        c.drawCircle(cx, cy, r * 0.8f, edge)
        label.color = if (gold) 0xFF5E430A.toInt() else 0xFF3F4652.toInt()
        label.textSize = r * (if (p.v >= 10) 0.75f else 0.95f)
        c.drawText(p.v.toString(), cx, cy + r * 0.12f, label)
        label.textSize = r * 0.32f
        c.drawText("kr", cx, cy + r * 0.52f, label)
    }

    private fun note(c: Canvas, p: Placed) {
        val rect = RectF(p.x, p.y, p.x + p.w, p.y + p.h)
        val rad = p.h * 0.08f
        fill.color = 0xFFB89AD6.toInt()
        c.drawRoundRect(rect, rad, rad, fill)
        edge.color = 0xFF6E4A96.toInt()
        edge.strokeWidth = p.h * 0.04f
        val inset = p.h * 0.1f
        c.drawRoundRect(RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset), rad, rad, edge)
        // a simple portrait-like oval, so it reads as a banknote
        fill.color = 0xFFD9C8EA.toInt()
        c.drawOval(RectF(rect.left + p.w * 0.12f, rect.top + p.h * 0.22f, rect.left + p.w * 0.4f, rect.bottom - p.h * 0.22f), fill)
        label.color = 0xFF3D2260.toInt()
        label.textSize = p.h * 0.5f
        c.drawText(p.v.toString(), rect.left + p.w * 0.68f, rect.centerY() + p.h * 0.12f, label)
        label.textSize = p.h * 0.2f
        c.drawText("kronor", rect.left + p.w * 0.68f, rect.centerY() + p.h * 0.36f, label)
    }
}
