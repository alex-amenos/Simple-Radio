package com.alxnophis.simpleradio.settings.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.simpleradio.settings.ui.contract.SettingsUiState
import com.alxnophis.simpleradio.settings.ui.contract.Theme

internal class SettingsPreviewProvider : PreviewParameterProvider<SettingsUiState> {
    override val values: Sequence<SettingsUiState>
        get() =
            sequenceOf(
                SettingsUiState(
                    themeOption = Theme.DARK,
                ),
            )
}
