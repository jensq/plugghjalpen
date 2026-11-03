package se.plugghjalpen

import kotlin.random.Random

/** 7+ "Rim": a word at the top, tap the word that rhymes with it. */
class RhymeActivity : QuizActivity() {
    private var task: Rhymes.Task? = null

    override val screenTitle = "Rim"

    override fun makeGrid() = OptionGrid(this, 2.2f, 6f, Ui.dp(this, 12), Ui.dp(this, 110))

    override fun newQuestion() {
        val t = Rhymes.task(Random.Default, avoid = task?.word)
        task = t
        questionArea.addView(speakerButton(t.word, 40f))
        // each word is read aloud when tapped, so you can hear whether it rhymes
        for (o in t.options) addOption(Ui.optionText(this, o, 14, 36), o == t.answer, o, o)
    }

    override fun speakQuestion() {
        task?.let { Speech.speak(this, "Vilket ord rimmar på ${it.word}?") }
    }
}
