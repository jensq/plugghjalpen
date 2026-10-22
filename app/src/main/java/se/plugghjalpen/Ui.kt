package se.plugghjalpen

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/** Small helpers so the screens can be built in code (same style as R56 video). */
object Ui {
    const val BG = 0xFFFFF6E9.toInt()
    const val CARD = 0xFFFFFFFF.toInt()
    const val TEXT = 0xFF2B2D42.toInt()
    const val TEXT_DIM = 0xFF7A7C8C.toInt()
    const val PRIMARY = 0xFF2F6FDE.toInt()
    const val ACCENT = 0xFFFF9F1C.toInt()
    const val CORRECT = 0xFF2E9E5B.toInt()
    const val CORRECT_BG = 0xFFDDF5E6.toInt()
    const val WRONG = 0xFFE0453A.toInt()
    const val WRONG_BG = 0xFFFDE2E0.toInt()
    const val BORDER = 0xFFE8DCC8.toInt()

    fun dp(ctx: Context, v: Number): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), ctx.resources.displayMetrics).toInt()

    fun rounded(ctx: Context, color: Int, radiusDp: Int, strokeColor: Int = 0, strokeDp: Int = 0): GradientDrawable {
        val g = GradientDrawable()
        g.setColor(color)
        g.cornerRadius = dp(ctx, radiusDp).toFloat()
        if (strokeDp > 0) g.setStroke(dp(ctx, strokeDp), strokeColor)
        return g
    }

    /** A background with a touch ripple on top of [content]. */
    fun pressable(content: Drawable): Drawable =
        RippleDrawable(ColorStateList.valueOf(0x33000000), content, null)

    fun card(ctx: Context, color: Int = CARD, strokeColor: Int = BORDER): Drawable =
        pressable(rounded(ctx, color, 24, strokeColor, 3))

    fun text(ctx: Context, s: String, sp: Float, color: Int = TEXT, bold: Boolean = false): TextView {
        val t = TextView(ctx)
        t.text = s
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        t.setTextColor(color)
        if (bold) t.typeface = Typeface.DEFAULT_BOLD
        return t
    }

    fun icon(ctx: Context, res: Int, sizeDp: Int, tint: Int = Color.WHITE): ImageView {
        val i = ImageView(ctx)
        i.setImageResource(res)
        i.imageTintList = ColorStateList.valueOf(tint)
        i.layoutParams = LinearLayout.LayoutParams(dp(ctx, sizeDp), dp(ctx, sizeDp))
        return i
    }

    /** Round button with only an icon (used for "back"). */
    fun roundIconButton(ctx: Context, res: Int, color: Int, sizeDp: Int, onClick: () -> Unit): FrameLayout {
        val f = FrameLayout(ctx)
        val bg = GradientDrawable()
        bg.shape = GradientDrawable.OVAL
        bg.setColor(color)
        f.background = pressable(bg)
        val i = icon(ctx, res, sizeDp / 2)
        f.addView(i, FrameLayout.LayoutParams(dp(ctx, sizeDp / 2), dp(ctx, sizeDp / 2), Gravity.CENTER))
        f.layoutParams = LinearLayout.LayoutParams(dp(ctx, sizeDp), dp(ctx, sizeDp))
        f.isClickable = true
        f.isFocusable = true
        f.setOnClickListener { onClick() }
        return f
    }

    fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    fun pulse(v: View) {
        v.animate().scaleX(1.12f).scaleY(1.12f).setDuration(180).withEndAction {
            v.animate().scaleX(1f).scaleY(1f).setDuration(180).start()
        }.start()
    }

    fun shake(v: View) {
        val d = dp(v.context, 8).toFloat()
        v.animate().translationX(d).setDuration(60).withEndAction {
            v.animate().translationX(-d).setDuration(90).withEndAction {
                v.animate().translationX(0f).setDuration(60).start()
            }.start()
        }.start()
    }
}
