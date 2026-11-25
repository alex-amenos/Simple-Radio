package com.alxnophis.simpleradio.favorites.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.alxnophis.simpleradio.core.ui.composable.CoreEmptyState
import com.alxnophis.simpleradio.core.ui.theme.AppTheme
import com.alxnophis.simpleradio.core.ui.theme.mediumPadding
import com.alxnophis.simpleradio.favorites.R
import com.alxnophis.simpleradio.favorites.ui.composable.provider.FavoritesPreviewProvider
import com.alxnophis.simpleradio.favorites.ui.contract.FavoritesEvent
import com.alxnophis.simpleradio.favorites.ui.contract.FavoritesStatus
import com.alxnophis.simpleradio.favorites.ui.contract.FavoritesUiState
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation

@Composable
internal fun FavoritesScreen(
    uiState: FavoritesUiState,
    onStationSelected: (stations: List<FavoriteStation>, index: Int) -> Unit,
    handleEvent: (FavoritesEvent) -> Unit,
) {
    var stationIdToConfirm by remember { mutableStateOf<String?>(null) }

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
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentPadding = PaddingValues(mediumPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(items = uiState.stations, key = { _, station -> station.id }) { index, station ->
                        FavoriteStationItem(
                            station = station,
                            isConfirmingRemove = stationIdToConfirm == station.id,
                            onClick = {
                                if (stationIdToConfirm == station.id) {
                                    stationIdToConfirm = null
                                } else {
                                    onStationSelected(uiState.stations, index)
                                }
                            },
                            onRequestRemove = {
                                stationIdToConfirm = station.id
                            },
                            onConfirmRemove = {
                                stationIdToConfirm = null
                                handleEvent(FavoritesEvent.Remove(station.id))
                            },
                            onCancelRemove = {
                                stationIdToConfirm = null
                            },
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
    isConfirmingRemove: Boolean,
    onClick: () -> Unit,
    onRequestRemove: () -> Unit,
    onConfirmRemove: () -> Unit,
    onCancelRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (station.faviconUrl.isNullOrBlank()) {
                StationFallback(modifier = Modifier.fillMaxSize())
            } else {
                SubcomposeAsyncImage(
                    model = station.faviconUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = { StationFallback(modifier = Modifier.fillMaxSize()) },
                    error = { StationFallback(modifier = Modifier.fillMaxSize()) },
                )
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.65f),
                                        Color.Black.copy(alpha = 0.95f),
                                    ),
                            ),
                        ).padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 8.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                    )
                    val details = listOfNotNull(station.country, station.codec, station.bitrate?.let { "$it kbps" }).joinToString(" · ")
                    if (details.isNotEmpty()) {
                        Text(
                            text = details,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }
                }
            }

            if (!isConfirmingRemove) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(bottomEnd = 12.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(onClick = onRequestRemove)
                            .size(36.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.favorites_cd_remove, station.name),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            this@Card.AnimatedVisibility(
                visible = isConfirmingRemove,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onCancelRemove,
                            ).padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.favorites_remove_prompt),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Button(
                                onClick = onConfirmRemove,
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError,
                                    ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = stringResource(R.string.favorites_remove_confirm),
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            OutlinedButton(
                                onClick = onCancelRemove,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = stringResource(R.string.favorites_remove_cancel),
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StationFallback(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Radio,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(48.dp),
        )
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
