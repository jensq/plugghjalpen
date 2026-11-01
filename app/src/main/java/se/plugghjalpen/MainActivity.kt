package se.plugghjalpen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

/** Start screen: choose age group, 4+ or 7+. */
class MainActivity : Activity() {
    private lateinit var score: ScoreBadge

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
        val sub = Ui.text(this, "Hur gammal är du?", 22f, Ui.TEXT)
        sub.gravity = Gravity.CENTER
        root.addView(sub, Ui.matchWrap())

        val grid = OptionGrid(this, 0.8f, 1f, Ui.dp(this, 24), Ui.dp(this, 360))
        grid.addView(ageCard("4+", "🧸", 4))
        grid.addView(ageCard("7+", "🎒", 7))
        val glp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
        glp.topMargin = Ui.dp(this, 12)
        root.addView(grid, glp)

        score = ScoreBadge(this)
        val slp = Ui.matchWrap()
        slp.topMargin = Ui.dp(this, 14)
        root.addView(score.row, slp)

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

    override fun onResume() {
        super.onResume()
        score.refresh()
    }

    private fun ageCard(label: String, emoji: String, age: Int): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.background = Ui.card(this)
        val p = Ui.dp(this, 16)
        card.setPadding(p, p, p, p)
        val e = Ui.emoji(this, emoji, 96)
        card.addView(e, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        val t = Ui.text(this, label, 56f, Ui.PRIMARY, bold = true)
        t.gravity = Gravity.CENTER
        t.maxLines = 1
        t.setAutoSizeTextTypeUniformWithConfiguration(20, 56, 2, android.util.TypedValue.COMPLEX_UNIT_SP)
        card.addView(t, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.8f))
        card.isClickable = true
        card.isFocusable = true
        card.contentDescription = "$label år"
        card.setOnClickListener {
            startActivity(Intent(this, SubjectsActivity::class.java).putExtra(SubjectsActivity.AGE, age))
        }
        return card
    }
}
