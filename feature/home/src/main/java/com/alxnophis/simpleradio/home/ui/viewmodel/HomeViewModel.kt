package com.alxnophis.simpleradio.home.ui.viewmodel

import arrow.optics.copy
import com.alxnophis.simpleradio.core.ui.viewmodel.BaseViewModel
import com.alxnophis.simpleradio.home.ui.contract.HomeEvent
import com.alxnophis.simpleradio.home.ui.contract.HomeUiState
import com.alxnophis.simpleradio.home.ui.contract.selectedTab

internal class HomeViewModel(
    initialUiState: HomeUiState = HomeUiState.initialState,
) : BaseViewModel<HomeEvent, HomeUiState>(initialUiState) {
    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SelectTab -> updateUiState { copy { HomeUiState.selectedTab set event.tab } }
        }
    }
}
