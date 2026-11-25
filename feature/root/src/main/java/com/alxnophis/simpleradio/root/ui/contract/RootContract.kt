package com.alxnophis.simpleradio.root.ui.contract

import androidx.compose.runtime.Immutable
import com.alxnophis.simpleradio.core.ui.viewmodel.UiEvent
import com.alxnophis.simpleradio.core.ui.viewmodel.UiState
import com.alxnophis.simpleradio.settings.data.model.SettingsPreferences

internal sealed interface RootUiEvent : UiEvent

@Immutable
internal data class RootUiState(
    val themeOption: SettingsPreferences.ThemeOptions,
    val isOnline: Boolean,
    val isReady: Boolean = false,
) : UiState {
    companion object {
        val initialState =
            RootUiState(
                themeOption = SettingsPreferences.ThemeOptions.SYSTEM,
                isOnline = true,
                isReady = false,
            )
    }
}
