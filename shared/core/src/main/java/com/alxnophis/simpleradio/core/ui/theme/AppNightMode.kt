package com.alxnophis.simpleradio.core.ui.theme

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

enum class AppNightMode {
    LIGHT,
    DARK,
    SYSTEM,
}

object AppNightModeManager {
    private const val PREFERENCES_NAME = "app_night_mode"
    private const val KEY_NIGHT_MODE = "night_mode"

    fun save(
        context: Context,
        nightMode: AppNightMode,
    ) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_NIGHT_MODE, nightMode.name)
        }
    }

    fun applySaved(context: Context) {
        val nightMode =
            context
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(KEY_NIGHT_MODE, null)
                ?.let { name -> AppNightMode.entries.firstOrNull { it.name == name } }
                ?: AppNightMode.SYSTEM
        apply(context, nightMode)
    }

    fun apply(
        context: Context,
        nightMode: AppNightMode,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(UiModeManager::class.java).setApplicationNightMode(nightMode.toUiModeNightMode())
        } else {
            AppCompatDelegate.setDefaultNightMode(nightMode.toAppCompatNightMode())
        }
    }

    private fun AppNightMode.toUiModeNightMode(): Int =
        when (this) {
            AppNightMode.LIGHT -> UiModeManager.MODE_NIGHT_NO
            AppNightMode.DARK -> UiModeManager.MODE_NIGHT_YES
            AppNightMode.SYSTEM -> UiModeManager.MODE_NIGHT_AUTO
        }

    private fun AppNightMode.toAppCompatNightMode(): Int =
        when (this) {
            AppNightMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppNightMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppNightMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
}
