package com.alxnophis.simpleradio.search.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.simpleradio.search.data.model.Country
import com.alxnophis.simpleradio.search.ui.contract.SearchEvent
import com.alxnophis.simpleradio.search.ui.viewmodel.SearchViewModel
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchFeature(
    onStationSelected: (stations: List<FavoriteStation>, index: Int) -> Unit,
    onSelectCountry: (currentCountryCode: String?) -> Unit = {},
    selectedCountry: Country? = null,
    hasNewCountrySelection: Boolean = false,
    onCountrySelectionConsumed: () -> Unit = {},
) {
    val viewModel = koinViewModel<SearchViewModel>()
    LaunchedEffect(hasNewCountrySelection, selectedCountry) {
        if (hasNewCountrySelection) {
            viewModel.handleEvent(SearchEvent.CountrySelected(selectedCountry))
            onCountrySelectionConsumed()
        }
    }
    SearchScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onStationSelected = onStationSelected,
        onSelectCountry = {
            onSelectCountry(
                viewModel.uiState.value.selectedCountry
                    ?.code,
            )
        },
        handleEvent = viewModel::handleEvent,
    )
}
