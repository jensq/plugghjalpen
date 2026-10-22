package se.plugghjalpen

import android.content.Context
import java.time.LocalDate

/** Counts answers that were right on the first try, per day (starts over at midnight). */
object Stats {
    private const val PREFS = "stats"
    private const val DAY = "day"
    private const val COUNT = "count"

    private fun today() = LocalDate.now().toString() // e.g. 2026-10-22, phone's own time zone

    fun todayCount(ctx: Context): Int {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return if (p.getString(DAY, null) == today()) p.getInt(COUNT, 0) else 0
    }

    fun reset(ctx: Context) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(DAY, today()).putInt(COUNT, 0).apply()
    }

    fun addFirstTryCorrect(ctx: Context) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val n = todayCount(ctx) + 1
        p.edit().putString(DAY, today()).putInt(COUNT, n).apply()
    }
}
