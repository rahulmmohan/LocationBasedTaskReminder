package com.task.locationbasedtaskreminder.composetask

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.task.locationbasedtaskreminder.R
import kotlinx.android.synthetic.main.activity_compose_task.*

class ComposeTaskActivity : AppCompatActivity() {

    private lateinit var composeTaskViewModel: ComposeTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_task)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Create a new task"
        }
        composeTaskViewModel = ViewModelProviders.of(this).get(ComposeTaskViewModel::class.java)

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
        val location = LocationEditLayout.editText?.text.toString()
        val lat = latitudeEditLayout.editText?.text.toString()
        val lon = longitudeEditLayout.editText?.text.toString()
        if (title.isEmpty() || location.isEmpty()) {
            Snackbar.make(coordinator, R.string.error_empty, Snackbar.LENGTH_LONG).show()
        } else {

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
