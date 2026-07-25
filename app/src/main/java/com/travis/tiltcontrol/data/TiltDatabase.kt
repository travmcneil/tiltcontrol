package com.travis.tiltcontrol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LocationEntity::class, MachineEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TiltDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
    abstract fun machineDao(): MachineDao

    companion object {
        @Volatile
        private var INSTANCE: TiltDatabase? = null

        fun getDatabase(context: Context): TiltDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TiltDatabase::class.java,
                    "tiltcontrol_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}