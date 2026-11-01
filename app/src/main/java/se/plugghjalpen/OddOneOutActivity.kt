package se.plugghjalpen

import kotlin.random.Random

/** 4+ "Vilken passar inte?": three things belong together, one does not. */
class OddOneOutActivity : QuizActivity() {
    private var task: Kids.OddTask? = null

    override val screenTitle = "Vilken passar inte?"

    override fun makeGrid() = OptionGrid(this, 1f, 1f, Ui.dp(this, 14))

    override fun newQuestion() {
        val t = Kids.oddTask(Random.Default, avoidGroup = task?.group)
        task = t
        questionArea.addView(speakerButton("Vilken passar inte?", 26f))
        for (o in t.options) addOption(Ui.emoji(this, o.emoji, 110), o === t.odd, o.name, o.name)
    }

    override fun speakQuestion() {
        Speech.speak(this, "Vilken passar inte ihop med de andra?")
    }

    override fun praiseExtra() = task?.group?.reason
}
