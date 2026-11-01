package se.plugghjalpen

import kotlin.random.Random

/** 4+ "Former": where is the triangle? Colours are random, so the colour gives no hint. */
class ShapeActivity : QuizActivity() {
    private var target: ShapeKind? = null

    private val colors = listOf(
        0xFFE0453A.toInt(), 0xFF2F6FDE.toInt(), 0xFF2E9E5B.toInt(),
        0xFFFF9F1C.toInt(), 0xFF8E5BB5.toInt(), 0xFFE85D9C.toInt()
    )

    override val screenTitle = "Former"

    override fun makeGrid() = OptionGrid(this, 1f, 1f, Ui.dp(this, 14))

    override fun newQuestion() {
        var t = ShapeKind.values().random()
        while (t == target) t = ShapeKind.values().random()
        target = t
        questionArea.addView(speakerButton(t.label.replaceFirstChar { it.uppercase() }, 32f))
        val others = ShapeKind.values().filter { it != t }.shuffled().take(2)
        val cols = colors.shuffled()
        (others + t).shuffled().forEachIndexed { i, k ->
            val v = ShapeView(this)
            v.set(k, cols[i])
            addOption(v, k == t, k.label, k.label)
        }
    }

    override fun speakQuestion() {
        target?.let { Speech.speak(this, "Var är ${it.definite}?") }
    }
}
