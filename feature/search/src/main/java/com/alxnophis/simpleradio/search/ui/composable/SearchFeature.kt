package com.alxnophis.simpleradio.search.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.simpleradio.search.ui.viewmodel.SearchViewModel
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchFeature(onStationSelected: (stations: List<FavoriteStation>, index: Int) -> Unit) {
    val viewModel = koinViewModel<SearchViewModel>()
    SearchScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onStationSelected = onStationSelected,
        handleEvent = viewModel::handleEvent,
    )
}
