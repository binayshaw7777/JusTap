package com.binay.shaw.justap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.Converters
import com.binay.shaw.justap.model.LocalHistory
import com.binay.shaw.justap.model.LocalUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by binay on 02,January,2023
 */

@Database(entities = [LocalUser::class, Accounts::class, LocalHistory::class], version = 4)
@TypeConverters(Converters::class)
abstract class LocalUserDatabase : RoomDatabase() {

    abstract fun localUserDao(): LocalUserDAO

    abstract fun accountsDao(): AccountsDAO

    abstract fun localUserHistoryDao(): LocalHistoryDAO

    companion object {
        @Volatile
        var INSTANCE: LocalUserDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): LocalUserDatabase {

            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalUserDatabase::class.java,
                    "account_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    fun clearTables() {
        CoroutineScope(Dispatchers.IO).launch {
            this@LocalUserDatabase.clearAllTables()
        }
    }
}