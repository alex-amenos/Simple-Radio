package com.alxnophis.simpleradio.favorites.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.simpleradio.favorites.ui.viewmodel.FavoritesViewModel
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesFeature(onStationSelected: (stations: List<FavoriteStation>, index: Int) -> Unit) {
    val viewModel = koinViewModel<FavoritesViewModel>()
    FavoritesScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onStationSelected = onStationSelected,
        handleEvent = viewModel::handleEvent,
    )
}
