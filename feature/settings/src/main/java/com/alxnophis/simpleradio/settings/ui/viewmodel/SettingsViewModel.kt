package com.alxnophis.simpleradio.settings.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.simpleradio.settings.data.repository.SettingsRepository
import com.alxnophis.simpleradio.settings.ui.contract.SettingsUiEvent
import com.alxnophis.simpleradio.settings.ui.contract.SettingsUiState
import com.alxnophis.simpleradio.settings.ui.contract.Theme
import com.alxnophis.simpleradio.settings.ui.mapper.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    initialState: SettingsUiState = SettingsUiState.initialState,
) : ViewModel() {
    val uiState =
        settingsRepository
            .getSettingsFlow()
            .map { SettingsUiState(themeOption = it.themeOption.map()) }
            .stateIn(
                scope = viewModelScope,
                initialValue = initialState,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            )

    fun handleEvent(event: SettingsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsUiEvent.SetTheme -> setTheme(event.theme)
            }
        }
    }

    private suspend fun setTheme(theme: Theme) {
        settingsRepository.updateThemeOption(theme.map())
    }
}
