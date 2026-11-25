package com.alxnophis.jetpack.home.domain.model

sealed interface Feature {
    data object Settings : Feature

    data object RadioStreaming : Feature
}
