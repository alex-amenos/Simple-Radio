package com.alxnophis.simpleradio.api.radiobrowser.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryApiModel(
    @SerialName("name") val name: String,
    @SerialName("iso_3166_1") val isoCode: String? = null,
    @SerialName("stationcount") val stationCount: Int = 0,
)
