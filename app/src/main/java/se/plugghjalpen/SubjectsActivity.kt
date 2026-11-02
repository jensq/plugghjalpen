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
        val pad = Ui.dp(this, 18)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(pad, pad, pad, Ui.dp(this, 12))
        root.background = Ui.page()
        root.clipToPadding = false

        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.gravity = Gravity.CENTER_VERTICAL
        bar.addView(Ui.roundIconButton(this, R.drawable.ic_back, Ui.PRIMARY, 48) { finish() })
        val chip = Ui.text(this, if (age == 4) "4+" else "7+", 18f, 0xFFFFFFFF.toInt(), bold = true)
        chip.gravity = Gravity.CENTER
        chip.background = if (age == 4) Ui.gradient(this, 0xFFFF7EB3.toInt(), 0xFFFF9F5A.toInt(), 16)
            else Ui.gradient(this, Ui.PRIMARY, Ui.PRIMARY_2, 16)
        chip.setPadding(Ui.dp(this, 12), 0, Ui.dp(this, 12), 0)
        val clp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Ui.dp(this, 34))
        clp.leftMargin = Ui.dp(this, 14)
        bar.addView(chip, clp)
        val title = Ui.text(this, if (age == 4) "Vad vill du spela?" else "Vad vill du plugga?", 24f, Ui.TEXT, bold = true)
        title.maxLines = 1
        title.setAutoSizeTextTypeUniformWithConfiguration(14, 24, 1, android.util.TypedValue.COMPLEX_UNIT_SP)
        val tlp = LinearLayout.LayoutParams(0, Ui.dp(this, 40), 1f)
        tlp.leftMargin = Ui.dp(this, 10)
        bar.addView(title, tlp)
        root.addView(bar, Ui.matchWrap())

        val grid = OptionGrid(this, 0.85f, 1.15f, Ui.dp(this, 16), Ui.dp(this, 340))
        grid.clipToPadding = false
        grid.clipChildren = false
        val gp = Ui.dp(this, 4)
        grid.setPadding(gp, gp, gp, gp)
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

    private var cardIndex = 0

    /** White card with a soft coloured area for the picture and the name underneath. */
    private fun subjectCard(label: String, iconView: View, onClick: () -> Unit): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.background = Ui.card(this)
        Ui.elevate(card, 4)
        val p = Ui.dp(this, 12)
        card.setPadding(p, p, p, Ui.dp(this, 8))

        val well = android.widget.FrameLayout(this)
        well.background = Ui.rounded(this, Ui.TINTS[cardIndex++ % Ui.TINTS.size], 20)
        val wp = Ui.dp(this, 14)
        well.setPadding(wp, wp, wp, wp)
        well.addView(iconView, android.widget.FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        card.addView(well, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        val t = Ui.text(this, label, 22f, Ui.TEXT, bold = true)
        t.gravity = Gravity.CENTER
        t.maxLines = 1
        t.setAutoSizeTextTypeUniformWithConfiguration(12, 22, 1, android.util.TypedValue.COMPLEX_UNIT_SP)
        val tl = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 40))
        card.addView(t, tl)
        card.isClickable = true
        card.isFocusable = true
        card.contentDescription = label
        card.setOnClickListener { onClick() }
        return card
    }
}
