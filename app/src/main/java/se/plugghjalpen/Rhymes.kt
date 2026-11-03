package se.plugghjalpen

import kotlin.random.Random

/**
 * Groups of short words that rhyme with each other. Words in different groups must NOT rhyme,
 * since the wrong alternatives are taken from other groups.
 */
object Rhymes {
    val GROUPS: List<List<String>> = listOf(
        listOf("katt", "hatt", "natt", "platt", "skratt"),
        listOf("hus", "mus", "ljus", "grus"),
        listOf("bil", "pil", "fil", "stil"),
        listOf("sol", "stol", "kjol", "fiol"),
        listOf("boll", "troll", "noll"),
        listOf("sko", "ko", "bro", "ro"),
        listOf("fisk", "disk"),
        listOf("hund", "rund", "stund", "grund"),
        listOf("bok", "krok", "klok"),
        listOf("glass", "klass", "pass"),
        listOf("hand", "sand", "land", "strand", "band"),
        listOf("säng", "äng", "gäng"),
        listOf("tåg", "våg", "låg"),
        listOf("frost", "rost", "post", "kost"),
        listOf("ben", "sten", "ren", "gren"),
        listOf("gris", "is", "ris", "pris"),
        listOf("häst", "fest", "gäst", "bäst"),
        listOf("bär", "där", "här"),
        listOf("val", "sal", "tal", "smal"),
        listOf("bord", "ord", "jord", "nord"),
        listOf("dag", "slag", "lag", "drag")
    )

    class Task(val word: String, val answer: String, val options: List<String>)

    /** One word, its rhyme and two words from two other groups, in random order. */
    fun task(rnd: Random, avoid: String? = null): Task {
        while (true) {
            val g = GROUPS.random(rnd)
            val pair = g.shuffled(rnd).take(2)
            if (pair[0] == avoid) continue
            val wrong = GROUPS.filter { it !== g }.shuffled(rnd).take(2).map { it.random(rnd) }
            return Task(pair[0], pair[1], (wrong + pair[1]).shuffled(rnd))
        }
    }
}
