package com.task.locationbasedtaskreminder.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.task.locationbasedtaskreminder.R
import com.task.locationbasedtaskreminder.viewmodel.TasksViewModel
import kotlinx.android.synthetic.main.activity_compose_task.*
import java.util.*

class ComposeTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TasksViewModel
    private var AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_task)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Create a new task"
        }
        taskViewModel = ViewModelProviders.of(this).get(TasksViewModel::class.java)
        // Initialize Places.
        Places.initialize(applicationContext, getString(R.string.google_maps_key))


        val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        )
            .build(this)
        locationimageView.setOnClickListener {
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                locationEditLayout.editText?.setText(place.name)
                latitudeEditLayout.editText?.setText(place.latLng?.latitude.toString())
                longitudeEditLayout.editText?.setText(place.latLng?.longitude.toString())

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.compose_task_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> exitCompose()
            R.id.action_save -> saveNote()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        val title = titleEditLayout.editText?.text.toString()
        val location = locationEditLayout.editText?.text.toString()
        val lat = latitudeEditLayout.editText?.text.toString()
        val lon = longitudeEditLayout.editText?.text.toString()
        if (title.isEmpty() || location.isEmpty()) {
            Snackbar.make(coordinator, R.string.error_empty, Snackbar.LENGTH_LONG).show()
        } else {
            taskViewModel.createTask(title, location, lat, lon).observe(this, Observer {
                if (it > -1) {
                    finish()
                } else {
                    Snackbar.make(coordinator, "Something went wrong!", Snackbar.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun exitCompose() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setMessage(getString(R.string.discard))
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "YES"
        ) { dialog, _ -> dialog.dismiss();finish() }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO") { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    override fun onBackPressed() {
        exitCompose()
    }
}
