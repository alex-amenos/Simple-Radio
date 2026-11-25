package com.alxnophis.simpleradio.settings.ui.contract

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.simpleradio.core.ui.viewmodel.UiEvent
import com.alxnophis.simpleradio.core.ui.viewmodel.UiState
import com.alxnophis.simpleradio.settings.R
import com.alxnophis.simpleradio.settings.data.model.SettingsPreferences

internal sealed class SettingsUiEvent : UiEvent {
    data class SetTheme(
        val theme: Theme,
    ) : SettingsUiEvent()
}

@optics
@Immutable
internal data class SettingsUiState(
    val themeOption: Theme,
) : UiState {
    internal companion object {
        val initialState =
            SettingsUiState(
                themeOption = Theme.SYSTEM,
            )
    }
}

internal enum class Theme(
    @StringRes val label: Int,
) {
    LIGHT(R.string.settings_light),
    DARK(R.string.settings_dark),
    SYSTEM(R.string.settings_system),
    ;

    fun map(): SettingsPreferences.ThemeOptions =
        when (this) {
            LIGHT -> SettingsPreferences.ThemeOptions.LIGHT
            DARK -> SettingsPreferences.ThemeOptions.DARK
            SYSTEM -> SettingsPreferences.ThemeOptions.SYSTEM
        }
}
