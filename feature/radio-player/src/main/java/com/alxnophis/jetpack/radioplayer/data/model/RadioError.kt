package com.alxnophis.jetpack.radioplayer.data.model

internal sealed interface RadioError {
    data object NetworkUnavailable : RadioError

    data object ServerError : RadioError
}
