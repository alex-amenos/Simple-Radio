@file:Suppress("ktlint:standard:filename")

package com.alxnophis.jetpack.home.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState

@Immutable
internal enum class HomeTab {
    Favorites,
    Search,
    Settings,
}

internal sealed interface HomeEvent : UiEvent {
    data class SelectTab(
        val tab: HomeTab,
    ) : HomeEvent
}

@optics
@Immutable
internal data class HomeUiState(
    val selectedTab: HomeTab = HomeTab.Favorites,
) : UiState {
    internal companion object {
        val initialState = HomeUiState()
    }
}
