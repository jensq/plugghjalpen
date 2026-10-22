package se.plugghjalpen

import kotlin.random.Random

/** One shape of a flag, in the flag's own units (see [Flag.w], [Flag.h]). */
sealed class Shape(val color: Long) {
    class Rect(val x: Float, val y: Float, val w: Float, val h: Float, color: Long) : Shape(color)
    class Circle(val cx: Float, val cy: Float, val r: Float, color: Long) : Shape(color)
    /** Five-pointed star, one point straight up, [r] = outer radius. */
    class Star(val cx: Float, val cy: Float, val r: Float, color: Long) : Shape(color)
}

/**
 * A national flag. [w]×[h] are its official proportions. [group] puts look-alike flags together
 * so the wrong alternatives are ones that are easy to mix up. [drawable] = drawn from a vector
 * resource instead of shapes (the Union Jack).
 */
class Flag(
    val name: String,
    val genitive: String,
    val group: Int,
    val w: Float,
    val h: Float,
    val shapes: List<Shape>,
    val drawable: Boolean = false
)

object Flags {
    private const val NORDIC = 1
    private const val VERTICAL = 2
    private const val HORIZONTAL = 3
    private const val OTHER = 4

    private fun r(x: Number, y: Number, w: Number, h: Number, c: Long) =
        Shape.Rect(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), c)

    /** Nordic cross: background, then each cross layer as (start x, start y, thickness). */
    private fun nordic(name: String, gen: String, w: Int, h: Int, bg: Long, vararg cross: Pair<Triple<Int, Int, Int>, Long>) =
        Flag(name, gen, NORDIC, w.toFloat(), h.toFloat(),
            listOf(r(0, 0, w, h, bg)) + cross.flatMap { (t, c) -> listOf(r(t.first, 0, t.third, h, c), r(0, t.second, w, t.third, c)) })

    private fun vertical(name: String, gen: String, w: Int, h: Int, vararg colors: Long): Flag {
        val s = w.toFloat() / colors.size
        return Flag(name, gen, VERTICAL, w.toFloat(), h.toFloat(), colors.mapIndexed { i, c -> r(i * s, 0, s, h, c) })
    }

    /** Horizontal stripes; [parts] gives each stripe's relative height (1:2:1 for Spain). */
    private fun horizontal(name: String, gen: String, w: Int, h: Int, colors: List<Long>, parts: List<Int> = colors.map { 1 }): Flag {
        val unit = h.toFloat() / parts.sum()
        var y = 0f
        val shapes = colors.mapIndexed { i, c -> r(0, y, w, parts[i] * unit, c).also { y += parts[i] * unit } }
        return Flag(name, gen, HORIZONTAL, w.toFloat(), h.toFloat(), shapes)
    }

    private const val WHITE = 0xFFFFFFFF

    private fun usa(): Flag {
        val h = 10f
        val w = 19f
        val stripe = h / 13
        val shapes = mutableListOf<Shape>()
        for (i in 0 until 13) shapes += r(0, i * stripe, w, stripe, if (i % 2 == 0) 0xFFB22234 else WHITE)
        shapes += r(0, 0, 0.76f * h, 7 * stripe, 0xFF3C3B6E)
        for (row in 1..9) {
            val cols = if (row % 2 == 1) listOf(1, 3, 5, 7, 9, 11) else listOf(2, 4, 6, 8, 10)
            for (c in cols) shapes += Shape.Star(0.063f * h * c, 0.054f * h * row, 0.0308f * h, WHITE)
        }
        return Flag("USA", "USA:s", OTHER, w, h, shapes)
    }

    private fun greece(): Flag {
        val blue = 0xFF0D5EAF
        val shapes = mutableListOf<Shape>()
        for (i in 0 until 9) shapes += r(0, i * 2, 27, 2, if (i % 2 == 0) blue else WHITE)
        shapes += r(0, 0, 10, 10, blue)
        shapes += r(4, 0, 2, 10, WHITE)
        shapes += r(0, 4, 10, 2, WHITE)
        return Flag("Grekland", "Greklands", OTHER, 27f, 18f, shapes)
    }

    val ALL: List<Flag> = listOf(
        nordic("Sverige", "Sveriges", 16, 10, 0xFF006AA7, Triple(5, 4, 2) to 0xFFFECC02),
        nordic("Norge", "Norges", 22, 16, 0xFFBA0C2F, Triple(6, 6, 4) to WHITE, Triple(7, 7, 2) to 0xFF00205B),
        nordic("Danmark", "Danmarks", 37, 28, 0xFFC8102E, Triple(12, 12, 4) to WHITE),
        nordic("Finland", "Finlands", 18, 11, WHITE, Triple(5, 4, 3) to 0xFF002F6C),
        nordic("Island", "Islands", 25, 18, 0xFF02529C, Triple(7, 7, 4) to WHITE, Triple(8, 8, 2) to 0xFFDC1E35),
        Flag("Storbritannien", "Storbritanniens", OTHER, 2f, 1f, emptyList(), drawable = true),
        horizontal("Tyskland", "Tysklands", 5, 3, listOf(0xFF000000, 0xFFDD0000, 0xFFFFCE00)),
        vertical("Frankrike", "Frankrikes", 3, 2, 0xFF000091, WHITE, 0xFFE1000F),
        vertical("Italien", "Italiens", 3, 2, 0xFF009246, 0xFFF1F2F1, 0xFFCE2B37),
        vertical("Irland", "Irlands", 2, 1, 0xFF169B62, WHITE, 0xFFFF883E),
        vertical("Belgien", "Belgiens", 15, 13, 0xFF000000, 0xFFFDDA24, 0xFFEF3340),
        horizontal("Nederländerna", "Nederländernas", 3, 2, listOf(0xFFAE1C28, WHITE, 0xFF21468B)),
        horizontal("Polen", "Polens", 8, 5, listOf(WHITE, 0xFFDC143C)),
        horizontal("Estland", "Estlands", 33, 21, listOf(0xFF0072CE, 0xFF000000, WHITE)),
        horizontal("Österrike", "Österrikes", 3, 2, listOf(0xFFC8102E, WHITE, 0xFFC8102E)),
        horizontal("Ukraina", "Ukrainas", 3, 2, listOf(0xFF0057B7, 0xFFFFD700)),
        // the plain civil flag, without the coat of arms
        horizontal("Spanien", "Spaniens", 3, 2, listOf(0xFFAA151B, 0xFFF1BF00, 0xFFAA151B), listOf(1, 2, 1)),
        Flag("Japan", "Japans", OTHER, 3f, 2f, listOf(r(0, 0, 3, 2, WHITE), Shape.Circle(1.5f, 1f, 0.6f, 0xFFBC002D))),
        Flag("Schweiz", "Schweiz", OTHER, 32f, 32f, listOf(r(0, 0, 32, 32, 0xFFDA291C), r(6, 13, 20, 6, WHITE), r(13, 6, 6, 20, WHITE))),
        greece(),
        usa()
    )

    /** Three flags, one is [correct]: one look-alike from the same group plus one from anywhere. */
    fun options(correct: Flag, rnd: Random): List<Flag> {
        val result = mutableListOf(correct)
        ALL.filter { it.group == correct.group && it !== correct }.randomOrNull(rnd)?.let { result += it }
        val rest = ALL.filter { it !in result }.shuffled(rnd)
        result += rest.take(3 - result.size)
        result.shuffle(rnd)
        return result
    }
}

/** Walks through all flags in random order before any comes back. */
class FlagDeck(private val rnd: Random = Random.Default) {
    private val deck = ArrayDeque<Flag>()
    private var last: Flag? = null

    fun next(): Flag {
        if (deck.isEmpty()) {
            val fresh = Flags.ALL.shuffled(rnd).toMutableList()
            if (fresh.first() === last && fresh.size > 1) fresh.add(fresh.removeAt(0))
            deck.addAll(fresh)
        }
        return deck.removeFirst().also { last = it }
    }
}
