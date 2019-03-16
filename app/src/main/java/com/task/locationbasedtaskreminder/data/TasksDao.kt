package com.task.locationbasedtaskreminder.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TasksDao {
    @Query("SELECT * FROM Tasks")
    fun getAll(): List<Task>
}