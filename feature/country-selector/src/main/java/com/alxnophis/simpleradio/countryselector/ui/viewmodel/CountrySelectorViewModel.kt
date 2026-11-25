package com.alxnophis.simpleradio.countryselector.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.simpleradio.countryselector.data.model.Country
import com.alxnophis.simpleradio.countryselector.data.repository.CountrySelectorRepository
import com.alxnophis.simpleradio.countryselector.ui.contract.CountrySelectorEvent
import com.alxnophis.simpleradio.countryselector.ui.contract.CountrySelectorStatus
import com.alxnophis.simpleradio.countryselector.ui.contract.CountrySelectorUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CountrySelectorViewModel(
    private val repository: CountrySelectorRepository,
    private val initialCountryCode: String? = null,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            CountrySelectorUiState.initialState.copy(selectedCountryCode = initialCountryCode),
        )
    val uiState: StateFlow<CountrySelectorUiState> = _uiState.asStateFlow()

    private var allCountries: List<Country> = emptyList()

    init {
        loadCountries()
    }

    fun handleEvent(event: CountrySelectorEvent) {
        when (event) {
            is CountrySelectorEvent.QueryChanged -> filterCountries(event.query)
            CountrySelectorEvent.Retry -> loadCountries()
        }
    }

    private fun loadCountries() {
        _uiState.update { it.copy(status = CountrySelectorStatus.Loading) }
        viewModelScope.launch {
            repository
                .getCountries()
                .fold(
                    {
                        _uiState.update { it.copy(status = CountrySelectorStatus.Error) }
                    },
                    { loaded ->
                        allCountries = loaded
                        filterCountries(_uiState.value.query)
                    },
                )
        }
    }

    private fun filterCountries(query: String) {
        val filtered =
            if (query.isBlank()) {
                allCountries
            } else {
                val trimmed = query.trim()
                allCountries.filter { country ->
                    country.name.contains(trimmed, ignoreCase = true) || country.code.contains(trimmed, ignoreCase = true)
                }
            }

        _uiState.update { current ->
            current.copy(
                query = query,
                countries = filtered.toImmutableList(),
                status = if (filtered.isEmpty() && allCountries.isNotEmpty()) CountrySelectorStatus.Empty else CountrySelectorStatus.Content,
            )
        }
    }
}
