package se.plugghjalpen

import kotlin.random.Random

/** Swedish coins (1, 2, 5, 10 kr) and the 20 kr note, kept to totals a seven-year-old can count. */
object MoneyQuiz {
    val DENOMINATIONS = listOf(1, 2, 5, 10, 20)
    const val MAX_TOTAL = 50

    /** A handful of 2–4 coins/notes, largest first, total at most [MAX_TOTAL]. */
    fun randomSet(rnd: Random): List<Int> {
        while (true) {
            val n = rnd.nextInt(2, 5)
            val set = List(n) { DENOMINATIONS[rnd.nextInt(DENOMINATIONS.size)] }.sortedDescending()
            if (set.sum() <= MAX_TOTAL) return set
        }
    }

    /** A random set whose total is exactly [total] (2–4 pieces when possible). */
    fun setWithTotal(total: Int, rnd: Random): List<Int> {
        repeat(500) {
            val s = randomSet(rnd)
            if (s.sum() == total) return s
        }
        // fallback: greedy change
        val out = mutableListOf<Int>()
        var left = total
        for (d in DENOMINATIONS.reversed()) while (left >= d) { out += d; left -= d }
        return out
    }

    /** Three different totals for "Hur mycket är det?", one correct. */
    fun totalOptions(set: List<Int>, rnd: Random): List<Int> {
        val c = set.sum()
        val tricky = mutableListOf(c + 1, c - 1, c + 2, c - 2, c + 5, c - 5, c + 10, c - 10)
        tricky.shuffle(rnd)
        tricky.add(0, set.size) // counting the pieces instead of the money
        val result = mutableListOf(c)
        for (x in tricky) if (result.size < 3 && x > 0 && x != c && x !in result) result += x
        result.shuffle(rnd)
        return result
    }

    /** Three sets for "Välj X kr": one with the right total, two with nearby wrong totals. */
    fun setOptions(total: Int, rnd: Random): List<List<Int>> {
        val right = setWithTotal(total, rnd)
        val wrongTotals = listOf(total + 1, total - 1, total + 2, total - 2, total + 5, total - 5, total + 10, total - 10)
            .filter { it in 2..MAX_TOTAL }
            .shuffled(rnd)
            .take(2)
        val result = mutableListOf(right)
        for (w in wrongTotals) result += setWithTotal(w, rnd)
        result.shuffle(rnd)
        return result
    }
}
