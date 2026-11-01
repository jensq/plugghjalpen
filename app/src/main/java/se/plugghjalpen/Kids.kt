package se.plugghjalpen

import kotlin.random.Random

/** A picture (emoji) with its Swedish name, read aloud when tapped. */
class Thing(val emoji: String, val name: String)

/** Data and question logic for the 4+ exercises (no Android code, so it is easy to test). */
object Kids {

    // ---------- Räkna ----------

    /** What to count: emoji and its plural ("Hur många äpplen är det?"). */
    val COUNTABLES = listOf(
        "🍎" to "äpplen", "⭐" to "stjärnor", "🐟" to "fiskar", "🎈" to "ballonger",
        "🐞" to "nyckelpigor", "🌸" to "blommor", "🚗" to "bilar", "🐥" to "kycklingar"
    )
    const val COUNT_MAX = 6

    /** Three different numbers 1–[COUNT_MAX], one is [n]; wrong ones are close (one or two off). */
    fun countOptions(n: Int, rnd: Random): List<Int> {
        val near = listOf(n - 1, n + 1, n - 2, n + 2).filter { it in 1..COUNT_MAX }.shuffled(rnd)
        val result = mutableListOf(n)
        for (x in near) if (result.size < 3) result += x
        result.shuffle(rnd)
        return result
    }

    // ---------- Bokstäver ----------

    /**
     * Words whose first sound is the same as the letter's usual sound (no "sk", "sj", "hj",
     * "lj" or soft g/k, which would confuse a beginner).
     */
    val LETTER_WORDS = listOf(
        Thing("🐒", "apa"), Thing("🍍", "ananas"), Thing("🦆", "anka"),
        Thing("🚗", "bil"), Thing("🍌", "banan"), Thing("⚽", "boll"), Thing("⛵", "båt"), Thing("🚌", "buss"),
        Thing("🐬", "delfin"), Thing("🚪", "dörr"),
        Thing("🐘", "elefant"), Thing("🐿️", "ekorre"),
        Thing("🐟", "fisk"), Thing("🦋", "fjäril"),
        Thing("🐷", "gris"), Thing("🍦", "glass"), Thing("🐸", "groda"),
        Thing("🐶", "hund"), Thing("🐴", "häst"), Thing("🏠", "hus"), Thing("🎩", "hatt"),
        Thing("🐱", "katt"), Thing("🐮", "ko"), Thing("🍪", "kaka"), Thing("🐊", "krokodil"),
        Thing("🦁", "lejon"), Thing("💡", "lampa"), Thing("🍃", "löv"),
        Thing("🐭", "mus"), Thing("🌙", "måne"), Thing("🥕", "morot"),
        Thing("🐍", "orm"), Thing("🧀", "ost"),
        Thing("🐧", "pingvin"), Thing("🍕", "pizza"), Thing("🍐", "päron"), Thing("✏️", "penna"),
        Thing("🦊", "räv"), Thing("🌹", "ros"), Thing("🌈", "regnbåge"),
        Thing("☀️", "sol"), Thing("🐌", "snigel"), Thing("⛄", "snögubbe"), Thing("🕷️", "spindel"),
        Thing("🚂", "tåg"), Thing("🍅", "tomat"), Thing("🌳", "träd"), Thing("🎂", "tårta"),
        Thing("🦉", "uggla"),
        Thing("🐺", "varg"), Thing("🍉", "vattenmelon"),
        Thing("🍎", "äpple"), Thing("🥚", "ägg")
    )

    fun letterOf(t: Thing) = t.name.first().uppercaseChar()

    val LETTERS: List<Char> = LETTER_WORDS.map { letterOf(it) }.distinct()

    class LetterTask(val letter: Char, val answer: Thing, val options: List<Thing>)

    /** "Vilken bild börjar på S?": one word with that letter, two with other (different) letters. */
    fun letterTask(rnd: Random, avoid: Char? = null): LetterTask {
        var letter = LETTERS.random(rnd)
        while (letter == avoid && LETTERS.size > 1) letter = LETTERS.random(rnd)
        val answer = LETTER_WORDS.filter { letterOf(it) == letter }.random(rnd)
        val wrong = mutableListOf<Thing>()
        for (t in LETTER_WORDS.shuffled(rnd)) {
            if (wrong.size == 2) break
            val l = letterOf(t)
            if (l != letter && wrong.none { letterOf(it) == l }) wrong += t
        }
        return LetterTask(letter, answer, (wrong + answer).shuffled(rnd))
    }

    // ---------- Vilken passar inte? ----------

    /** [reason] is said after "Rätt, bra jobbat!" when the odd one is found. */
    class Category(val reason: String, val things: List<Thing>)

    val CATEGORIES = listOf(
        Category("Den är inget djur.", listOf(
            Thing("🐶", "hund"), Thing("🐱", "katt"), Thing("🐮", "ko"), Thing("🐷", "gris"), Thing("🐴", "häst"),
            Thing("🐭", "mus"), Thing("🐸", "groda"), Thing("🐰", "kanin"), Thing("🐻", "björn"), Thing("🐵", "apa")
        )),
        Category("Den är ingen frukt.", listOf(
            Thing("🍎", "äpple"), Thing("🍌", "banan"), Thing("🍐", "päron"), Thing("🍊", "apelsin"),
            Thing("🍓", "jordgubbe"), Thing("🍇", "vindruvor"), Thing("🍉", "vattenmelon"), Thing("🍒", "körsbär")
        )),
        Category("Den kan man inte åka i.", listOf(
            Thing("🚗", "bil"), Thing("🚌", "buss"), Thing("🚂", "tåg"), Thing("✈️", "flygplan"),
            Thing("⛵", "båt"), Thing("🚒", "brandbil"), Thing("🚜", "traktor"), Thing("🚑", "ambulans")
        )),
        Category("Den har man inte på sig.", listOf(
            Thing("👕", "tröja"), Thing("👖", "byxor"), Thing("👗", "klänning"), Thing("👟", "sko"),
            Thing("🎩", "hatt"), Thing("👢", "stövel"), Thing("🧣", "halsduk")
        ))
    )

    class OddTask(val group: Category, val odd: Thing, val options: List<Thing>)

    /** Three things from one category and one from another, in random order. */
    fun oddTask(rnd: Random, avoidGroup: Category? = null): OddTask {
        var group = CATEGORIES.random(rnd)
        while (group === avoidGroup) group = CATEGORIES.random(rnd)
        val same = group.things.shuffled(rnd).take(3)
        val odd = CATEGORIES.filter { it !== group }.random(rnd).things.random(rnd)
        return OddTask(group, odd, (same + odd).shuffled(rnd))
    }
}

// ---------- Former ----------

enum class ShapeKind(val label: String, val definite: String) {
    CIRCLE("cirkel", "cirkeln"),
    SQUARE("fyrkant", "fyrkanten"),
    TRIANGLE("triangel", "triangeln"),
    STAR("stjärna", "stjärnan"),
    HEART("hjärta", "hjärtat")
}
