package com.alxnophis.simpleradio.settings.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.simpleradio.settings.ui.composable.SettingsScreen
import com.alxnophis.simpleradio.settings.ui.contract.SettingsUiEvent
import com.alxnophis.simpleradio.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsFeature(
    onSelectCountry: (currentCountryCode: String?) -> Unit = {},
    selectedCountry: Pair<String?, String?>? = null,
    hasNewCountrySelection: Boolean = false,
    onCountrySelectionConsumed: () -> Unit = {},
) {
    val viewModel = koinViewModel<SettingsViewModel>()
    LaunchedEffect(hasNewCountrySelection, selectedCountry) {
        if (hasNewCountrySelection) {
            viewModel.handleEvent(
                SettingsUiEvent.SetDefaultCountry(
                    countryCode = selectedCountry?.first,
                    countryName = selectedCountry?.second,
                ),
            )
            onCountrySelectionConsumed()
        }
    }
    SettingsScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onSelectCountry = { onSelectCountry(viewModel.uiState.value.defaultCountryCode) },
        onEvent = viewModel::handleEvent,
    )
}
