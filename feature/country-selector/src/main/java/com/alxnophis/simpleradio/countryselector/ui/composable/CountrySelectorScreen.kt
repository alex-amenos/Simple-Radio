package com.alxnophis.simpleradio.countryselector.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alxnophis.simpleradio.core.ui.composable.CoreEmptyState
import com.alxnophis.simpleradio.core.ui.theme.mediumPadding
import com.alxnophis.simpleradio.core.ui.theme.smallPadding
import com.alxnophis.simpleradio.countryselector.R
import com.alxnophis.simpleradio.countryselector.data.model.Country
import com.alxnophis.simpleradio.countryselector.ui.contract.CountrySelectorEvent
import com.alxnophis.simpleradio.countryselector.ui.contract.CountrySelectorStatus
import com.alxnophis.simpleradio.countryselector.ui.contract.CountrySelectorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CountrySelectorScreen(
    uiState: CountrySelectorUiState,
    onCountrySelected: (Country?) -> Unit,
    onBack: () -> Unit,
    handleEvent: (CountrySelectorEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.country_selector_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag(CountrySelectorTags.TAG_BACK_BUTTON),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.country_selector_cd_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { handleEvent(CountrySelectorEvent.QueryChanged(it)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = mediumPadding, vertical = smallPadding)
                        .testTag(CountrySelectorTags.TAG_SEARCH_FIELD),
                label = { Text(text = stringResource(R.string.country_selector_search_hint)) },
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(
                            onClick = { handleEvent(CountrySelectorEvent.QueryChanged("")) },
                            modifier = Modifier.testTag(CountrySelectorTags.TAG_CLEAR_BUTTON),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.country_selector_cd_clear),
                            )
                        }
                    }
                },
                singleLine = true,
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (uiState.status) {
                    CountrySelectorStatus.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    CountrySelectorStatus.Empty -> {
                        CoreEmptyState(
                            icon = Icons.Filled.SearchOff,
                            message = stringResource(R.string.country_selector_empty),
                        )
                    }

                    CountrySelectorStatus.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            CoreEmptyState(
                                icon = Icons.Filled.CloudOff,
                                message = stringResource(R.string.country_selector_error),
                                modifier = Modifier.weight(1f),
                            )
                            Button(onClick = { handleEvent(CountrySelectorEvent.Retry) }) {
                                Text(text = stringResource(R.string.country_selector_retry))
                            }
                            Spacer(modifier = Modifier.height(mediumPadding))
                        }
                    }

                    CountrySelectorStatus.Content -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            if (uiState.query.isBlank()) {
                                item(key = "all_countries", contentType = "special") {
                                    CountryRow(
                                        name = stringResource(R.string.country_selector_all_countries),
                                        code = null,
                                        isSelected = uiState.selectedCountryCode == null,
                                        onClick = { onCountrySelected(null) },
                                        modifier = Modifier.testTag(CountrySelectorTags.TAG_ALL_COUNTRIES),
                                    )
                                    HorizontalDivider()
                                }
                            }
                            items(
                                items = uiState.countries,
                                key = { it.code },
                                contentType = { "country" },
                            ) { country ->
                                CountryRow(
                                    name = country.name,
                                    code = country.code,
                                    isSelected = uiState.selectedCountryCode == country.code,
                                    onClick = { onCountrySelected(country) },
                                    modifier = Modifier.testTag(CountrySelectorTags.TAG_COUNTRY_ITEM_PREFIX + country.code),
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryRow(
    name: String,
    code: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = mediumPadding, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (code != null) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
