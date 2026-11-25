package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.core.extensions.getVersion
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiState

@Composable
internal fun SettingsScreen(
    state: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit = {},
    appVersion: String = LocalContext.current.getVersion(),
) {
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .verticalScroll(rememberScrollState()),
            ) {
                SettingsThemeItem(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTheme = state.themeOption,
                    onOptionSelected = { theme ->
                        onEvent(SettingsUiEvent.SetTheme(theme))
                    },
                )
                SettingsSectionSpacer(
                    modifier = Modifier.fillMaxWidth(),
                )
                SettingsAppVersion(
                    modifier = Modifier.fillMaxWidth(),
                    appVersion = appVersion,
                )
                HorizontalDivider()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsPreviewProvider::class) uiState: SettingsUiState,
) {
    SettingsScreen(
        state = uiState,
        appVersion = "1.0.0",
    )
}
