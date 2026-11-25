package com.alxnophis.jetpack.search.ui

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.search.data.model.Country
import com.alxnophis.jetpack.search.data.model.SearchError
import com.alxnophis.jetpack.search.data.repository.SearchStationsRepository
import com.alxnophis.jetpack.search.domain.usecase.GetCountriesUseCase
import com.alxnophis.jetpack.search.domain.usecase.ObserveFavoriteIdsUseCase
import com.alxnophis.jetpack.search.domain.usecase.SearchStationsUseCase
import com.alxnophis.jetpack.search.domain.usecase.SetStationFavoriteUseCase
import com.alxnophis.jetpack.search.ui.contract.SearchEvent
import com.alxnophis.jetpack.search.ui.contract.SearchStatus
import com.alxnophis.jetpack.search.ui.viewmodel.SEARCH_DEBOUNCE_MILLIS
import com.alxnophis.jetpack.search.ui.viewmodel.SearchViewModel
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
import com.alxnophis.jetpack.storage.favorites.repository.FavoriteStationsRepository
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModelUnitTests : BaseViewModelUnitTest() {
    private val searchRepository = FakeSearchStationsRepository()
    private val favoritesRepository = FakeFavoriteStationsRepository()

    @Test
    fun `GIVEN stations available WHEN view model is initialized THEN content and countries are loaded`() =
        runTest {
            searchRepository.stations = listOf(station("1")).right()
            searchRepository.countries = listOf(Country("ES", "Spain")).right()

            val viewModel = viewModelMother()
            testScheduler.advanceUntilIdle()

            viewModel.currentUiState.status shouldBeEqualTo SearchStatus.Content
            viewModel.currentUiState.countries shouldBeEqualTo listOf(Country("ES", "Spain"))
        }

    @Test
    fun `GIVEN no results WHEN searching THEN empty status is shown`() =
        runTest {
            searchRepository.stations = emptyList<FavoriteStation>().right()

            val viewModel = viewModelMother()
            testScheduler.advanceUntilIdle()

            viewModel.currentUiState.status shouldBeEqualTo SearchStatus.Empty
        }

    @Test
    fun `GIVEN a failing search WHEN retrying after recovery THEN content is shown`() =
        runTest {
            searchRepository.stations = SearchError.ServerError.left()
            val viewModel = viewModelMother()
            testScheduler.advanceUntilIdle()
            viewModel.currentUiState.status shouldBeEqualTo SearchStatus.Error

            searchRepository.stations = listOf(station("1")).right()
            viewModel.handleEvent(SearchEvent.Retry)
            testScheduler.advanceUntilIdle()

            viewModel.currentUiState.status shouldBeEqualTo SearchStatus.Content
        }

    @Test
    fun `GIVEN typing fast WHEN query changes THEN only the last query is searched after the debounce`() =
        runTest {
            val viewModel = viewModelMother()
            testScheduler.advanceUntilIdle()
            searchRepository.queries.clear()

            viewModel.handleEvent(SearchEvent.QueryChanged("r"))
            testScheduler.advanceTimeBy(SEARCH_DEBOUNCE_MILLIS / 2)
            viewModel.handleEvent(SearchEvent.QueryChanged("rad"))
            testScheduler.advanceUntilIdle()

            searchRepository.queries shouldBeEqualTo listOf<Pair<String, String?>>("rad" to null)
        }

    @Test
    fun `GIVEN a country WHEN selected THEN search runs immediately with its code`() =
        runTest {
            val viewModel = viewModelMother()
            testScheduler.advanceUntilIdle()
            searchRepository.queries.clear()

            viewModel.handleEvent(SearchEvent.CountrySelected(Country("ES", "Spain")))
            testScheduler.runCurrent()

            searchRepository.queries shouldBeEqualTo listOf<Pair<String, String?>>("" to "ES")
        }

    @Test
    fun `GIVEN a station WHEN toggled twice THEN it is added and then removed from favorites`() =
        runTest {
            val station = station("1")
            val viewModel = viewModelMother()
            testScheduler.advanceUntilIdle()

            viewModel.handleEvent(SearchEvent.ToggleFavorite(station))
            testScheduler.advanceUntilIdle()
            viewModel.currentUiState.favoriteIds shouldBeEqualTo setOf("1")

            viewModel.handleEvent(SearchEvent.ToggleFavorite(station))
            testScheduler.advanceUntilIdle()
            viewModel.currentUiState.favoriteIds shouldBeEqualTo emptySet<String>()
        }

    private fun viewModelMother() =
        SearchViewModel(
            getCountries = GetCountriesUseCase(searchRepository),
            searchStations = SearchStationsUseCase(searchRepository),
            observeFavoriteIds = ObserveFavoriteIdsUseCase(favoritesRepository),
            setStationFavorite = SetStationFavoriteUseCase(favoritesRepository),
        )

    private fun station(id: String) = FavoriteStation(id, "Station $id", "https://example.com/$id", null, "MP3", 128, "Spain")

    private class FakeSearchStationsRepository : SearchStationsRepository {
        var countries: Either<SearchError, List<Country>> = emptyList<Country>().right()
        var stations: Either<SearchError, List<FavoriteStation>> = emptyList<FavoriteStation>().right()
        val queries = mutableListOf<Pair<String, String?>>()

        override suspend fun getCountries() = countries

        override suspend fun searchStations(
            name: String,
            countryCode: String?,
        ): Either<SearchError, List<FavoriteStation>> {
            queries += name to countryCode
            return stations
        }
    }

    private class FakeFavoriteStationsRepository : FavoriteStationsRepository {
        private val stations = MutableStateFlow<List<FavoriteStation>>(emptyList())

        override fun observeFavorites(): Flow<List<FavoriteStation>> = stations

        override fun observeFavoriteIds(): Flow<Set<String>> = stations.map { list -> list.map { it.id }.toSet() }

        override suspend fun add(station: FavoriteStation) {
            stations.value = stations.value + station
        }

        override suspend fun remove(stationId: String) {
            stations.value = stations.value.filterNot { it.id == stationId }
        }
    }
}
