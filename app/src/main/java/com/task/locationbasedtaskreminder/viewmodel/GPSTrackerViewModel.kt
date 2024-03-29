package com.task.locationbasedtaskreminder.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData


class GPSTrackerViewModel(@NonNull application: Application) : AndroidViewModel(application) , LocationListener {

    // flag for GPS status
    private var isGPSEnabled = false

    // flag for network status
    private var isNetworkEnabled = false

    // flag for GPS status
    private var canGetLocation = false

    //internal var location: Location? = null // location
    var location: MutableLiveData<Location> = MutableLiveData()
    private var latitude: Double = 0.toDouble() // latitude
    private var longitude: Double = 0.toDouble() // longitude

    // Declaring a Location Manager
    private var locationManager: LocationManager? = null

    init {
        locationManager = application.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            // getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
                this.canGetLocation = false
            } else {
                this.canGetLocation = true

                //First get location from Network Provider
                if (isNetworkEnabled) {

                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)

                        location.value = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                        if (location.value != null) {
                            latitude = location.value!!.latitude
                            longitude = location.value!!.longitude
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled && location.value == null) {


                    Log.d("GPS Enabled", "GPS Enabled")
                    if (locationManager != null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)

                        location.value = locationManager!!
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER)

                    }

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location.value
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }


    override fun onLocationChanged(location: Location) {

        this.location.value = location
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {

        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }
}