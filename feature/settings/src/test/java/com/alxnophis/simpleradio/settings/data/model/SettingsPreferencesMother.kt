package com.alxnophis.simpleradio.settings.data.model

import com.alxnophis.simpleradio.settings.data.model.SettingsPreferences.ThemeOptions

internal object SettingsPreferencesMother {
    fun create(
        themeOption: ThemeOptions = ThemeOptions.SYSTEM,
        defaultCountryCode: String? = null,
    ) = SettingsPreferences(
        themeOption = themeOption,
        defaultCountryCode = defaultCountryCode,
    )
}
