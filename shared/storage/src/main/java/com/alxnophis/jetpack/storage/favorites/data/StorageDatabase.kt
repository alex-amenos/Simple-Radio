package com.alxnophis.jetpack.storage.favorites.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteStationEntity::class],
    version = 1,
    exportSchema = true,
)
internal abstract class StorageDatabase : RoomDatabase() {
    abstract fun favoriteStationDao(): FavoriteStationDao

    companion object {
        const val NAME = "storage.db"
    }
}
