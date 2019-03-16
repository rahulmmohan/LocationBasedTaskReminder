package com.task.locationbasedtaskreminder

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.task.locationbasedtaskreminder.data.AppDatabase
import com.task.locationbasedtaskreminder.data.Task
import com.task.locationbasedtaskreminder.data.TasksDao
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private var tasksDao: TasksDao? = null

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        tasksDao = AppDatabase.getInstance(appContext).taskDao()
    }

    @Test
    fun should_Insert_Task_Item() {
        val task = Task()
        task.id = Random().nextInt()
        task.title = "title"
        task.place = "place"

        tasksDao?.let {
            it.insert(task)
            val taskTest = it.getTask(task.id)
            Assert.assertEquals(task.title,taskTest.title)
        }
    }
}