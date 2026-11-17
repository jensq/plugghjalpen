package se.plugghjalpen

import kotlin.random.Random

/** 4+ "Hitta bokstaven": the app says a letter, tap the right capital letter among three. */
class HearLetterActivity : QuizActivity() {
    private val deck = LetterDeck()
    private var current: Char? = null

    override val screenTitle = "Hitta bokstaven"

    override fun makeGrid() = OptionGrid(this, 1f, 1f, Ui.dp(this, 14))

    override fun newQuestion() {
        val c = deck.next()
        current = c
        // the button must not show the letter, only let you hear it again
        questionArea.addView(speakerButton("Vilken bokstav?", 26f))
        for (o in Alphabet.options(c, Random.Default)) {
            val name = Alphabet.spoken(o)
            addOption(Ui.optionText(this, o.toString(), 30, 140), o == c, "Bokstaven $name", name)
        }
    }

    override fun speakQuestion() {
        current?.let { Speech.speak(this, "Var är bokstaven ${Alphabet.spoken(it)}?") }
    }
}
