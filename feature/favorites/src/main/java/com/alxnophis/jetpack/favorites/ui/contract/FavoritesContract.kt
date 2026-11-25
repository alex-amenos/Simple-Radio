@file:Suppress("ktlint:standard:filename")

package com.alxnophis.jetpack.favorites.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal sealed interface FavoritesEvent : UiEvent {
    data class Remove(
        val stationId: String,
    ) : FavoritesEvent
}

@Immutable
internal enum class FavoritesStatus {
    Loading,
    Empty,
    Content,
}

@optics
@Immutable
internal data class FavoritesUiState(
    val status: FavoritesStatus,
    val stations: ImmutableList<FavoriteStation>,
) : UiState {
    internal companion object {
        val initialState =
            FavoritesUiState(
                status = FavoritesStatus.Loading,
                stations = persistentListOf(),
            )
    }
}
