package com.task.locationbasedtaskreminder.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tasks")
class Task {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "place")
    var place: String = ""

    @ColumnInfo(name = "latitude")
    var latitude: String = ""

    @ColumnInfo(name = "longitude")
    var longitude: String = ""

    @ColumnInfo(name = "Done")
    var isDone: Boolean = false

}