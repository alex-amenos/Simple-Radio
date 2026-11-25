package com.alxnophis.jetpack.home.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.home.ui.contract.HomeUiState
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Home shell with the bottom navigation bar. Tab contents are provided by the caller so that
 * this module does not depend on the other feature modules.
 */
@Composable
fun HomeFeature(
    favoritesContent: @Composable () -> Unit,
    searchContent: @Composable () -> Unit,
    settingsContent: @Composable (onBack: () -> Unit) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<HomeViewModel>()
    val uiState: HomeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        handleEvent = viewModel::handleEvent,
        favoritesContent = favoritesContent,
        searchContent = searchContent,
        settingsContent = settingsContent,
        onBack = onBack,
    )
}
