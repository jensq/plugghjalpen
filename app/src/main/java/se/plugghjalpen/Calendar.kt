package se.plugghjalpen

import kotlin.random.Random

/** "Vilken dag kommer efter tisdag?" / "Vilken månad kommer före mars?" */
data class CalendarTask(val months: Boolean, val after: Boolean, val base: Int) {
    private val names get() = if (months) CalendarQuiz.MONTHS else CalendarQuiz.DAYS
    private fun at(i: Int) = names[((i % names.size) + names.size) % names.size]

    val answer get() = at(base + if (after) 1 else -1)
    val question get() = "Vilken ${if (months) "månad" else "dag"} kommer ${if (after) "efter" else "före"} ${names[base]}?"

    /** Correct, the other direction (före/efter mix-up), and two steps away. */
    fun options(rnd: Random): List<String> {
        val step = if (after) 1 else -1
        return listOf(answer, at(base - step), at(base + 2 * step)).shuffled(rnd)
    }
}

object CalendarQuiz {
    val DAYS = listOf("måndag", "tisdag", "onsdag", "torsdag", "fredag", "lördag", "söndag")
    val MONTHS = listOf(
        "januari", "februari", "mars", "april", "maj", "juni",
        "juli", "augusti", "september", "oktober", "november", "december"
    )

    fun randomTask(rnd: Random, avoid: CalendarTask? = null): CalendarTask {
        while (true) {
            val months = rnd.nextBoolean()
            val t = CalendarTask(months, rnd.nextBoolean(), rnd.nextInt(if (months) 12 else 7))
            if (t != avoid) return t
        }
    }
}
