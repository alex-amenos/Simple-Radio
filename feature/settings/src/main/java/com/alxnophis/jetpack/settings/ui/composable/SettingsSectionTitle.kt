package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding

@Composable
internal fun SettingsSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = mediumPadding, bottom = mediumPadding / 2, start = mediumPadding, end = mediumPadding)
                .semantics { heading() }
                .testTag(SettingsTags.TAG_SECTION_TITLE_PREFIX + title),
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}

@PreviewLightDark
@Composable
private fun SettingsSectionTitlePreview() {
    AppTheme {
        SettingsSectionTitle(title = "Appearance")
    }
}
