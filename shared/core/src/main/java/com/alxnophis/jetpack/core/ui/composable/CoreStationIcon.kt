package com.alxnophis.jetpack.core.ui.composable

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
import coil.compose.SubcomposeAsyncImage

@Composable
fun CoreStationIcon(
    faviconUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(size / 4)
    if (faviconUrl == null) {
        StationIconFallback(size = size, shape = shape, modifier = modifier)
    } else {
        SubcomposeAsyncImage(
            model = faviconUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                modifier
                    .size(size)
                    .clip(shape),
            loading = { StationIconFallback(size = size, shape = shape) },
            error = { StationIconFallback(size = size, shape = shape) },
        )
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
