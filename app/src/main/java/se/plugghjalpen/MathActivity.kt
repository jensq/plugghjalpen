package se.plugghjalpen

import android.view.Gravity
import android.widget.TextView
import kotlin.random.Random

/** "Matte": plus and minus up to 20, three answers to choose from. */
class MathActivity : QuizActivity() {
    private var current: MathTask? = null

    override val screenTitle = "Matte"

    override fun makeGrid() = OptionGrid(this, 1f, 2.5f, Ui.dp(this, 14), Ui.dp(this, 150))

    override fun newQuestion() {
        val t = MathQuiz.randomTask(Random.Default, avoid = current)
        current = t
        questionArea.addView(speakerButton(t.text, 40f))
        for (n in MathQuiz.options(t, Random.Default)) {
            addOption(numberLabel(n.toString()), n == t.answer, n.toString())
        }
    }

    override fun speakQuestion() {
        current?.let { Speech.speak(this, it.spoken) }
    }

    private fun numberLabel(s: String): TextView {
        val t = TextView(this)
        t.text = s
        t.setTextColor(Ui.TEXT)
        t.typeface = android.graphics.Typeface.DEFAULT_BOLD
        t.gravity = Gravity.CENTER
        t.maxLines = 1
        t.setAutoSizeTextTypeUniformWithConfiguration(20, 72, 2, android.util.TypedValue.COMPLEX_UNIT_SP)
        return t
    }
}
