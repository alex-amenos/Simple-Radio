package com.alxnophis.simpleradio.search.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.simpleradio.search.data.model.Country
import com.alxnophis.simpleradio.search.ui.contract.SearchStatus
import com.alxnophis.simpleradio.search.ui.contract.SearchUiState
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

internal class SearchPreviewProvider : PreviewParameterProvider<SearchUiState> {
    override val values: Sequence<SearchUiState>
        get() =
            sequenceOf(
                SearchUiState.initialState.copy(
                    status = SearchStatus.Content,
                    query = "ser",
                    selectedCountry = Country("ES", "Spain"),
                    stations =
                        persistentListOf(
                            FavoriteStation("1", "Cadena SER", "https://example.com/ser", null, "MP3", 128, "Spain"),
                            FavoriteStation("2", "Los 40", "https://example.com/40", null, "AAC", 96, "Spain"),
                        ),
                    favoriteIds = persistentSetOf("1"),
                ),
                SearchUiState.initialState.copy(status = SearchStatus.Empty, query = "zzz"),
            )
}
