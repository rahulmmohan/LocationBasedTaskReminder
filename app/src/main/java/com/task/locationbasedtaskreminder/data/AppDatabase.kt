package com.task.locationbasedtaskreminder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [(Task::class)], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun taskDao(): TasksDao

    companion object {
        private const val DATABASE = "locationbasedtaskreminder"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java,DATABASE)
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }
    }
}