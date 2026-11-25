package com.alxnophis.jetpack.radioplayer.ui.composable

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
internal fun RadioStationIcon(
    faviconUrl: String?,
    size: Dp,
) {
    val shape = RoundedCornerShape(size / 4)
    if (faviconUrl == null) {
        RadioIconFallback(size = size, shape = shape)
    } else {
        SubcomposeAsyncImage(
            model = faviconUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .size(size)
                    .clip(shape),
            loading = { RadioIconFallback(size = size, shape = shape) },
            error = { RadioIconFallback(size = size, shape = shape) },
        )
    }
}

@Composable
private fun RadioIconFallback(
    size: Dp,
    shape: Shape,
) {
    Box(
        modifier =
            Modifier
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
