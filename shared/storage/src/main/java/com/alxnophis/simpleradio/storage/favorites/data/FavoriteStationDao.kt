package com.alxnophis.simpleradio.storage.favorites.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteStationDao {
    @Query("SELECT * FROM favorite_stations ORDER BY added_at DESC")
    fun observeAll(): Flow<List<FavoriteStationEntity>>

    @Query("SELECT id FROM favorite_stations")
    fun observeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: FavoriteStationEntity)

    @Query("DELETE FROM favorite_stations WHERE id = :stationId")
    suspend fun delete(stationId: String)
}
