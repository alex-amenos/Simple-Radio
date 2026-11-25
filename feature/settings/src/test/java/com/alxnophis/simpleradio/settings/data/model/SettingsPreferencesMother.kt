package com.alxnophis.simpleradio.settings.data.model

import com.alxnophis.simpleradio.settings.data.model.SettingsPreferences.ThemeOptions

internal object SettingsPreferencesMother {
    fun create(themeOption: ThemeOptions = ThemeOptions.SYSTEM) = SettingsPreferences(themeOption = themeOption)
}
