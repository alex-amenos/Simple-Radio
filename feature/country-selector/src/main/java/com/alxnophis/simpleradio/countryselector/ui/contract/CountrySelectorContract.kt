package com.alxnophis.simpleradio.countryselector.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.simpleradio.core.ui.viewmodel.UiEvent
import com.alxnophis.simpleradio.core.ui.viewmodel.UiState
import com.alxnophis.simpleradio.countryselector.data.model.Country
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal sealed interface CountrySelectorEvent : UiEvent {
    data class QueryChanged(
        val query: String,
    ) : CountrySelectorEvent

    data object Retry : CountrySelectorEvent
}

@Immutable
internal enum class CountrySelectorStatus {
    Loading,
    Content,
    Empty,
    Error,
}

@optics
@Immutable
internal data class CountrySelectorUiState(
    val status: CountrySelectorStatus,
    val query: String,
    val selectedCountryCode: String?,
    val countries: ImmutableList<Country>,
) : UiState {
    internal companion object {
        val initialState =
            CountrySelectorUiState(
                status = CountrySelectorStatus.Loading,
                query = "",
                selectedCountryCode = null,
                countries = persistentListOf(),
            )
    }
}
