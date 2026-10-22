package se.plugghjalpen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

/** Start screen: choose what to practise. */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = Ui.dp(this, 24)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(pad, pad, pad, Ui.dp(this, 12))
        root.setBackgroundColor(Ui.BG)

        val title = Ui.text(this, getString(R.string.app_name), 34f, Ui.PRIMARY, bold = true)
        title.gravity = Gravity.CENTER
        root.addView(title, Ui.matchWrap())
        val sub = Ui.text(this, "Vad vill du plugga?", 22f, Ui.TEXT)
        sub.gravity = Gravity.CENTER
        root.addView(sub, Ui.matchWrap())

        val grid = OptionGrid(this, 1f, 1f, Ui.dp(this, 20), Ui.dp(this, 340))
        val clock = ClockView(this)
        clock.time = ClockTime(10, 10)
        grid.addView(subjectCard("Klockan", clock) { start(ClockActivity::class.java) })
        val flag = ImageView(this)
        flag.setImageResource(R.drawable.ic_flag_uk)
        flag.scaleType = ImageView.ScaleType.FIT_CENTER
        grid.addView(subjectCard("Engelska", flag) { start(EnglishActivity::class.java) })
        val math = ImageView(this)
        math.setImageResource(R.drawable.ic_math)
        math.scaleType = ImageView.ScaleType.FIT_CENTER
        grid.addView(subjectCard("Matte", math) { start(MathActivity::class.java) })
        val money = MoneyView(this)
        money.pieces = listOf(20, 10, 5)
        grid.addView(subjectCard("Pengar", money) { start(MoneyActivity::class.java) })
        val glp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
        glp.topMargin = Ui.dp(this, 12)
        root.addView(grid, glp)

        // version + manual update check
        val bottom = LinearLayout(this)
        bottom.orientation = LinearLayout.HORIZONTAL
        bottom.gravity = Gravity.CENTER_VERTICAL
        bottom.addView(
            Ui.text(this, "Version " + Updater.currentName(this), 14f, Ui.TEXT_DIM),
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        val upd = LinearLayout(this)
        upd.orientation = LinearLayout.HORIZONTAL
        upd.gravity = Gravity.CENTER_VERTICAL
        val u = Ui.dp(this, 10)
        upd.setPadding(u, u, u, u)
        upd.background = Ui.pressable(Ui.rounded(this, 0x00000000, 16))
        upd.addView(Ui.icon(this, R.drawable.ic_refresh, 20, Ui.TEXT_DIM))
        val ut = Ui.text(this, "Sök efter uppdateringar", 14f, Ui.TEXT_DIM)
        val utlp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        utlp.leftMargin = Ui.dp(this, 6)
        upd.addView(ut, utlp)
        upd.isClickable = true
        upd.setOnClickListener { Updater.check(this, false) }
        bottom.addView(upd)
        root.addView(bottom, Ui.matchWrap())

        setContentView(root)
        if (savedInstanceState == null) Updater.check(this, true)
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
        card.addView(t, Ui.matchWrap())
        card.isClickable = true
        card.isFocusable = true
        card.contentDescription = label
        card.setOnClickListener { onClick() }
        return card
    }
}
