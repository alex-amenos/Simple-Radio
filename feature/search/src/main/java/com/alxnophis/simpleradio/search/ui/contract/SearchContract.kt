@file:Suppress("ktlint:standard:filename")

package com.alxnophis.simpleradio.search.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.simpleradio.core.ui.viewmodel.UiEvent
import com.alxnophis.simpleradio.core.ui.viewmodel.UiState
import com.alxnophis.simpleradio.search.data.model.Country
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

internal sealed interface SearchEvent : UiEvent {
    data class QueryChanged(
        val query: String,
    ) : SearchEvent

    data class CountrySelected(
        val country: Country?,
    ) : SearchEvent

    data class ToggleFavorite(
        val station: FavoriteStation,
    ) : SearchEvent

    data object Retry : SearchEvent
}

@Immutable
internal enum class SearchStatus {
    Loading,
    Content,
    Empty,
    Error,
}

@optics
@Immutable
internal data class SearchUiState(
    val status: SearchStatus,
    val query: String,
    val selectedCountry: Country?,
    val countries: ImmutableList<Country>,
    val stations: ImmutableList<FavoriteStation>,
    val favoriteIds: ImmutableSet<String>,
) : UiState {
    internal companion object {
        val initialState =
            SearchUiState(
                status = SearchStatus.Loading,
                query = "",
                selectedCountry = null,
                countries = persistentListOf(),
                stations = persistentListOf(),
                favoriteIds = persistentSetOf(),
            )
    }
}
