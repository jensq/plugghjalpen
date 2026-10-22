package se.plugghjalpen

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.random.Random

/**
 * "Pengar", two kinds of questions at random:
 *  - "Hur mycket är det?": coins/notes are shown, pick the right total.
 *  - "Välj 15 kr": pick the group of coins/notes that adds up to the amount.
 */
class MoneyActivity : QuizActivity() {
    private var spoken = ""
    private var lastTotal = -1

    override val screenTitle = "Pengar"

    override fun makeGrid() = OptionGrid(this, 1f, 3f, Ui.dp(this, 14), Ui.dp(this, 160))

    override fun newQuestion() {
        var set = MoneyQuiz.randomSet(Random.Default)
        while (set.sum() == lastTotal) set = MoneyQuiz.randomSet(Random.Default)
        val total = set.sum()
        lastTotal = total
        if (Random.nextBoolean()) askTotal(set) else askPick(total)
    }

    private fun askTotal(set: List<Int>) {
        spoken = "Hur mycket pengar är det?"
        questionArea.addView(speakerButton("Hur mycket är det?", 26f))
        val money = MoneyView(this)
        money.pieces = set
        // about a quarter of the screen height, but not more than 170 dp
        val h = minOf(Ui.dp(this, 170), resources.displayMetrics.heightPixels / 4)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h)
        lp.topMargin = Ui.dp(this, 8)
        questionArea.addView(money, lp)
        val total = set.sum()
        for (n in MoneyQuiz.totalOptions(set, Random.Default)) {
            addOption(amountLabel("$n kr"), n == total, "$n kronor")
        }
    }

    private fun askPick(total: Int) {
        spoken = "Välj ${SwedishNumbers.kronor(total)}."
        questionArea.addView(speakerButton("Välj $total kr", 32f))
        for (s in MoneyQuiz.setOptions(total, Random.Default)) {
            val v = MoneyView(this)
            v.pieces = s
            addOption(v, s.sum() == total, "Pengar")
        }
    }

    override fun speakQuestion() {
        if (spoken.isNotEmpty()) Speech.speak(this, spoken)
    }

    private fun amountLabel(s: String): TextView {
        val t = TextView(this)
        t.text = s
        t.setTextColor(Ui.TEXT)
        t.typeface = android.graphics.Typeface.DEFAULT_BOLD
        t.gravity = Gravity.CENTER
        t.maxLines = 1
        t.setAutoSizeTextTypeUniformWithConfiguration(18, 56, 2, android.util.TypedValue.COMPLEX_UNIT_SP)
        return t
    }
}
