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

/** Colours, shapes and small helpers so the screens can be built in code. */
object Ui {
    // calm, light base with a few clear accent colours
    const val BG = 0xFFF5F6FB.toInt()
    const val BG_TOP = 0xFFE9EEFF.toInt()
    const val CARD = 0xFFFFFFFF.toInt()
    const val TEXT = 0xFF1E2240.toInt()
    const val TEXT_DIM = 0xFF8A8FA8.toInt()
    const val PRIMARY = 0xFF4F6BFF.toInt()
    const val PRIMARY_2 = 0xFF8357F5.toInt()
    const val ACCENT = 0xFFFF8A3D.toInt()
    const val ACCENT_2 = 0xFFFF5E7E.toInt()
    const val CORRECT = 0xFF1FAE7A.toInt()
    const val CORRECT_BG = 0xFFE2F7EE.toInt()
    const val WRONG = 0xFFEF5A5F.toInt()
    const val WRONG_BG = 0xFFFDE9EA.toInt()
    const val BORDER = 0xFFE2E5F0.toInt()
    const val DISABLED = 0xFFE3E5EE.toInt()
    const val DISABLED_TEXT = 0xFFA5A9BD.toInt()

    /** Soft tints for the icon area of each exercise card. */
    val TINTS = intArrayOf(
        0xFFE7ECFF.toInt(), 0xFFFFEEDD.toInt(), 0xFFE1F6EC.toInt(),
        0xFFF1E9FC.toInt(), 0xFFFDE6EF.toInt(), 0xFFFFF5D6.toInt()
    )

    val HEADING: Typeface by lazy { Typeface.create("sans-serif", Typeface.BOLD) }
    val MEDIUM: Typeface by lazy { Typeface.create("sans-serif-medium", Typeface.NORMAL) }

    fun dp(ctx: Context, v: Number): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), ctx.resources.displayMetrics).toInt()

    fun rounded(ctx: Context, color: Int, radiusDp: Int, strokeColor: Int = 0, strokeDp: Int = 0): GradientDrawable {
        val g = GradientDrawable()
        g.setColor(color)
        g.cornerRadius = dp(ctx, radiusDp).toFloat()
        if (strokeDp > 0) g.setStroke(dp(ctx, strokeDp), strokeColor)
        return g
    }

    /** Diagonal colour gradient with rounded corners. */
    fun gradient(ctx: Context, from: Int, to: Int, radiusDp: Int): GradientDrawable {
        val g = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(from, to))
        g.cornerRadius = dp(ctx, radiusDp).toFloat()
        return g
    }

    /** Page background: a light tint at the top fading into the base colour. */
    fun page(): Drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(BG_TOP, BG, BG))

    /** A background with a touch ripple on top of [content]. */
    fun pressable(content: Drawable, ripple: Int = 0x22000000): Drawable =
        RippleDrawable(ColorStateList.valueOf(ripple), content, null)

    /** White (or tinted) card. [strokeColor] ≠ 0 draws a coloured edge (used for right/wrong). */
    fun card(ctx: Context, color: Int = CARD, strokeColor: Int = 0): Drawable =
        pressable(rounded(ctx, color, 26, strokeColor, if (strokeColor != 0) 3 else 0))

    /** Soft drop shadow (Android draws it from the view's rounded background). */
    fun elevate(v: View, dpValue: Int) {
        v.elevation = dp(v.context, dpValue).toFloat()
    }

    fun text(ctx: Context, s: String, sp: Float, color: Int = TEXT, bold: Boolean = false): TextView {
        val t = TextView(ctx)
        t.text = s
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        t.setTextColor(color)
        if (bold) t.typeface = HEADING
        return t
    }

    fun icon(ctx: Context, res: Int, sizeDp: Int, tint: Int = Color.WHITE): ImageView {
        val i = ImageView(ctx)
        i.setImageResource(res)
        i.imageTintList = ColorStateList.valueOf(tint)
        i.layoutParams = LinearLayout.LayoutParams(dp(ctx, sizeDp), dp(ctx, sizeDp))
        return i
    }

    /** White round button with a dark icon and a soft shadow (used for "back"). */
    fun roundIconButton(ctx: Context, res: Int, @Suppress("UNUSED_PARAMETER") color: Int, sizeDp: Int, onClick: () -> Unit): FrameLayout {
        val f = FrameLayout(ctx)
        val bg = GradientDrawable()
        bg.shape = GradientDrawable.OVAL
        bg.setColor(CARD)
        f.background = pressable(bg)
        val i = icon(ctx, res, sizeDp / 2, TEXT)
        f.addView(i, FrameLayout.LayoutParams(dp(ctx, sizeDp / 2), dp(ctx, sizeDp / 2), Gravity.CENTER))
        f.layoutParams = LinearLayout.LayoutParams(dp(ctx, sizeDp), dp(ctx, sizeDp))
        elevate(f, 3)
        f.isClickable = true
        f.isFocusable = true
        f.contentDescription = "Tillbaka"
        f.setOnClickListener { onClick() }
        return f
    }

    /** Emoji (or any text) that grows to fill its box, up to [maxSp]. */
    fun emoji(ctx: Context, s: String, maxSp: Int): TextView {
        val t = TextView(ctx)
        t.text = s
        t.gravity = Gravity.CENTER
        t.maxLines = 1
        t.includeFontPadding = false
        t.setTextColor(TEXT)
        t.setAutoSizeTextTypeUniformWithConfiguration(16, maxSp, 2, TypedValue.COMPLEX_UNIT_SP)
        return t
    }

    /** Answer text inside an option card (words, numbers, amounts). */
    fun optionText(ctx: Context, s: String, minSp: Int, maxSp: Int, color: Int = TEXT): TextView {
        val t = TextView(ctx)
        t.text = s
        t.setTextColor(color)
        t.typeface = HEADING
        t.gravity = Gravity.CENTER
        t.maxLines = 1
        t.setAutoSizeTextTypeUniformWithConfiguration(minSp, maxSp, 1, TypedValue.COMPLEX_UNIT_SP)
        return t
    }

    fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    fun pulse(v: View) {
        v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(160).withEndAction {
            v.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
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

    /** Fade and lift a view in (used when a new question appears). */
    fun appear(v: View, delayMs: Long = 0) {
        v.alpha = 0f
        v.translationY = dp(v.context, 12).toFloat()
        v.animate().alpha(1f).translationY(0f).setStartDelay(delayMs).setDuration(220).start()
    }
}
