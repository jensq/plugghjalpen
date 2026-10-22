package se.plugghjalpen

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.random.Random

/**
 * "Flaggor", two kinds of questions at random:
 *  - a flag is shown, pick the country (three names)
 *  - "Var är Norges flagga?", pick the flag (three flags)
 */
class FlagActivity : QuizActivity() {
    private val deck = FlagDeck()
    private var spoken = ""

    override val screenTitle = "Flaggor"

    override fun makeGrid() = OptionGrid(this, 1f, 3f, Ui.dp(this, 14), Ui.dp(this, 160))

    override fun newQuestion() {
        val f = deck.next()
        val options = Flags.options(f, Random.Default)
        if (Random.nextBoolean()) {
            spoken = "Vilket land har den här flaggan?"
            questionArea.addView(speakerButton("Vilket land?", 28f))
            val v = FlagView(this)
            v.flag = f
            val h = minOf(Ui.dp(this, 170), resources.displayMetrics.heightPixels / 4)
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h)
            lp.topMargin = Ui.dp(this, 8)
            questionArea.addView(v, lp)
            for (o in options) addOption(label(o.name), o === f, o.name)
        } else {
            spoken = "Var är ${f.genitive} flagga?"
            questionArea.addView(speakerButton("${f.genitive} flagga", 28f))
            for (o in options) {
                val v = FlagView(this)
                v.flag = o
                addOption(v, o === f, "Flagga")
            }
        }
    }

    override fun speakQuestion() {
        if (spoken.isNotEmpty()) Speech.speak(this, spoken)
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
