package com.alxnophis.simpleradio.storage.favorites

import app.cash.turbine.test
import com.alxnophis.simpleradio.storage.favorites.data.FavoriteStationDao
import com.alxnophis.simpleradio.storage.favorites.data.FavoriteStationEntity
import com.alxnophis.simpleradio.storage.favorites.data.FavoriteStationsRepositoryImpl
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class FavoriteStationsRepositoryImplTest {
    private val dao = InMemoryFavoriteStationDao()
    private var now = 0L
    private val repository = FavoriteStationsRepositoryImpl(dao) { now }

    @Test
    fun `GIVEN no favorites WHEN observing favorites THEN emits empty list`() =
        runTest {
            repository.observeFavorites().test {
                awaitItem() shouldBeEqualTo emptyList()
            }
        }

    @Test
    fun `GIVEN a station WHEN added THEN it is emitted as favorite`() =
        runTest {
            repository.add(station("1"))

            repository.observeFavorites().test {
                awaitItem() shouldBeEqualTo listOf(station("1"))
            }
            repository.observeFavoriteIds().test {
                awaitItem() shouldBeEqualTo setOf("1")
            }
        }

    @Test
    fun `GIVEN several favorites WHEN observing THEN most recently added comes first`() =
        runTest {
            now = 1
            repository.add(station("old"))
            now = 2
            repository.add(station("new"))

            repository.observeFavorites().test {
                awaitItem().map { it.id } shouldBeEqualTo listOf("new", "old")
            }
        }

    @Test
    fun `GIVEN a favorite WHEN removed THEN it is no longer emitted`() =
        runTest {
            repository.add(station("1"))
            repository.add(station("2"))

            repository.remove("1")

            repository.observeFavoriteIds().test {
                awaitItem() shouldBeEqualTo setOf("2")
            }
        }

    private fun station(id: String) =
        FavoriteStation(
            id = id,
            name = "Station $id",
            streamUrl = "https://example.com/$id",
            faviconUrl = null,
            codec = "MP3",
            bitrate = 128,
            country = "Spain",
        )

    private class InMemoryFavoriteStationDao : FavoriteStationDao {
        private val entities = MutableStateFlow<List<FavoriteStationEntity>>(emptyList())

        override fun observeAll(): Flow<List<FavoriteStationEntity>> = entities.map { list -> list.sortedByDescending { it.addedAt } }

        override fun observeIds(): Flow<List<String>> = entities.map { list -> list.map { it.id } }

        override suspend fun insert(station: FavoriteStationEntity) {
            entities.value = entities.value.filterNot { it.id == station.id } + station
        }

        override suspend fun delete(stationId: String) {
            entities.value = entities.value.filterNot { it.id == stationId }
        }
    }
}
