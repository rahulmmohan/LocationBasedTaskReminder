package com.task.locationbasedtaskreminder.helper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.task.locationbasedtaskreminder.R
import com.task.locationbasedtaskreminder.data.Task
import com.task.locationbasedtaskreminder.viewmodel.GPSTrackerViewModel
import com.task.locationbasedtaskreminder.viewmodel.TasksViewModel
import java.util.*


class LocationService : LifecycleService() {

    private lateinit var taskViewModel: TasksViewModel
    private lateinit var locationViewModel: GPSTrackerViewModel
    private val mAllTasks = ArrayList<Task>()
    private val mHandler = Handler()
    private lateinit var mTimer: Timer

    override fun onCreate() {
        super.onCreate()
        taskViewModel = TasksViewModel(applicationContext as Application)
        locationViewModel = GPSTrackerViewModel(applicationContext as Application)
        mTimer = Timer()
        mTimer.schedule(TimerTaskToGetLocation(), 5, 30000)
    }


    private fun getLocation() {
        if (taskViewModel.getAllTasks().hasObservers()) {
            taskViewModel.getAllTasks().removeObservers(this@LocationService)
        }
        taskViewModel.getAllTasks().observe(this, Observer { tasks ->
            tasks?.let {
                mAllTasks.clear()
                mAllTasks.addAll(tasks)
            }
        })

        Log.e("Location", "getLocation Called ... ")
        for (task in mAllTasks) {
            val lat = task.latitude.toDoubleOrNull()
            val lon = task.longitude.toDoubleOrNull()
            val location = locationViewModel.location.value ?: null
            if (location != null && lat != null && lon != null) {
                if (isNearToCurrentLocation(location.latitude, location.longitude, lat, lon)) {
                    showNotification(task)
                }
            }
        }
        locationViewModel.getLocation()
    }

    private fun showNotification(task: Task) {
        val intentAction = Intent(this@LocationService, TaskActionReceiver::class.java).apply {
            action = Intent.ACTION_ANSWER
            putExtra("taskId", task.id)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(this@LocationService, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, "1000")
            .setSmallIcon(R.drawable.ic_event_note_red_600_24dp)
            .setContentTitle(task.title)
            .setContentText(task.place)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_action_save, "Done", pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "task"
            val descriptionText = "showing task at current location"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1000", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, builder.build())


    }

    private inner class TimerTaskToGetLocation : TimerTask() {
        override fun run() {
            mHandler.post { getLocation() }
        }
    }

}