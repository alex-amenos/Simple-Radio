package com.alxnophis.jetpack.favorites.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesStatus
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesUiState
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
import kotlinx.collections.immutable.persistentListOf

internal class FavoritesPreviewProvider : PreviewParameterProvider<FavoritesUiState> {
    override val values: Sequence<FavoritesUiState>
        get() =
            sequenceOf(
                FavoritesUiState(
                    status = FavoritesStatus.Content,
                    stations =
                        persistentListOf(
                            FavoriteStation("1", "Cadena SER", "https://example.com/ser", null, "MP3", 128, "Spain"),
                            FavoriteStation("2", "Los 40", "https://example.com/40", null, "AAC", 96, "Spain"),
                        ),
                ),
                FavoritesUiState(status = FavoritesStatus.Empty, stations = persistentListOf()),
            )
}
