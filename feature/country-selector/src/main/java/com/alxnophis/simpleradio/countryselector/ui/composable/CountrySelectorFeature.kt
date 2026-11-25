package com.alxnophis.simpleradio.countryselector.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.simpleradio.countryselector.data.model.Country
import com.alxnophis.simpleradio.countryselector.ui.viewmodel.CountrySelectorViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CountrySelectorFeature(
    selectedCountryCode: String?,
    onCountrySelected: (Country?) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<CountrySelectorViewModel> { parametersOf(selectedCountryCode) }
    CountrySelectorScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onCountrySelected = onCountrySelected,
        onBack = onBack,
        handleEvent = viewModel::handleEvent,
    )
}
