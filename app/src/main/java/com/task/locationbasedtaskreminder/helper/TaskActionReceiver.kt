package com.task.locationbasedtaskreminder.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.task.locationbasedtaskreminder.data.AppDatabase

class TaskActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("taskId", -1)
        val db = AppDatabase.getInstance(context)
        val task = db.taskDao().getTask(taskId = taskId)
        task.isDone = true
        db.taskDao().update(task)
    }
}