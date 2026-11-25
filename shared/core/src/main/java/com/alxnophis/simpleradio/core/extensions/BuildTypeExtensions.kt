@file:Suppress("unused", "KotlinConstantConditions")

package com.alxnophis.simpleradio.core.extensions

import com.alxnophis.simpleradio.core.BuildConfig

private const val BUILD_TYPE_DEBUG = "debug"
private const val BUILD_TYPE_RELEASE = "release"

fun isDebugBuildType(): Boolean = (BuildConfig.BUILD_TYPE == BUILD_TYPE_DEBUG)

fun isReleaseBuildType(): Boolean = (BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE)
