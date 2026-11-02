package se.plugghjalpen

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Shared screen for all exercises: title bar, a question area, feedback, a grid of alternatives
 * and a "Nästa" button. "Nästa" stays grey and can't be pressed until the right answer is found.
 * Subclasses fill in the question and the alternatives.
 */
abstract class QuizActivity : Activity() {
    protected lateinit var questionArea: LinearLayout
    protected lateinit var grid: OptionGrid
    private lateinit var feedback: TextView
    private lateinit var next: LinearLayout
    private lateinit var nextLabel: TextView
    private lateinit var nextArrow: ImageView
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
        val pad = Ui.dp(this, 18)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(pad, pad, pad, pad)
        root.background = Ui.page()
        root.clipToPadding = false

        // title bar
        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.gravity = Gravity.CENTER_VERTICAL
        bar.addView(Ui.roundIconButton(this, R.drawable.ic_back, Ui.PRIMARY, 48) { finish() })
        val title = Ui.text(this, screenTitle, 24f, Ui.TEXT, bold = true)
        title.maxLines = 1
        title.setAutoSizeTextTypeUniformWithConfiguration(14, 24, 1, android.util.TypedValue.COMPLEX_UNIT_SP)
        val tlp = LinearLayout.LayoutParams(0, Ui.dp(this, 36), 1f)
        tlp.leftMargin = Ui.dp(this, 14)
        bar.addView(title, tlp)
        root.addView(bar, Ui.matchWrap())

        questionArea = LinearLayout(this)
        questionArea.orientation = LinearLayout.VERTICAL
        questionArea.clipToPadding = false
        questionArea.clipChildren = false
        val qlp = Ui.matchWrap()
        qlp.topMargin = Ui.dp(this, 16)
        root.addView(questionArea, qlp)

