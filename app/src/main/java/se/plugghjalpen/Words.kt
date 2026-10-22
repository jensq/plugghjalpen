package se.plugghjalpen

import kotlin.random.Random

class Word(val english: String, val swedish: String)

/** Beginner words. Every Swedish translation must be unique, since it is the answer the child picks. */
object Words {
    val ALL = listOf(
        // animals
        Word("dog", "hund"), Word("cat", "katt"), Word("fish", "fisk"), Word("bird", "fågel"),
        Word("horse", "häst"), Word("cow", "ko"), Word("pig", "gris"), Word("mouse", "mus"),
        Word("rabbit", "kanin"), Word("duck", "anka"),
        // colours
        Word("red", "röd"), Word("green", "grön"), Word("blue", "blå"), Word("yellow", "gul"),
        Word("black", "svart"), Word("white", "vit"), Word("pink", "rosa"), Word("brown", "brun"),
        // nature
        Word("sun", "sol"), Word("moon", "måne"), Word("star", "stjärna"), Word("tree", "träd"),
        Word("flower", "blomma"), Word("rain", "regn"), Word("snow", "snö"), Word("sea", "hav"),
        // food
        Word("apple", "äpple"), Word("milk", "mjölk"), Word("water", "vatten"), Word("bread", "bröd"),
        Word("cake", "tårta"), Word("egg", "ägg"), Word("ice cream", "glass"), Word("cheese", "ost"),
        // people
        Word("mum", "mamma"), Word("dad", "pappa"), Word("brother", "bror"), Word("sister", "syster"),
        Word("friend", "vän"), Word("boy", "pojke"), Word("girl", "flicka"), Word("baby", "bebis"),
        // body
        Word("eye", "öga"), Word("nose", "näsa"), Word("mouth", "mun"), Word("head", "huvud"),
        Word("foot", "fot"), Word("ear", "öra"),
        // things
        Word("house", "hus"), Word("car", "bil"), Word("book", "bok"), Word("ball", "boll"),
        Word("chair", "stol"), Word("table", "bord"), Word("door", "dörr"), Word("bed", "säng"),
        Word("bike", "cykel"), Word("boat", "båt"), Word("shoe", "sko"), Word("hat", "hatt"),
        Word("school", "skola"),
        // describing words
        Word("big", "stor"), Word("small", "liten"), Word("hot", "varm"), Word("cold", "kall"),
        Word("happy", "glad"), Word("sad", "ledsen"),
        // numbers
        Word("one", "ett"), Word("two", "två"), Word("three", "tre"), Word("four", "fyra"), Word("five", "fem")
    )
}

/** Walks through all words in random order before any word comes back. */
class WordDeck(private val rnd: Random = Random.Default) {
    private val deck = ArrayDeque<Word>()
    private var last: Word? = null

    fun next(): Word {
        if (deck.isEmpty()) {
            val fresh = Words.ALL.shuffled(rnd).toMutableList()
            // don't start the new round with the word we just had
            if (fresh.first() == last && fresh.size > 1) fresh.add(fresh.removeAt(0))
            deck.addAll(fresh)
        }
        return deck.removeFirst().also { last = it }
    }

    /** Five Swedish alternatives in random order, one of them correct. */
    fun options(word: Word, count: Int = 5): List<Word> =
        (Words.ALL.filter { it !== word }.shuffled(rnd).take(count - 1) + word).shuffled(rnd)
}
