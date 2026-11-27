package com.alxnophis.simpleradio.root.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.simpleradio.core.connectivity.NetworkMonitor
import com.alxnophis.simpleradio.root.ui.contract.RootUiState
import com.alxnophis.simpleradio.settings.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class RootViewModel(
    settingsRepository: SettingsRepository,
    networkMonitor: NetworkMonitor,
    initialState: RootUiState = RootUiState.initialState,
) : ViewModel() {
    val uiState: StateFlow<RootUiState> =
        combine(
            settingsRepository.getSettingsFlow(),
            networkMonitor.isOnline,
        ) { settings, isOnline ->
            RootUiState(
                themeOption = settings.themeOption,
                isOnline = isOnline,
                isReady = true,
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = initialState,
            started = SharingStarted.Eagerly,
        )
}
