package se.plugghjalpen

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Speech.init(this) // start the voice early so it is ready for the first question
    }
}
