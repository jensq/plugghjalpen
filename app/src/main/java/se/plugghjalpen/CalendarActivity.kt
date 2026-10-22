package se.plugghjalpen

import android.view.Gravity
import android.widget.TextView
import kotlin.random.Random

/** "Kalendern": which day or month comes before/after another. */
class CalendarActivity : QuizActivity() {
    private var current: CalendarTask? = null

    override val screenTitle = "Kalendern"

    override fun makeGrid() = OptionGrid(this, 2.2f, 6f, Ui.dp(this, 12), Ui.dp(this, 110))

    override fun newQuestion() {
        val t = CalendarQuiz.randomTask(Random.Default, avoid = current)
        current = t
        questionArea.addView(speakerButton(t.question, 24f))
        for (o in t.options(Random.Default)) addOption(label(o), o == t.answer, o)
    }

    override fun speakQuestion() {
        current?.let { Speech.speak(this, it.question) }
    }

    private fun label(s: String): TextView {
        val t = TextView(this)
        t.text = s
        t.setTextColor(Ui.TEXT)
        t.typeface = android.graphics.Typeface.DEFAULT_BOLD
        t.gravity = Gravity.CENTER
        t.maxLines = 1
        t.setAutoSizeTextTypeUniformWithConfiguration(14, 34, 1, android.util.TypedValue.COMPLEX_UNIT_SP)
        return t
    }
}
