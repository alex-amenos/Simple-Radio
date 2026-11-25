package com.alxnophis.jetpack.search.data.model

internal sealed interface SearchError {
    data object NetworkUnavailable : SearchError

    data object ServerError : SearchError
}
