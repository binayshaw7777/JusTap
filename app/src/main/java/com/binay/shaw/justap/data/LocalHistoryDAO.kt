package com.binay.shaw.justap.data


import androidx.lifecycle.LiveData
import androidx.room.*
import com.binay.shaw.justap.model.LocalHistory


@Dao
interface LocalHistoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserHistory(localHistory: LocalHistory)

    @Delete
    suspend fun deleteUserHistory(localHistory: LocalHistory)

    @Query(value = "DELETE FROM historyDB")
    fun clearHistory()

    @Query(value = "SELECT * FROM historyDB")
    fun getAllHistory() : LiveData<List<LocalHistory>>
}