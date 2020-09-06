package com.victorsmind.launchonwakeup

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.victorsmind.launchonwakeup.Settings.Companion.DEFAULT_TIME_DELAY

class Storage(context: Context) {
    private val settings: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            context
        )
    }
    private val editor by lazy { settings.edit() }

    fun getSettings(): Settings = Settings(
        settings.getString(AUTO_START_PACKAGE_KEY, null),
        settings.getString(LAUNCHER_PACKAGE_KEY, null),
        settings.getInt(TIME_DELAY_KEY, DEFAULT_TIME_DELAY)
    )

    fun setSettings(settings: Settings) {
        editor.putString(AUTO_START_PACKAGE_KEY, settings.autoStartApp)
        editor.putString(LAUNCHER_PACKAGE_KEY, settings.launcherApp)
        editor.putInt(TIME_DELAY_KEY, settings.timeDelay)
        editor.apply()
    }

    companion object {
        const val AUTO_START_PACKAGE_KEY = "autoStartPackageKey"
        const val LAUNCHER_PACKAGE_KEY = "launcherPackageKey"
        const val TIME_DELAY_KEY = "timeDelay"
    }
}