package com.alxnophis.simpleradio.settings.ui.mapper

import com.alxnophis.simpleradio.settings.data.model.SettingsPreferences
import com.alxnophis.simpleradio.settings.ui.contract.Theme

internal fun SettingsPreferences.ThemeOptions.map(): Theme =
    when (this) {
        SettingsPreferences.ThemeOptions.LIGHT -> Theme.LIGHT
        SettingsPreferences.ThemeOptions.DARK -> Theme.DARK
        SettingsPreferences.ThemeOptions.SYSTEM -> Theme.SYSTEM
    }
