package com.alxnophis.simpleradio.search.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.alxnophis.simpleradio.core.ui.composable.CoreEmptyState
import com.alxnophis.simpleradio.core.ui.composable.CoreStationIcon
import com.alxnophis.simpleradio.core.ui.theme.AppTheme
import com.alxnophis.simpleradio.core.ui.theme.mediumPadding
import com.alxnophis.simpleradio.core.ui.theme.smallPadding
import com.alxnophis.simpleradio.search.R
import com.alxnophis.simpleradio.search.data.model.Country
import com.alxnophis.simpleradio.search.ui.composable.provider.SearchPreviewProvider
import com.alxnophis.simpleradio.search.ui.contract.SearchEvent
import com.alxnophis.simpleradio.search.ui.contract.SearchStatus
import com.alxnophis.simpleradio.search.ui.contract.SearchUiState
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation

@Composable
internal fun SearchScreen(
    uiState: SearchUiState,
    onStationSelected: (stations: List<FavoriteStation>, index: Int) -> Unit,
    handleEvent: (SearchEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            SearchFilters(
                query = uiState.query,
                selectedCountry = uiState.selectedCountry,
                countries = uiState.countries,
                handleEvent = handleEvent,
            )
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                SearchResults(uiState = uiState, onStationSelected = onStationSelected, handleEvent = handleEvent)
            }
        }
    }
}

@Composable
private fun SearchFilters(
    query: String,
    selectedCountry: Country?,
    countries: List<Country>,
    handleEvent: (SearchEvent) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = mediumPadding, vertical = smallPadding)) {
        OutlinedTextField(
            value = query,
            onValueChange = { handleEvent(SearchEvent.QueryChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.search_name_label)) },
            shape = SearchFieldShape,
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { handleEvent(SearchEvent.QueryChanged("")) }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = stringResource(R.string.search_cd_clear))
                    }
                }
            },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(smallPadding))
        CountryPicker(
            selectedCountry = selectedCountry,
            countries = countries,
            onCountrySelected = { handleEvent(SearchEvent.CountrySelected(it)) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryPicker(
    selectedCountry: Country?,
    countries: List<Country>,
    onCountrySelected: (Country?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val allCountries = stringResource(R.string.search_country_all)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedCountry?.name ?: allCountries,
            onValueChange = {},
            modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            shape = SearchFieldShape,
            label = { Text(text = stringResource(R.string.search_country_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 360.dp),
        ) {
            DropdownMenuItem(
                text = { Text(text = allCountries) },
                onClick = {
                    expanded = false
                    onCountrySelected(null)
                },
            )
            countries.forEach { country ->
                DropdownMenuItem(
                    text = { Text(text = country.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    onClick = {
                        expanded = false
                        onCountrySelected(country)
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchResults(
    uiState: SearchUiState,
    onStationSelected: (stations: List<FavoriteStation>, index: Int) -> Unit,
    handleEvent: (SearchEvent) -> Unit,
) {
    when (uiState.status) {
        SearchStatus.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        SearchStatus.Empty -> {
            CoreEmptyState(
                icon = Icons.Filled.SearchOff,
                message = stringResource(R.string.search_empty),
            )
        }

        SearchStatus.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CoreEmptyState(
                    icon = Icons.Filled.CloudOff,
                    message = stringResource(R.string.search_error),
                    modifier = Modifier.weight(1f),
                )
                Button(onClick = { handleEvent(SearchEvent.Retry) }) {
                    Text(text = stringResource(R.string.search_retry))
                }
                Spacer(modifier = Modifier.height(mediumPadding))
            }
        }

        SearchStatus.Content -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(mediumPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(items = uiState.stations, key = { _, station -> station.id }) { index, station ->
                    SearchStationItem(
                        station = station,
                        isFavorite = station.id in uiState.favoriteIds,
                        onClick = { onStationSelected(uiState.stations, index) },
                        onToggleFavorite = { handleEvent(SearchEvent.ToggleFavorite(station)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchStationItem(
    station: FavoriteStation,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, top = 8.dp, end = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoreStationIcon(faviconUrl = station.faviconUrl, size = 48.dp)
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val details = listOfNotNull(station.country, station.codec, station.bitrate?.let { "$it kbps" }).joinToString(" · ")
                if (details.isNotEmpty()) {
                    Text(
                        text = details,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription =
                        stringResource(
                            if (isFavorite) R.string.search_cd_remove_favorite else R.string.search_cd_add_favorite,
                            station.name,
                        ),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(SearchPreviewProvider::class) uiState: SearchUiState,
) {
    AppTheme {
        SearchScreen(
            uiState = uiState,
            onStationSelected = { _, _ -> },
            handleEvent = {},
        )
    }
}

private val SearchFieldShape = RoundedCornerShape(16.dp)
