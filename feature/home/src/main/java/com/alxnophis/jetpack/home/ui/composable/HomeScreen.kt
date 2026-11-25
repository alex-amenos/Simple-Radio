package com.alxnophis.jetpack.home.ui.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.home.R
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeTab
import com.alxnophis.jetpack.home.ui.contract.HomeUiState

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    handleEvent: (HomeEvent) -> Unit,
    favoritesContent: @Composable () -> Unit,
    searchContent: @Composable () -> Unit,
    settingsContent: @Composable (onBack: () -> Unit) -> Unit,
    onBack: () -> Unit,
) {
    val goToFirstTab = { handleEvent(HomeEvent.SelectTab(HomeTab.Favorites)) }
    BackHandler {
        if (uiState.selectedTab == HomeTab.Favorites) onBack() else goToFirstTab()
    }
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                NavigationBar(modifier = Modifier.testTag(HomeTags.TAG_HOME_BOTTOM_BAR)) {
                    HomeTab.entries.forEach { tab ->
                        val label = stringResource(tab.labelRes())
                        NavigationBarItem(
                            selected = uiState.selectedTab == tab,
                            onClick = { handleEvent(HomeEvent.SelectTab(tab)) },
                            icon = { Icon(imageVector = tab.icon(), contentDescription = null) },
                            label = { Text(text = label) },
                        )
                    }
                }
            },
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .fillMaxSize(),
            ) {
                when (uiState.selectedTab) {
                    HomeTab.Favorites -> favoritesContent()
                    HomeTab.Search -> searchContent()
                    HomeTab.Settings -> settingsContent(goToFirstTab)
                }
            }
        }
    }
}

private fun HomeTab.labelRes(): Int =
    when (this) {
        HomeTab.Favorites -> R.string.home_tab_favorites
        HomeTab.Search -> R.string.home_tab_search
        HomeTab.Settings -> R.string.home_tab_settings
    }

private fun HomeTab.icon(): ImageVector =
    when (this) {
        HomeTab.Favorites -> Icons.Filled.Favorite
        HomeTab.Search -> Icons.Filled.Search
        HomeTab.Settings -> Icons.Filled.Settings
    }

@PreviewLightDark
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        uiState = HomeUiState(),
        handleEvent = {},
        favoritesContent = { Text(text = "Favorites") },
        searchContent = { Text(text = "Search") },
        settingsContent = { Text(text = "Settings") },
        onBack = {},
    )
}
