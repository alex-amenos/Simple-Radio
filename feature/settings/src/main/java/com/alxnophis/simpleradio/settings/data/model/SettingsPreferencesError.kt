package com.alxnophis.simpleradio.settings.data.model

sealed class SettingsPreferencesError {
    data object Disk : SettingsPreferencesError()

    data object Unexpected : SettingsPreferencesError()
}
