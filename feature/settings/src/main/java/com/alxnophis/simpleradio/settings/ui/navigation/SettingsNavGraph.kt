package com.alxnophis.simpleradio.settings.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.simpleradio.settings.ui.composable.SettingsScreen
import com.alxnophis.simpleradio.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsFeature() {
    val viewModel = koinViewModel<SettingsViewModel>()
    SettingsScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = viewModel::handleEvent,
    )
}
