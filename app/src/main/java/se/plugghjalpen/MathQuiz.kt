package se.plugghjalpen

import kotlin.random.Random

enum class MathOp { PLUS, MINUS, HALF }

/** "7 + 5", "12 − 4" (numbers up to 20) or "hälften av 14" (Swedish year 1 level). */
data class MathTask(val a: Int, val b: Int, val op: MathOp) {
    val answer get() = when (op) {
        MathOp.PLUS -> a + b
        MathOp.MINUS -> a - b
        MathOp.HALF -> a / 2
    }

    /** What is written: "7 + 5 = ?" / "Hälften av 14 = ?" */
    val text get() = when (op) {
        MathOp.PLUS -> "$a + $b = ?"
        MathOp.MINUS -> "$a − $b = ?"
        MathOp.HALF -> "Hälften av $a = ?"
    }

    /** What is said: "Hur mycket är sju plus fem?" / "Vad är hälften av fjorton?" */
    val spoken get() = when (op) {
        MathOp.PLUS -> "Hur mycket är ${SwedishNumbers.word(a)} plus ${SwedishNumbers.word(b)}?"
        MathOp.MINUS -> "Hur mycket är ${SwedishNumbers.word(a)} minus ${SwedishNumbers.word(b)}?"
        MathOp.HALF -> "Vad är hälften av ${SwedishNumbers.word(a)}?"
    }
}

object MathQuiz {
    const val MAX = 20

    /** Round numbers that are fun to halve once the small ones are known. */
    private val BIG_HALVES = listOf(30, 40, 50, 60, 80, 100)

    fun randomTask(rnd: Random, avoid: MathTask? = null): MathTask {
        while (true) {
            // about a third each: plus, minus and halves
            val t = when (rnd.nextInt(3)) {
                0 -> {
                    val max = if (rnd.nextBoolean()) 10 else MAX // half stay within 10
                    val a = rnd.nextInt(1, max) // 1 .. max-1
                    MathTask(a, rnd.nextInt(1, max - a + 1), MathOp.PLUS)
                }
                1 -> {
                    val max = if (rnd.nextBoolean()) 10 else MAX
                    val a = rnd.nextInt(2, max + 1)
                    MathTask(a, rnd.nextInt(1, a + 1), MathOp.MINUS)
                }
                else -> {
                    // mostly even numbers 2–20, sometimes a round ten up to 100
                    val n = if (rnd.nextInt(4) == 0) BIG_HALVES.random(rnd) else rnd.nextInt(1, 11) * 2
                    MathTask(n, 0, MathOp.HALF)
                }
            }
            if (t != avoid) return t
        }
    }

    /**
     * Three different answers in random order, one correct. Wrong answers are typical mistakes:
     * one or two off, ten off, using the other operation, or (for halves) the number itself.
     */
    fun options(t: MathTask, rnd: Random): List<Int> {
        val c = t.answer
        val tricky = when (t.op) {
            MathOp.HALF -> {
                val step = if (t.a >= 30) 5 else 1 // 25 or 30 for half of 40, not 19 or 21
                mutableListOf(c + step, c - step, c + 2 * step, c - 2 * step).also { it.shuffle(rnd); it.add(0, t.a) }
            }
            else -> {
                val other = if (t.op == MathOp.PLUS) t.a - t.b else t.a + t.b
                mutableListOf(c + 1, c - 1, c + 2, c - 2, c + 10, c - 10).also { it.shuffle(rnd); it.add(0, kotlin.math.abs(other)) }
            }
        }
        val max = maxOf(30, t.a)
        val result = mutableListOf(c)
        for (x in tricky) if (result.size < 3 && x >= 0 && x <= max && x !in result) result += x
        while (result.size < 3) { // can't really happen, but never loop on a short list
            val x = rnd.nextInt(0, max + 1)
            if (x !in result) result += x
        }
        result.shuffle(rnd)
        return result
    }
}
