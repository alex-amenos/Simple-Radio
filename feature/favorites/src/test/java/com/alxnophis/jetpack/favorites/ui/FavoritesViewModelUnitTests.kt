package com.alxnophis.jetpack.favorites.ui

import app.cash.turbine.test
import com.alxnophis.jetpack.favorites.domain.usecase.ObserveFavoriteStationsUseCase
import com.alxnophis.jetpack.favorites.domain.usecase.RemoveFavoriteStationUseCase
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesEvent
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesStatus
import com.alxnophis.jetpack.favorites.ui.viewmodel.FavoritesViewModel
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
internal class FavoritesViewModelUnitTests : BaseViewModelUnitTest() {
    private val repository = FakeFavoriteStationsRepository()

    @Test
    fun `GIVEN no favorites WHEN view model is initialized THEN status is empty`() =
        runTest {
            val viewModel = viewModelMother()

            viewModel.uiState.test {
                awaitItem().status shouldBeEqualTo FavoritesStatus.Loading
                testScheduler.advanceUntilIdle()
                val state = awaitItem()
                state.status shouldBeEqualTo FavoritesStatus.Empty
                state.stations shouldBeEqualTo emptyList()
            }
        }

    @Test
    fun `GIVEN persisted favorites WHEN view model is initialized THEN content with stations is shown`() =
        runTest {
            repository.stations.value = listOf(station("1"), station("2"))
            val viewModel = viewModelMother()

            viewModel.uiState.test {
                awaitItem().status shouldBeEqualTo FavoritesStatus.Loading
                testScheduler.advanceUntilIdle()
                val state = awaitItem()
                state.status shouldBeEqualTo FavoritesStatus.Content
                state.stations.map { it.id } shouldBeEqualTo listOf("1", "2")
            }
        }

    @Test
    fun `GIVEN a favorite WHEN removed THEN it disappears and empty state is shown`() =
        runTest {
            repository.stations.value = listOf(station("1"))
            val viewModel = viewModelMother()

            viewModel.uiState.test {
                awaitItem().status shouldBeEqualTo FavoritesStatus.Loading
                testScheduler.advanceUntilIdle()
                awaitItem().status shouldBeEqualTo FavoritesStatus.Content

                viewModel.handleEvent(FavoritesEvent.Remove("1"))
                testScheduler.advanceUntilIdle()

                awaitItem().status shouldBeEqualTo FavoritesStatus.Empty
            }
        }

    @Test
    fun `GIVEN no subscribers WHEN view model is created THEN favorites are not observed`() =
        runTest {
            viewModelMother()
            testScheduler.advanceUntilIdle()

            repository.stations.subscriptionCount.value shouldBeEqualTo 0
        }

    private fun viewModelMother() =
        FavoritesViewModel(
            observeFavoriteStations = ObserveFavoriteStationsUseCase(repository),
            removeFavoriteStation = RemoveFavoriteStationUseCase(repository),
        )

    private fun station(id: String) = FavoriteStation(id, "Station $id", "https://example.com/$id", null, "MP3", 128, "Spain")

    private class FakeFavoriteStationsRepository : FavoriteStationsRepository {
        val stations = MutableStateFlow<List<FavoriteStation>>(emptyList())

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
