package com.alxnophis.simpleradio.api.jsonplaceholder.model

import com.alxnophis.simpleradio.kotlin.constants.EMPTY
import com.alxnophis.simpleradio.kotlin.constants.ZERO_LONG
import org.jetbrains.annotations.TestOnly

@TestOnly
object PostApiModelMother {
    operator fun invoke(
        id: Long = ZERO_LONG,
        userId: Long = ZERO_LONG,
        title: String = EMPTY,
        body: String = EMPTY,
    ) = PostApiModel(
        id = id,
        userId = userId,
        title = title,
        body = body,
    )
}
