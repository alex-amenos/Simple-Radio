package com.alxnophis.simpleradio.search.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.simpleradio.search.data.model.Country
import com.alxnophis.simpleradio.search.data.model.SearchError
import com.alxnophis.simpleradio.search.data.repository.SearchStationsRepository
import com.alxnophis.simpleradio.search.domain.usecase.ObserveFavoriteIdsUseCase
import com.alxnophis.simpleradio.search.domain.usecase.SearchStationsUseCase
import com.alxnophis.simpleradio.search.domain.usecase.SetStationFavoriteUseCase
import com.alxnophis.simpleradio.search.ui.contract.SearchEvent
import com.alxnophis.simpleradio.search.ui.contract.SearchStatus
import com.alxnophis.simpleradio.search.ui.viewmodel.KEY_SEARCH_REQUEST
import com.alxnophis.simpleradio.search.ui.viewmodel.SEARCH_DEBOUNCE_MILLIS
import com.alxnophis.simpleradio.search.ui.viewmodel.SearchRequest
import com.alxnophis.simpleradio.search.ui.viewmodel.SearchViewModel
import com.alxnophis.simpleradio.settings.data.model.SettingsPreferences
import com.alxnophis.simpleradio.settings.data.repository.SettingsRepository
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import com.alxnophis.simpleradio.storage.favorites.repository.FavoriteStationsRepository
import com.alxnophis.simpleradio.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModelUnitTests : BaseViewModelUnitTest() {
    private val searchRepository = FakeSearchStationsRepository()
    private val favoritesRepository = FakeFavoriteStationsRepository()

    @Test
    fun `GIVEN stations available WHEN view model is initialized THEN content is shown`() =
        runTest {
            searchRepository.stations = listOf(station("1")).right()

            val viewModel = viewModelMother().subscribed(this)
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.status shouldBeEqualTo SearchStatus.Content
        }

    @Test
    fun `GIVEN stations returned WHEN loaded THEN ui models have precomputed details and favorite state`() =
        runTest {
            val testStation =
                FavoriteStation(
                    id = "1",
                    name = "Radio 1",
                    streamUrl = "https://stream.example.com",
                    faviconUrl = null,
                    codec = "MP3",
                    bitrate = 128,
                    country = "Spain",
                )
            searchRepository.stations = listOf(testStation).right()
            favoritesRepository.add(testStation)

            val viewModel = viewModelMother().subscribed(this)
            testScheduler.advanceUntilIdle()

            val item =
                viewModel.uiState.value.stations
                    .first()
            item.station shouldBeEqualTo testStation
            item.details shouldBeEqualTo "Spain · MP3 · 128 kbps"
            item.isFavorite shouldBeEqualTo true
        }

    @Test
    fun `GIVEN no results WHEN searching THEN empty status is shown`() =
        runTest {
            searchRepository.stations = emptyList<FavoriteStation>().right()

            val viewModel = viewModelMother().subscribed(this)
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.status shouldBeEqualTo SearchStatus.Empty
        }

    @Test
    fun `GIVEN a failing search WHEN retrying after recovery THEN content is shown`() =
        runTest {
            searchRepository.stations = SearchError.ServerError.left()
            val viewModel = viewModelMother().subscribed(this)
            testScheduler.advanceUntilIdle()
            viewModel.uiState.value.status shouldBeEqualTo SearchStatus.Error

            searchRepository.stations = listOf(station("1")).right()
            viewModel.handleEvent(SearchEvent.Retry)
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.status shouldBeEqualTo SearchStatus.Content
        }

    @Test
    fun `GIVEN a saved request WHEN view model is recreated THEN query and country are restored and searched`() =
        runTest {
            searchRepository.stations = listOf(station("1")).right()
            val savedState =
                SavedStateHandle().apply {
                    set(KEY_SEARCH_REQUEST, SearchRequest(query = "jazz", country = Country("FR", "France")))
                }

            val viewModel = viewModelMother(savedState).subscribed(this)
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.query shouldBeEqualTo "jazz"
            viewModel.uiState.value.selectedCountry shouldBeEqualTo Country("FR", "France")
            searchRepository.queries shouldBeEqualTo listOf("jazz" to "FR")
        }

    @Test
    fun `GIVEN a country WHEN selected THEN search runs immediately with its code`() =
        runTest {
            val viewModel = viewModelMother().subscribed(this)
            testScheduler.advanceUntilIdle()

            viewModel.handleEvent(SearchEvent.CountrySelected(Country("ES", "Spain")))
            testScheduler.advanceUntilIdle()

            searchRepository.queries shouldBeEqualTo listOf("" to null, "" to "ES")
            viewModel.uiState.value.selectedCountry shouldBeEqualTo Country("ES", "Spain")
        }

    @Test
    fun `GIVEN typing fast WHEN query changes THEN only the last query is searched after the debounce`() =
        runTest {
            val viewModel = viewModelMother().subscribed(this)
            testScheduler.advanceUntilIdle()

            viewModel.handleEvent(SearchEvent.QueryChanged("a"))
            testScheduler.advanceTimeBy(SEARCH_DEBOUNCE_MILLIS / 2)
            viewModel.handleEvent(SearchEvent.QueryChanged("ab"))
            testScheduler.advanceTimeBy(SEARCH_DEBOUNCE_MILLIS / 2)
            viewModel.handleEvent(SearchEvent.QueryChanged("abc"))
            testScheduler.advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
            testScheduler.advanceUntilIdle()

            searchRepository.queries shouldBeEqualTo listOf("" to null, "abc" to null)
        }

    @Test
    fun `GIVEN a station WHEN toggled twice THEN it is added and then removed from favorites`() =
        runTest {
            val station = station("1")
            val viewModel = viewModelMother().subscribed(this)
            testScheduler.advanceUntilIdle()

            viewModel.handleEvent(SearchEvent.ToggleFavorite(station))
            testScheduler.advanceUntilIdle()
            favoritesRepository.observeFavoriteIds().map { "1" in it }.test {
                awaitItem() shouldBeEqualTo true
            }

            viewModel.handleEvent(SearchEvent.ToggleFavorite(station))
            testScheduler.advanceUntilIdle()
            favoritesRepository.observeFavoriteIds().map { "1" in it }.test {
                awaitItem() shouldBeEqualTo false
            }
        }

    @Test
    fun `GIVEN a query and a country WHEN changed THEN they are saved in the saved state handle`() =
        runTest {
            val savedState = SavedStateHandle()
            val viewModel = viewModelMother(savedState).subscribed(this)

            viewModel.handleEvent(SearchEvent.QueryChanged("rock"))
            viewModel.handleEvent(SearchEvent.CountrySelected(Country("ES", "Spain")))

            val saved = savedState.get<SearchRequest>(KEY_SEARCH_REQUEST)
            saved?.query shouldBeEqualTo "rock"
            saved?.country shouldBeEqualTo Country("ES", "Spain")
        }

    @Test
    fun `GIVEN no subscribers WHEN view model is created THEN nothing is searched`() =
        runTest {
            viewModelMother()
            testScheduler.advanceUntilIdle()

            searchRepository.queries shouldBeEqualTo emptyList<Pair<String, String?>>()
        }

    @Test
    fun `GIVEN default country set in preferences and fresh launch WHEN view model is initialized THEN country is selected from preferences`() =
        runTest {
            val settingsRepository = mock<SettingsRepository>()
            whenever(settingsRepository.getSettingsFlow()).thenReturn(
                flowOf(
                    SettingsPreferences(
                        themeOption = SettingsPreferences.ThemeOptions.SYSTEM,
                        defaultCountryCode = "ES",
                        defaultCountryName = "Spain",
                    ),
                ),
            )
            searchRepository.stations = listOf(station("1")).right()

            val viewModel = viewModelMother(savedStateHandle = SavedStateHandle(), settingsRepository = settingsRepository).subscribed(this)
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.selectedCountry shouldBeEqualTo Country("ES", "Spain")
        }

    private fun viewModelMother(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
        settingsRepository: SettingsRepository? = null,
    ) = SearchViewModel(
        searchStations = SearchStationsUseCase(searchRepository),
        observeFavoriteIds = ObserveFavoriteIdsUseCase(favoritesRepository),
        setStationFavorite = SetStationFavoriteUseCase(favoritesRepository),
        settingsRepository = settingsRepository,
        savedStateHandle = savedStateHandle,
    )

    private fun SearchViewModel.subscribed(testScope: TestScope): SearchViewModel =
        apply { testScope.backgroundScope.launch(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) { uiState.collect {} } }

    private fun station(id: String) = FavoriteStation(id, "Station $id", "https://example.com/$id", null, "MP3", 128, "Spain")

    private class FakeSearchStationsRepository : SearchStationsRepository {
        var stations: Either<SearchError, List<FavoriteStation>> = emptyList<FavoriteStation>().right()
        val queries = mutableListOf<Pair<String, String?>>()

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
