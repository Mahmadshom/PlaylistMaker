package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("theme_setting_switch", Context.MODE_PRIVATE)
        val darkTheme = sharedPreferences.getBoolean("darkTheme", false)
        switchTheme(darkTheme)
    }


    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        val sharedPreferences = getSharedPreferences("theme_setting_switch", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean("darkTheme", darkThemeEnabled)
            .apply()
    }
}