        // feedback chip; the row keeps its height so nothing jumps when it appears
        val fbRow = FrameLayout(this)
        feedback = Ui.text(this, "", 19f, Ui.TEXT, bold = true)
        feedback.gravity = Gravity.CENTER
        feedback.setPadding(Ui.dp(this, 18), 0, Ui.dp(this, 18), 0)
        feedback.visibility = View.INVISIBLE
        fbRow.addView(feedback, FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Ui.dp(this, 38), Gravity.CENTER))
        val flp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 54))
        root.addView(fbRow, flp)

        grid = makeGrid()
        grid.clipToPadding = false
        grid.clipChildren = false
        val gp = Ui.dp(this, 4)
        grid.setPadding(gp, gp, gp, gp)
        root.addView(grid, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        next = nextButton()
        val blp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 62))
        blp.topMargin = Ui.dp(this, 14)
        root.addView(next, blp)

        setContentView(root)
        showNewQuestion()
    }

    private fun nextButton(): LinearLayout {
        val b = LinearLayout(this)
        b.orientation = LinearLayout.HORIZONTAL
        b.gravity = Gravity.CENTER
        nextLabel = Ui.text(this, "Nästa", 22f, 0xFFFFFFFF.toInt(), bold = true)
        b.addView(nextLabel)
        nextArrow = Ui.icon(this, R.drawable.ic_next, 30)
        (nextArrow.layoutParams as LinearLayout.LayoutParams).leftMargin = Ui.dp(this, 10)
        b.addView(nextArrow)
        b.isFocusable = true
        b.contentDescription = "Nästa fråga"
        b.setOnClickListener { if (solved) showNewQuestion() }
        return b
    }

    /** Grey and inactive until the question is solved, then a bright gradient. */
    private fun setNextEnabled(on: Boolean) {
        next.isEnabled = on
        next.isClickable = on
        if (on) {
            next.background = Ui.pressable(Ui.gradient(this, Ui.ACCENT, Ui.ACCENT_2, 31), 0x33FFFFFF)
            nextLabel.setTextColor(0xFFFFFFFF.toInt())
            nextArrow.imageTintList = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
            Ui.elevate(next, 6)
        } else {
            next.background = Ui.rounded(this, Ui.DISABLED, 31)
            nextLabel.setTextColor(Ui.DISABLED_TEXT)
            nextArrow.imageTintList = android.content.res.ColorStateList.valueOf(Ui.DISABLED_TEXT)
            next.elevation = 0f
        }
    }

    private fun showNewQuestion() {
        solved = false
        missed = false
        feedback.visibility = View.INVISIBLE
        setNextEnabled(false)
        questionArea.removeAllViews()
        grid.removeAllViews()
        newQuestion()
        Ui.appear(questionArea)
        for (i in 0 until grid.childCount) Ui.appear(grid.getChildAt(i), 60L + i * 50L)
        speakQuestion()
    }

    /** Extra sentence said after "Rätt, bra jobbat!" (e.g. why the answer is right). */
    protected open fun praiseExtra(): String? = null

    /**
     * Adds an alternative. [content] is shown inside a white card; [correct] marks the right one.
     * [say]: read this aloud before the feedback when tapped (4+ exercises: "Sol. Rätt, bra jobbat!").
     */
    protected fun addOption(content: View, correct: Boolean, description: String, say: String? = null) {
        val card = FrameLayout(this)
        card.background = Ui.card(this)
        Ui.elevate(card, 4)
        val p = Ui.dp(this, 10)
        card.setPadding(p, p, p, p)
        card.addView(content, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        card.isClickable = true
        card.isFocusable = true
        card.contentDescription = description
        card.setOnClickListener { answer(card, correct, say) }
        grid.addView(card)
    }

    private fun showFeedback(text: String, color: Int, bg: Int) {
        feedback.text = text
        feedback.setTextColor(color)
        feedback.background = Ui.rounded(this, bg, 19)
        feedback.visibility = View.VISIBLE
        feedback.scaleX = 0.85f
        feedback.scaleY = 0.85f
        feedback.animate().scaleX(1f).scaleY(1f).setDuration(180).start()
    }

    private fun answer(card: View, correct: Boolean, say: String?) {
        if (solved) return
        val prefix = if (say != null) "$say. " else ""
        if (correct) {
            solved = true
            if (!missed) Stats.addFirstTryCorrect(this)
            card.background = Ui.card(this, Ui.CORRECT_BG, Ui.CORRECT)
            showFeedback("✓  Rätt, bra jobbat!", Ui.CORRECT, Ui.CORRECT_BG)
            Speech.speak(this, prefix + "Rätt, bra jobbat!" + (praiseExtra()?.let { " $it" } ?: ""))
            for (i in 0 until grid.childCount) {
                val c = grid.getChildAt(i)
                c.isClickable = false
                if (c !== card) c.animate().alpha(0.45f).setDuration(200).start()
            }
            Ui.pulse(card)
            setNextEnabled(true)
            next.postDelayed({ Ui.pulse(next) }, 450)
        } else {
            card.background = Ui.card(this, Ui.WRONG_BG, Ui.WRONG)
            card.alpha = 0.7f
            card.elevation = 0f
            missed = true
            card.isClickable = false
            showFeedback("Försök igen!", Ui.WRONG, Ui.WRONG_BG)
            Speech.speak(this, prefix + "Försök igen!")
            Ui.shake(card)
        }
    }

    /** Big gradient button with a speaker icon and [label]; tapping it repeats the question. */
    protected fun speakerButton(label: String, textSp: Float): View {
        val b = LinearLayout(this)
        b.orientation = LinearLayout.HORIZONTAL
        b.gravity = Gravity.CENTER
        b.background = Ui.pressable(Ui.gradient(this, Ui.PRIMARY, Ui.PRIMARY_2, 26), 0x33FFFFFF)
        Ui.elevate(b, 6)
        val p = Ui.dp(this, 16)
        b.setPadding(p, Ui.dp(this, 12), Ui.dp(this, 20), Ui.dp(this, 12))
        b.minimumHeight = Ui.dp(this, 80)

        // speaker in a light circle
        val circle = FrameLayout(this)
        val cbg = android.graphics.drawable.GradientDrawable()
        cbg.shape = android.graphics.drawable.GradientDrawable.OVAL
        cbg.setColor(0x33FFFFFF)
        circle.background = cbg
        circle.addView(Ui.icon(this, R.drawable.ic_speaker, 30), FrameLayout.LayoutParams(Ui.dp(this, 30), Ui.dp(this, 30), Gravity.CENTER))
        b.addView(circle, LinearLayout.LayoutParams(Ui.dp(this, 54), Ui.dp(this, 54)))

        val t = Ui.text(this, label, textSp, 0xFFFFFFFF.toInt(), bold = true)
        t.gravity = Gravity.CENTER
        val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        lp.leftMargin = Ui.dp(this, 12)
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
