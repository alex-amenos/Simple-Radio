package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiState
import com.alxnophis.jetpack.settings.ui.contract.Theme

internal class SettingsPreviewProvider : PreviewParameterProvider<SettingsUiState> {
    override val values: Sequence<SettingsUiState>
        get() = Theme.entries.asSequence().map { SettingsUiState(themeOption = it) }
}
