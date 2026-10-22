package se.plugghjalpen

import kotlin.random.Random

/** A time on an analog clock: hour 1–12, minute 0–55 in steps of five. */
data class ClockTime(val hour: Int, val minute: Int)

/** Swedish clock phrases and question generation (no Android code, so it is easy to test). */
object ClockQuiz {
    private val HOUR_WORDS = listOf("ett", "två", "tre", "fyra", "fem", "sex", "sju", "åtta", "nio", "tio", "elva", "tolv")

    fun hourWord(h: Int) = HOUR_WORDS[h - 1]
    private fun nextHour(h: Int) = if (h == 12) 1 else h + 1
    private fun prevHour(h: Int) = if (h == 1) 12 else h - 1

    /** "fem över sju", "kvart i fyra", "fem i halv åtta", "klockan tre" … */
    fun phrase(t: ClockTime): String {
        val h = hourWord(t.hour)
        val n = hourWord(nextHour(t.hour))
        return when (t.minute) {
            0 -> "klockan $h"
            5 -> "fem över $h"
            10 -> "tio över $h"
            15 -> "kvart över $h"
            20 -> "tjugo över $h"
            25 -> "fem i halv $n"
            30 -> "halv $n"
            35 -> "fem över halv $n"
            40 -> "tjugo i $n"
            45 -> "kvart i $n"
            50 -> "tio i $n"
            55 -> "fem i $n"
            else -> throw IllegalArgumentException("minute ${t.minute}")
        }
    }

    fun randomTime(rnd: Random, avoid: ClockTime? = null): ClockTime {
        while (true) {
            val t = ClockTime(rnd.nextInt(1, 13), rnd.nextInt(0, 12) * 5)
            if (t != avoid) return t
        }
    }

    /**
     * Three different times in random order, one of them [correct]. The wrong ones are picked from
     * typical mix-ups: one hour off (halv åtta / halv sju), "över" vs "i" (fem över / fem i)
     * and swapped hands — topped up with random times if needed.
     */
    fun options(correct: ClockTime, rnd: Random): List<ClockTime> {
        val h = correct.hour
        val m = correct.minute
        val tricky = mutableListOf(
            ClockTime(prevHour(h), m),
            ClockTime(nextHour(h), m),
            ClockTime(if (m == 0) 12 else m / 5, (h % 12) * 5)
        )
        if (m != 0 && m != 30) tricky += ClockTime(h, 60 - m)
        tricky.shuffle(rnd)
        val result = mutableListOf(correct)
        for (t in tricky) if (result.size < 3 && t !in result) result += t
        while (result.size < 3) {
            val t = randomTime(rnd)
            if (t !in result) result += t
        }
        result.shuffle(rnd)
        return result
    }
}
