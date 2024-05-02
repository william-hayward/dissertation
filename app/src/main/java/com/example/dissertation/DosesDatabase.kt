package com.example.dissertation

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = arrayOf(Doses::class), version = 1, exportSchema = false)
public abstract class DosesDatabase: RoomDatabase() {
    abstract fun DosesDao(): DosesDao

    companion object{
        private var instance: DosesDatabase? = null

        fun getDatabase(ctx:Context) : DosesDatabase{
            var tmpInstance = instance
            if(tmpInstance == null){
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    DosesDatabase::class.java,
                    "DosesDatabase"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }
}