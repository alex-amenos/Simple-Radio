package com.alxnophis.jetpack.radioplayer.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.radioplayer.data.model.RadioError
import com.alxnophis.jetpack.radioplayer.data.model.RadioStation

internal fun interface RadioStationsRepository {
    suspend fun getSpanishStations(): Either<RadioError, List<RadioStation>>
}
