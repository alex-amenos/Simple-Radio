package com.alxnophis.jetpack.storage.favorites.di

import androidx.room.Room
import com.alxnophis.jetpack.storage.favorites.data.FavoriteStationsRepositoryImpl
import com.alxnophis.jetpack.storage.favorites.data.StorageDatabase
import com.alxnophis.jetpack.storage.favorites.repository.FavoriteStationsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val storageModule: Module =
    module {
        single {
            Room
                .databaseBuilder(androidContext(), StorageDatabase::class.java, StorageDatabase.NAME)
                .build()
        }
        single { get<StorageDatabase>().favoriteStationDao() }
        single<FavoriteStationsRepository> { FavoriteStationsRepositoryImpl(get()) }
    }
