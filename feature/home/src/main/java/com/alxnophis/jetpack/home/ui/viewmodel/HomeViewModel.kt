package com.alxnophis.jetpack.home.ui.viewmodel

import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeUiState
import com.alxnophis.jetpack.home.ui.contract.selectedTab

internal class HomeViewModel(
    initialUiState: HomeUiState = HomeUiState.initialState,
) : BaseViewModel<HomeEvent, HomeUiState>(initialUiState) {
    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SelectTab -> updateUiState { copy { HomeUiState.selectedTab set event.tab } }
        }
    }
}
