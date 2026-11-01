package se.plugghjalpen

import android.view.ViewGroup
import android.widget.LinearLayout
import kotlin.random.Random

/** 4+ "Räkna": how many apples (stars, fish …)? Pick the number. */
class CountActivity : QuizActivity() {
    private var n = 0
    private var plural = ""

    override val screenTitle = "Räkna"

    override fun makeGrid() = OptionGrid(this, 1f, 2f, Ui.dp(this, 14), Ui.dp(this, 150))

    override fun newQuestion() {
        val (emoji, name) = Kids.COUNTABLES.random()
        var next = Random.nextInt(1, Kids.COUNT_MAX + 1)
        while (next == n) next = Random.nextInt(1, Kids.COUNT_MAX + 1)
        n = next
        plural = name
        questionArea.addView(speakerButton("Hur många?", 28f))
        val v = CountView(this)
        v.set(emoji, n)
        val h = minOf(Ui.dp(this, 220), resources.displayMetrics.heightPixels / 3)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h)
        lp.topMargin = Ui.dp(this, 8)
        questionArea.addView(v, lp)
        for (x in Kids.countOptions(n, Random.Default)) {
            addOption(Ui.emoji(this, x.toString(), 80).also { it.setTextColor(Ui.PRIMARY) }, x == n, "$x", SwedishNumbers.word(x))
        }
    }

    override fun speakQuestion() {
        if (plural.isNotEmpty()) Speech.speak(this, "Hur många $plural är det?")
    }
}
