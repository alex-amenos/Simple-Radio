package com.alxnophis.simpleradio.search.ui.model

import androidx.compose.runtime.Immutable
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation

@Immutable
data class SearchStationUiModel(
    val station: FavoriteStation,
    val details: String,
    val isFavorite: Boolean,
)
