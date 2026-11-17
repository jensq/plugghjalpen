package se.plugghjalpen

import kotlin.random.Random

/** The Swedish alphabet (capital letters) and how each letter's name is said. */
object Alphabet {
    /**
     * Letter → its name written so the voice says it right ("M" alone can be read oddly,
     * "em" is always said as the letter name).
     */
    val NAMES: List<Pair<Char, String>> = listOf(
        'A' to "a", 'B' to "be", 'C' to "se", 'D' to "de", 'E' to "e", 'F' to "eff",
        'G' to "ge", 'H' to "hå", 'I' to "i", 'J' to "ji", 'K' to "kå", 'L' to "ell",
        'M' to "em", 'N' to "enn", 'O' to "o", 'P' to "pe", 'Q' to "ku", 'R' to "ärr",
        'S' to "ess", 'T' to "te", 'U' to "u", 'V' to "ve", 'W' to "dubbel-ve", 'X' to "eks",
        'Y' to "y", 'Z' to "säta", 'Å' to "å", 'Ä' to "ä", 'Ö' to "ö"
    )

    val LETTERS: List<Char> = NAMES.map { it.first }
    private val nameOf = NAMES.toMap()

    fun spoken(c: Char): String = nameOf[c] ?: c.toString()

    /** Letters that look alike; sometimes one of them is a wrong alternative. */
    private val LOOK_ALIKE = listOf(
        "EF", "MNW", "OQC", "CG", "PRB", "AÅÄ", "OÖ", "UV", "ILT", "KX", "VY", "SZ"
    )

    /** Three different letters, one is [c]. Half of the time one wrong letter looks like [c]. */
    fun options(c: Char, rnd: Random): List<Char> {
        val result = mutableListOf(c)
        if (rnd.nextBoolean()) {
            val similar = LOOK_ALIKE.filter { c in it }.flatMap { it.toList() }.filter { it != c }.distinct()
            similar.randomOrNull(rnd)?.let { result += it }
        }
        for (x in LETTERS.shuffled(rnd)) if (result.size < 3 && x !in result) result += x
        result.shuffle(rnd)
        return result
    }
}

/** Walks through all letters in random order before any letter comes back. */
class LetterDeck(private val rnd: Random = Random.Default) {
    private val deck = ArrayDeque<Char>()
    private var last: Char? = null

    fun next(): Char {
        if (deck.isEmpty()) {
            val fresh = Alphabet.LETTERS.shuffled(rnd).toMutableList()
            if (fresh.first() == last && fresh.size > 1) fresh.add(fresh.removeAt(0))
            deck.addAll(fresh)
        }
        return deck.removeFirst().also { last = it }
    }
}
