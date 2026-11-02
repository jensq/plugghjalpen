package se.plugghjalpen

import android.content.Context
import android.widget.FrameLayout
import kotlin.math.min

/** A FrameLayout that is always square (the smaller of the space it gets), so round backgrounds stay round. */
class SquareFrame(ctx: Context) : FrameLayout(ctx) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val s = if (h == 0) w else if (w == 0) h else min(w, h)
        val spec = MeasureSpec.makeMeasureSpec(s, MeasureSpec.EXACTLY)
        super.onMeasure(spec, spec)
    }
}
