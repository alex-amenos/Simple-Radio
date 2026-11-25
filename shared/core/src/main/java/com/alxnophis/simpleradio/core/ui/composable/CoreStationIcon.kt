package com.alxnophis.simpleradio.core.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

@Composable
fun CoreStationIcon(
    faviconUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(size / 4)
    if (faviconUrl.isNullOrBlank()) {
        StationIconFallback(size = size, shape = shape, modifier = modifier)
    } else {
        val imageLoaded = androidx.compose.runtime.remember(faviconUrl) { androidx.compose.runtime.mutableStateOf(false) }
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            if (!imageLoaded.value) {
                StationIconFallback(size = size, shape = shape)
            }
            AsyncImage(
                model = faviconUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onLoading = { imageLoaded.value = false },
                onSuccess = { imageLoaded.value = true },
                onError = { imageLoaded.value = false },
                modifier =
                    Modifier
                        .size(size)
                        .clip(shape),
            )
        }
    }
}

@Composable
private fun StationIconFallback(
    size: Dp,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Radio,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}
