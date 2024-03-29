package com.task.locationbasedtaskreminder.views

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.task.locationbasedtaskreminder.R
import com.task.locationbasedtaskreminder.data.Task
import com.task.locationbasedtaskreminder.helper.LocationService
import com.task.locationbasedtaskreminder.helper.TaskActionReceiver
import com.task.locationbasedtaskreminder.helper.isNearToCurrentLocation
import com.task.locationbasedtaskreminder.viewmodel.GPSTrackerViewModel
import com.task.locationbasedtaskreminder.viewmodel.TasksViewModel
import kotlinx.android.synthetic.main.activity_main.*


class TasksActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        internal const val REQUEST_LOCATION = 199
    }

    private var mMap: GoogleMap? = null
    private lateinit var taskViewModel: TasksViewModel
    private lateinit var locationViewModel: GPSTrackerViewModel
    private var googleApiClient: GoogleApiClient? = null
    private val mAllTasks = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        taskViewModel = ViewModelProviders.of(this).get(TasksViewModel::class.java)
        locationViewModel = ViewModelProviders.of(this).get(GPSTrackerViewModel::class.java)

        add_task.setOnClickListener {
            val intent = Intent(this@TasksActivity, ComposeTaskActivity::class.java)
            startActivity(intent)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        if (checkPermission()) {
            mapFragment.getMapAsync(this)
            getLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        val serviceIntent = Intent(this@TasksActivity, LocationService::class.java)
        startService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        val serviceIntent = Intent(this@TasksActivity, LocationService::class.java)
        stopService(serviceIntent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.let {
            it.isMyLocationEnabled = true
            getTasks()
        }
    }

    //12.8327514,77.6548859
    private fun getTasks() {
        taskViewModel.getAllTasks().observe(this, Observer { tasks ->
            tasks?.let {
                mAllTasks.addAll(tasks)
                for (task in mAllTasks) {
                    val lat = task.latitude.toDoubleOrNull()
                    val lon = task.longitude.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val place = LatLng(lat, lon)
                        mMap?.addMarker(
                            MarkerOptions()
                                .position(place)
                                .title(task.title)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_event_note_red_600_24dp))
                        )
                    }
                }
            }
        })
    }

    private fun getLocation() {
        Log.e("Location", "getLocation Called ... ")
        locationViewModel.location.observe(this, Observer { location ->
            if (location != null) {
                Log.e("TaskActivity", "latitude -> ${location.latitude}")
                Log.e("TaskActivity", "longitude -> ${location.longitude}")
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15.0f)
                )
                for (task in mAllTasks) {
                    val lat = task.latitude.toDoubleOrNull()
                    val lon = task.longitude.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        if (isNearToCurrentLocation(location.latitude, location.longitude, lat, lon)) {
                            showNotification(task)
                        }
                    }
                }

            }
        })
        locationViewModel.getLocation()
        // check if GPS enabled
        if (!locationViewModel.canGetLocation()) {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            enableLocation()
        }
    }

    private fun showNotification(task: Task) {
        val intentAction = Intent(this@TasksActivity, TaskActionReceiver::class.java).apply {
            action = Intent.ACTION_ANSWER
            putExtra("taskId", task.id)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(this@TasksActivity, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT)
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
        notificationManager.notify(task.id, builder.build())


    }

    private fun enableLocation() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this@TasksActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(bundle: Bundle?) {

                    }

                    override fun onConnectionSuspended(i: Int) {
                        googleApiClient!!.connect()
                    }
                })
                .addOnConnectionFailedListener { connectionResult ->
                    Log.d(
                        "Location error",
                        "Location error " + connectionResult.errorCode
                    )
                }.build()
            googleApiClient!!.connect()
        }
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status

            Log.e("Location", "status Called  -->" + status.statusCode)

            when (status.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.e("Location", "LocationSettingsStatusCodes.RESOLUTION_REQUIRED Called ....")
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(this@TasksActivity, REQUEST_LOCATION)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this@TasksActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Log.e("permission", "granted")
                return true
            } else {
                ActivityCompat.requestPermissions(
                    this@TasksActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1
                )
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK -> {
                    getLocation()
                    Toast.makeText(this@TasksActivity, "Location enabled!", Toast.LENGTH_SHORT).show()
                }
                Activity.RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(this@TasksActivity, "Location not enabled!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    getLocation()
                    // permission was granted
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this@TasksActivity, "Permission denied to acees location", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }

    }


}
