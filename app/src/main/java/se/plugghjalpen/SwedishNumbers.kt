package se.plugghjalpen

/** Numbers as Swedish words, so the voice always says them the same way. */
object SwedishNumbers {
    private val SMALL = listOf(
        "noll", "ett", "två", "tre", "fyra", "fem", "sex", "sju", "åtta", "nio", "tio",
        "elva", "tolv", "tretton", "fjorton", "femton", "sexton", "sjutton", "arton", "nitton"
    )
    private val TENS = listOf("", "", "tjugo", "trettio", "fyrtio", "femtio", "sextio", "sjuttio", "åttio", "nittio")

    /** 0–100. 1 is "ett" (counting); use [kronor] for money. */
    fun word(n: Int): String = when {
        n < 0 || n > 100 -> n.toString()
        n < 20 -> SMALL[n]
        n == 100 -> "hundra"
        n % 10 == 0 -> TENS[n / 10]
        else -> TENS[n / 10] + SMALL[n % 10]
    }

    /** "en krona", "två kronor", "tjugoen kronor". */
    fun kronor(n: Int): String = when {
        n == 1 -> "en krona"
        n > 20 && n % 10 == 1 -> word(n).removeSuffix("ett") + "en kronor"
        else -> word(n) + " kronor"
    }
}
