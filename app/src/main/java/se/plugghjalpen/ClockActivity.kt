package se.plugghjalpen

import kotlin.random.Random

/** "Klockan": hear a time, pick the clock that shows it. */
class ClockActivity : QuizActivity() {
    private var current: ClockTime? = null

    override val screenTitle = "Klockan"

    override fun makeGrid() = OptionGrid(this, 1f, 1f, Ui.dp(this, 14))

    override fun newQuestion() {
        val t = ClockQuiz.randomTime(Random.Default, avoid = current)
        current = t
        questionArea.addView(speakerButton(ClockQuiz.phrase(t), 28f))
        for (o in ClockQuiz.options(t, Random.Default)) {
            val clock = ClockView(this)
            clock.time = o
            addOption(clock, o == t, "Klocka")
        }
    }

    override fun speakQuestion() {
        current?.let { Speech.speak(this, ClockQuiz.phrase(it)) }
    }
}
