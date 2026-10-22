package se.plugghjalpen

import android.view.Gravity
import android.widget.TextView

/** "Engelska": an English word, pick the Swedish translation among five. */
class EnglishActivity : QuizActivity() {
    private val deck = WordDeck()
    private var current: Word? = null

    override val screenTitle = "Engelska"

    override fun makeGrid() = OptionGrid(this, 2.2f, 6f, Ui.dp(this, 12), Ui.dp(this, 96))

    override fun newQuestion() {
        val w = deck.next()
        current = w
        // the English word, tap to hear it pronounced
        questionArea.addView(speakerButton(w.english, 40f))
        for (o in deck.options(w)) {
            val t = TextView(this)
            t.text = o.swedish
            t.setTextColor(Ui.TEXT)
            t.typeface = android.graphics.Typeface.DEFAULT_BOLD
            t.gravity = Gravity.CENTER
            t.maxLines = 1
            t.setAutoSizeTextTypeUniformWithConfiguration(14, 34, 1, android.util.TypedValue.COMPLEX_UNIT_SP)
            addOption(t, o === w, o.swedish)
        }
    }

    override fun speakQuestion() {
        current?.let { Speech.speak(this, it.english, Speech.ENGLISH) }
    }
}
