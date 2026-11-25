package com.alxnophis.simpleradio.settings.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.alxnophis.simpleradio.core.ui.theme.mediumPadding
import com.alxnophis.simpleradio.settings.R

@Composable
internal fun SettingsCountryItem(
    selectedCountryName: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    SettingsItem(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .clickable(
                        onClick = onClick,
                        onClickLabel = stringResource(R.string.settings_cd_select_country),
                    ).padding(mediumPadding)
                    .testTag(SettingsTags.TAG_SELECT_COUNTRY),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.settings_option_default_country),
            )
            Text(
                modifier =
                    Modifier
                        .weight(1f)
                        .testTag(SettingsTags.TAG_COUNTRY),
                text = selectedCountryName ?: stringResource(R.string.settings_country_all),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )
        }
    }
}
