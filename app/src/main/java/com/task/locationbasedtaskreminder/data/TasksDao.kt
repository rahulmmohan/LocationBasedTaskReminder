package com.task.locationbasedtaskreminder.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TasksDao {
    @Insert
    fun insert(task: Task): Long

    @Update
    fun update(task: Task)

    @Query("SELECT * FROM Tasks WHERE isDone = 0")
    fun getAll(): LiveData<List<Task>>

    @Query("SELECT * FROM Tasks WHERE id=:taskId")
    fun getTask(taskId: Int): Task
}