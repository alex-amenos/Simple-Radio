package com.alxnophis.simpleradio.application

import com.alxnophis.simpleradio.core.base.application.BaseApp
import com.alxnophis.simpleradio.root.di.featureModules
import org.koin.core.module.Module

class SimpleRadioApp : BaseApp() {
    override fun getFeatureModules(): List<Module> = featureModules
}
