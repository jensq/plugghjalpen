package se.plugghjalpen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

/** The exercises for one age group. */
class SubjectsActivity : Activity() {
    companion object {
        const val AGE = "age"
    }

    private lateinit var score: ScoreBadge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val age = intent.getIntExtra(AGE, 7)
        val pad = Ui.dp(this, 16)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(pad, pad, pad, Ui.dp(this, 12))
        root.setBackgroundColor(Ui.BG)

        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.gravity = Gravity.CENTER_VERTICAL
        bar.addView(Ui.roundIconButton(this, R.drawable.ic_back, Ui.PRIMARY, 48) { finish() })
        val title = Ui.text(this, if (age == 4) "4+  Vad vill du spela?" else "7+  Vad vill du plugga?", 24f, Ui.TEXT, bold = true)
        title.maxLines = 1
        title.setAutoSizeTextTypeUniformWithConfiguration(14, 24, 1, android.util.TypedValue.COMPLEX_UNIT_SP)
        val tlp = LinearLayout.LayoutParams(0, Ui.dp(this, 40), 1f)
        tlp.leftMargin = pad
        bar.addView(title, tlp)
        root.addView(bar, Ui.matchWrap())

        val grid = OptionGrid(this, 1f, 1f, Ui.dp(this, 20), Ui.dp(this, 340))
        if (age == 4) addKids(grid) else addBig(grid)
        val glp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
        glp.topMargin = Ui.dp(this, 12)
        root.addView(grid, glp)

        score = ScoreBadge(this)
        val slp = Ui.matchWrap()
        slp.topMargin = Ui.dp(this, 14)
        root.addView(score.row, slp)

        setContentView(root)
        if (age == 4 && savedInstanceState == null) Speech.speak(this, "Vad vill du spela?")
    }

    override fun onResume() {
        super.onResume()
        score.refresh()
    }

    private fun addBig(grid: OptionGrid) {
        val clock = ClockView(this)
        clock.time = ClockTime(10, 10)
        grid.addView(subjectCard("Klockan", clock) { start(ClockActivity::class.java) })
        grid.addView(subjectCard("Engelska", image(R.drawable.ic_flag_uk)) { start(EnglishActivity::class.java) })
        grid.addView(subjectCard("Matte", image(R.drawable.ic_math)) { start(MathActivity::class.java) })
        val money = MoneyView(this)
        money.pieces = listOf(20, 10, 5)
        grid.addView(subjectCard("Pengar", money) { start(MoneyActivity::class.java) })
        val flags = FlagView(this)
        flags.flag = Flags.ALL.first() // Sverige
        grid.addView(subjectCard("Flaggor", flags) { start(FlagActivity::class.java) })
        grid.addView(subjectCard("Kalendern", image(R.drawable.ic_calendar)) { start(CalendarActivity::class.java) })
    }

    private fun addKids(grid: OptionGrid) {
        val count = CountView(this)
        count.set("🍎", 3)
        grid.addView(subjectCard("Räkna", count) { start(CountActivity::class.java) })
        val abc = Ui.text(this, "ABC", 60f, Ui.PRIMARY, bold = true)
        abc.gravity = Gravity.CENTER
        abc.maxLines = 1
        abc.setAutoSizeTextTypeUniformWithConfiguration(20, 120, 2, android.util.TypedValue.COMPLEX_UNIT_SP)
        grid.addView(subjectCard("Bokstäver", abc) { start(LetterActivity::class.java) })
        val shapes = ShapeView(this)
        shapes.set(ShapeKind.STAR, Ui.ACCENT)
        grid.addView(subjectCard("Former", shapes) { start(ShapeActivity::class.java) })
        grid.addView(subjectCard("Passar inte", Ui.emoji(this, "🐶🐱🚗", 120)) { start(OddOneOutActivity::class.java) })
    }

    private fun image(res: Int): ImageView {
        val i = ImageView(this)
        i.setImageResource(res)
        i.scaleType = ImageView.ScaleType.FIT_CENTER
        return i
    }

    private fun start(c: Class<*>) = startActivity(Intent(this, c))

    private fun subjectCard(label: String, iconView: View, onClick: () -> Unit): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.background = Ui.card(this)
        val p = Ui.dp(this, 16)
        card.setPadding(p, p, p, p)
        val ilp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
        ilp.setMargins(p, p / 2, p, p / 2)
        card.addView(iconView, ilp)
        val t = Ui.text(this, label, 24f, Ui.TEXT, bold = true)
        t.gravity = Gravity.CENTER
        t.maxLines = 1
        t.setAutoSizeTextTypeUniformWithConfiguration(12, 24, 1, android.util.TypedValue.COMPLEX_UNIT_SP)
        card.addView(t, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 34)))
        card.isClickable = true
        card.isFocusable = true
        card.contentDescription = label
        card.setOnClickListener { onClick() }
        return card
    }
}
