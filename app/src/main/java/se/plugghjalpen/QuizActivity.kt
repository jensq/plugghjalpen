package se.plugghjalpen

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Shared screen for both quizzes: title bar, a question area, feedback text, a grid of
 * alternatives and a "next" arrow. Subclasses fill in the question and the alternatives.
 */
abstract class QuizActivity : Activity() {
    protected lateinit var questionArea: LinearLayout
    protected lateinit var grid: OptionGrid
    private lateinit var feedback: TextView
    private lateinit var next: View
    private var solved = false
    private var missed = false // a wrong answer was given on this question

    abstract val screenTitle: String
    abstract fun makeGrid(): OptionGrid

    /** Build the question and add one view per alternative to [grid] via [addOption]. */
    abstract fun newQuestion()

    /** Speak the question (called for every new question and from the speaker button). */
    abstract fun speakQuestion()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = Ui.dp(this, 16)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(pad, pad, pad, pad)
        root.setBackgroundColor(Ui.BG)

        // title bar
        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.gravity = Gravity.CENTER_VERTICAL
        bar.addView(Ui.roundIconButton(this, R.drawable.ic_back, Ui.PRIMARY, 48) { finish() })
        val title = Ui.text(this, screenTitle, 24f, Ui.TEXT, bold = true)
        val tlp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        tlp.leftMargin = pad
        bar.addView(title, tlp)
        root.addView(bar, Ui.matchWrap())

        questionArea = LinearLayout(this)
        questionArea.orientation = LinearLayout.VERTICAL
        val qlp = Ui.matchWrap()
        qlp.topMargin = pad
        root.addView(questionArea, qlp)

        feedback = Ui.text(this, "", 22f, Ui.TEXT, bold = true)
        feedback.gravity = Gravity.CENTER
        root.addView(feedback, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 44)))

        grid = makeGrid()
        root.addView(grid, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        // "next" arrow, bottom right
        val bottom = LinearLayout(this)
        bottom.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        next = nextButton()
        bottom.addView(next)
        val blp = Ui.matchWrap()
        blp.topMargin = Ui.dp(this, 12)
        root.addView(bottom, blp)

        setContentView(root)
        showNewQuestion()
    }

    private fun nextButton(): View {
        val b = LinearLayout(this)
        b.orientation = LinearLayout.HORIZONTAL
        b.gravity = Gravity.CENTER
        b.background = Ui.pressable(Ui.rounded(this, Ui.ACCENT, 32))
        b.setPadding(Ui.dp(this, 28), 0, Ui.dp(this, 22), 0)
        b.addView(Ui.text(this, "Nästa", 22f, 0xFFFFFFFF.toInt(), bold = true))
        val arrow = Ui.icon(this, R.drawable.ic_next, 34)
        (arrow.layoutParams as LinearLayout.LayoutParams).leftMargin = Ui.dp(this, 8)
        b.addView(arrow)
        b.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Ui.dp(this, 64))
        b.isClickable = true
        b.isFocusable = true
        b.contentDescription = "Nästa fråga"
        b.setOnClickListener { showNewQuestion() }
        return b
    }

    private fun showNewQuestion() {
        solved = false
        missed = false
        feedback.text = ""
        questionArea.removeAllViews()
        grid.removeAllViews()
        newQuestion()
        speakQuestion()
    }

    /** Adds an alternative. [content] is shown inside a white card; [correct] marks the right one. */
    protected fun addOption(content: View, correct: Boolean, description: String) {
        val card = android.widget.FrameLayout(this)
        card.background = Ui.card(this)
        val p = Ui.dp(this, 8)
        card.setPadding(p, p, p, p)
        card.addView(content, android.widget.FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        card.isClickable = true
        card.isFocusable = true
        card.contentDescription = description
        card.setOnClickListener { answer(card, correct) }
        grid.addView(card)
    }

    private fun answer(card: View, correct: Boolean) {
        if (solved) return
        if (correct) {
            solved = true
            if (!missed) Stats.addFirstTryCorrect(this)
            card.background = Ui.card(this, Ui.CORRECT_BG, Ui.CORRECT)
            feedback.text = "Rätt, bra jobbat!"
            feedback.setTextColor(Ui.CORRECT)
            Speech.speak(this, "Rätt, bra jobbat!")
            for (i in 0 until grid.childCount) grid.getChildAt(i).isClickable = false
            Ui.pulse(card)
            next.postDelayed({ Ui.pulse(next) }, 500)
        } else {
            card.background = Ui.card(this, Ui.WRONG_BG, Ui.WRONG)
            card.alpha = 0.6f
            missed = true
            card.isClickable = false
            feedback.text = "Försök igen!"
            feedback.setTextColor(Ui.WRONG)
            Speech.speak(this, "Försök igen!")
            Ui.shake(card)
        }
    }

    /** Big blue button with a speaker icon and [label]; tapping it repeats the question. */
    protected fun speakerButton(label: String, textSp: Float): View {
        val b = LinearLayout(this)
        b.orientation = LinearLayout.HORIZONTAL
        b.gravity = Gravity.CENTER
        b.background = Ui.pressable(Ui.rounded(this, Ui.PRIMARY, 24))
        val p = Ui.dp(this, 16)
        b.setPadding(p, Ui.dp(this, 10), p, Ui.dp(this, 10))
        b.minimumHeight = Ui.dp(this, 76)
        b.addView(Ui.icon(this, R.drawable.ic_speaker, 44))
        val t = Ui.text(this, label, textSp, 0xFFFFFFFF.toInt(), bold = true)
        t.gravity = Gravity.CENTER
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.leftMargin = Ui.dp(this, 14)
        b.addView(t, lp)
        b.isClickable = true
        b.isFocusable = true
        b.contentDescription = "Lyssna: $label"
        b.setOnClickListener { speakQuestion() }
        b.layoutParams = Ui.matchWrap()
        return b
    }

    override fun onPause() {
        super.onPause()
        Speech.stop()
    }
}
