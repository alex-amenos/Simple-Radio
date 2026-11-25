package com.alxnophis.jetpack.search.ui.viewmodel

import android.os.Parcelable
import com.alxnophis.jetpack.search.data.model.Country
import kotlinx.parcelize.Parcelize

/**
 * User input for a search, kept in [androidx.lifecycle.SavedStateHandle] as a single value.
 * [attempt] and [debounce] live here too so each event is one write and one emission.
 */
@Parcelize
internal data class SearchRequest(
    val query: String = "",
    val country: Country? = null,
    val attempt: Int = 0,
    val debounce: Boolean = false,
) : Parcelable
