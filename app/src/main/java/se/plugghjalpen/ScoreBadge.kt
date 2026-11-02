package se.plugghjalpen

import android.app.Activity
import android.app.AlertDialog
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

/** Yellow "⭐ 12 rätt idag" box. Long press asks whether to reset. Call [refresh] in onResume. */
class ScoreBadge(private val a: Activity) {
    val text: TextView = Ui.text(a, "", 20f, Ui.TEXT, bold = true)

    /** Row that centres the badge; add this to the layout. */
    val row: LinearLayout = LinearLayout(a)

    init {
        text.gravity = Gravity.CENTER
        text.background = Ui.rounded(a, 0xFFFFF1C4.toInt(), 22)
        text.setTextColor(0xFF6B4E00.toInt())
        text.textSize = 18f
        val tp = Ui.dp(a, 10)
        text.setPadding(Ui.dp(a, 20), tp, Ui.dp(a, 20), tp)
        text.setOnLongClickListener {
            AlertDialog.Builder(a)
                .setTitle("Nollställa?")
                .setMessage("Vill du nollställa antal rätt idag?")
                .setPositiveButton("Nollställ") { _, _ ->
                    Stats.reset(a)
                    refresh()
                }
                .setNegativeButton("Avbryt", null)
                .show()
            true
        }
        row.gravity = Gravity.CENTER
        row.addView(text)
    }

    fun refresh() {
        val n = Stats.todayCount(a)
        text.text = "⭐ $n rätt idag"
        text.contentDescription = "$n rätt på första försöket idag"
    }
}
