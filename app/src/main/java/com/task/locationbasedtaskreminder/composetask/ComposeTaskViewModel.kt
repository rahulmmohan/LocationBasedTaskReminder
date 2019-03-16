package com.task.locationbasedtaskreminder.composetask

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.task.locationbasedtaskreminder.data.AppDatabase
import com.task.locationbasedtaskreminder.data.Task

class ComposeTaskViewModel(@NonNull application: Application) : AndroidViewModel(application) {
    var db: AppDatabase = AppDatabase.getInstance(application.applicationContext)

    fun createTask(title: String, place: String, lat: String, lon: String): LiveData<Long> {
        val task = Task()
        task.title = title
        task.place = place
        task.latitude = lat
        task.longitude = lon
        return MutableLiveData<Long>(db.taskDao().insert(task))
    }
}