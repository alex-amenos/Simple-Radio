package com.alxnophis.jetpack.settings.data.model

import com.alxnophis.jetpack.settings.data.model.SettingsPreferences.ThemeOptions

internal object SettingsPreferencesMother {
    fun create(themeOption: ThemeOptions = ThemeOptions.SYSTEM) = SettingsPreferences(themeOption = themeOption)
}
