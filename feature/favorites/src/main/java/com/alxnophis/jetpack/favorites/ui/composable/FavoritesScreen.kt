package com.alxnophis.jetpack.favorites.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.composable.CoreEmptyState
import com.alxnophis.jetpack.core.ui.composable.CoreStationIcon
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.favorites.R
import com.alxnophis.jetpack.favorites.ui.composable.provider.FavoritesPreviewProvider
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesEvent
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesStatus
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesUiState
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation

@Composable
internal fun FavoritesScreen(
    uiState: FavoritesUiState,
    onStationSelected: (stations: List<FavoriteStation>, index: Int) -> Unit,
    handleEvent: (FavoritesEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        when (uiState.status) {
            FavoritesStatus.Loading -> {
                Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            FavoritesStatus.Empty -> {
                CoreEmptyState(
                    icon = Icons.Filled.FavoriteBorder,
                    message = stringResource(R.string.favorites_empty),
                    modifier = Modifier.padding(innerPadding),
                )
            }

            FavoritesStatus.Content -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentPadding = PaddingValues(mediumPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(items = uiState.stations, key = { _, station -> station.id }) { index, station ->
                        FavoriteStationItem(
                            station = station,
                            onClick = { onStationSelected(uiState.stations, index) },
                            onRemove = { handleEvent(FavoritesEvent.Remove(station.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteStationItem(
    station: FavoriteStation,
    onClick: () -> Unit,
    onRemove: () -> Unit,
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
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.favorites_cd_remove, station.name),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun FavoritesScreenPreview(
    @PreviewParameter(FavoritesPreviewProvider::class) uiState: FavoritesUiState,
) {
    AppTheme {
        FavoritesScreen(
            uiState = uiState,
            onStationSelected = { _, _ -> },
            handleEvent = {},
        )
    }
}
