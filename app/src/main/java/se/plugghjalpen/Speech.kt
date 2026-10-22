package se.plugghjalpen

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Text-to-speech in Swedish (questions and feedback) and British English (the glosor). */
object Speech : TextToSpeech.OnInitListener {
    val SWEDISH: Locale = Locale("sv", "SE")
    val ENGLISH: Locale = Locale.UK

    private var tts: TextToSpeech? = null
    private var ready = false
    private var failed = false
    private var pending: Pair<String, Locale>? = null
    private var warnedMissing = false
    private var counter = 0

    fun init(ctx: Context) {
        if (tts == null) tts = TextToSpeech(ctx.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ready = true
            tts?.setSpeechRate(0.9f)
            pending?.let { (text, locale) -> say(text, locale) }
        } else {
            failed = true
        }
        pending = null
    }

    /** Speaks [text], interrupting whatever is being said. Warns once if the voice is missing. */
    fun speak(a: Activity, text: String, locale: Locale = SWEDISH) {
        init(a)
        if (failed) {
            warn(a)
            return
        }
        if (!ready) {
            pending = text to locale
            return
        }
        if (!say(text, locale)) warn(a)
    }

    fun stop() {
        pending = null
        tts?.stop()
    }

    private fun say(text: String, locale: Locale): Boolean {
        val t = tts ?: return false
        val r = t.setLanguage(locale)
        if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) return false
        t.speak(text, TextToSpeech.QUEUE_FLUSH, null, "u" + (++counter))
        return true
    }

    private fun warn(a: Activity) {
        if (warnedMissing || a.isFinishing) return
        warnedMissing = true
        AlertDialog.Builder(a)
            .setTitle("Rösten saknas")
            .setMessage("Telefonen kan inte läsa upp text på svenska eller engelska just nu. " +
                "Installera rösten under Inställningar → Text till tal (t.ex. Google Talsyntes).")
            .setPositiveButton("Installera") { _, _ ->
                try {
                    a.startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
                } catch (_: Exception) {
                }
            }
            .setNegativeButton("Senare", null)
            .show()
    }
}
