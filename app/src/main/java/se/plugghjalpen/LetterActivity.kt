package se.plugghjalpen

import kotlin.random.Random

/** 4+ "Bokstäver": which picture starts with S? */
class LetterActivity : QuizActivity() {
    private var task: Kids.LetterTask? = null

    override val screenTitle = "Bokstäver"

    override fun makeGrid() = OptionGrid(this, 1f, 1f, Ui.dp(this, 14))

    override fun newQuestion() {
        val t = Kids.letterTask(Random.Default, avoid = task?.letter)
        task = t
        questionArea.addView(speakerButton(t.letter.toString(), 48f))
        for (o in t.options) addOption(Ui.emoji(this, o.emoji, 120), o === t.answer, o.name, o.name)
    }

    override fun speakQuestion() {
        task?.let { Speech.speak(this, "Vilken bild börjar på bokstaven ${it.letter}?") }
    }
}
