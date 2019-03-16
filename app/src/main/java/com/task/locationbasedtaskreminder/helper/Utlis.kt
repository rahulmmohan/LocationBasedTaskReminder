package com.task.locationbasedtaskreminder.helper

import android.location.Location


fun isNearToCurrentLocation(
    currentLatitude: Double,
    currentLongitude: Double,
    totLatitude: Double,
    toLongitude: Double
): Boolean {
    val results = FloatArray(1)
    Location.distanceBetween(currentLatitude, currentLongitude, totLatitude, toLongitude, results)
    val distanceInMeters = results[0]
    return distanceInMeters < 100
}