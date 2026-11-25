package com.alxnophis.simpleradio.search.data.model

internal sealed interface SearchError {
    data object NetworkUnavailable : SearchError

    data object ServerError : SearchError
}
