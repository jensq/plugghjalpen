package se.plugghjalpen

import kotlin.random.Random

/** "7 + 5" or "12 − 4" with numbers up to 20 (Swedish year 1 level). */
data class MathTask(val a: Int, val b: Int, val plus: Boolean) {
    val answer get() = if (plus) a + b else a - b
    /** What is written: "7 + 5 = ?" */
    val text get() = "$a ${if (plus) "+" else "−"} $b = ?"
    /** What is said: "Hur mycket är sju plus fem?" */
    val spoken get() = "Hur mycket är ${SwedishNumbers.word(a)} ${if (plus) "plus" else "minus"} ${SwedishNumbers.word(b)}?"
}

object MathQuiz {
    const val MAX = 20

    fun randomTask(rnd: Random, avoid: MathTask? = null): MathTask {
        while (true) {
            // half of the tasks stay within 10, the rest go up to 20
            val max = if (rnd.nextBoolean()) 10 else MAX
            val t = if (rnd.nextBoolean()) {
                val a = rnd.nextInt(1, max) // 1 .. max-1
                MathTask(a, rnd.nextInt(1, max - a + 1), true)
            } else {
                val a = rnd.nextInt(2, max + 1)
                MathTask(a, rnd.nextInt(1, a + 1), false)
            }
            if (t != avoid) return t
        }
    }

    /**
     * Three different answers in random order, one correct. Wrong answers are typical mistakes:
     * one or two off, ten off, or using the other operation.
     */
    fun options(t: MathTask, rnd: Random): List<Int> {
        val c = t.answer
        val other = if (t.plus) t.a - t.b else t.a + t.b
        val tricky = mutableListOf(c + 1, c - 1, c + 2, c - 2, c + 10, c - 10)
        tricky.shuffle(rnd)
        tricky.add(0, kotlin.math.abs(other)) // most typical mistake first
        val result = mutableListOf(c)
        for (x in tricky) if (result.size < 3 && x >= 0 && x <= 30 && x !in result) result += x
        while (result.size < 3) { // can't really happen, but never loop on a short list
            val x = rnd.nextInt(0, 31)
            if (x !in result) result += x
        }
        result.shuffle(rnd)
        return result
    }
}
