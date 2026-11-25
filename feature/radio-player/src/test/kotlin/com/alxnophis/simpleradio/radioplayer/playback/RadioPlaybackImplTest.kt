package com.alxnophis.simpleradio.radioplayer.playback

import com.alxnophis.simpleradio.radioplayer.data.model.RadioStationMother
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

internal class RadioPlaybackImplTest {
    private val player: RadioPlayer = mock()
    private val playback = RadioPlaybackImpl(player)

    @Test
    fun `GIVEN favorite stations WHEN played THEN the queue is mapped and started at the given index`() {
        val stations = listOf(favorite("1"), favorite("2"))

        playback.play(stations, startIndex = 1)

        verify(player).connect()
        verify(player).playQueue(
            listOf(RadioStationMother.create(id = "1"), RadioStationMother.create(id = "2")),
            1,
        )
    }

    @Test
    fun `GIVEN an index out of range WHEN played THEN nothing is played`() {
        playback.play(listOf(favorite("1")), startIndex = 3)

        verify(player, never()).playQueue(any(), any())
    }

    private fun favorite(id: String) =
        FavoriteStation(
            id = id,
            name = "Cadena SER",
            streamUrl = "https://example.com/ser.mp3",
            faviconUrl = "https://example.com/ser.png",
            codec = "MP3",
            bitrate = 128,
            country = "Spain",
        )
}
