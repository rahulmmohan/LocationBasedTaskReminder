package com.task.locationbasedtaskreminder.composetask

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.task.locationbasedtaskreminder.R
import kotlinx.android.synthetic.main.activity_compose_task.*

class ComposeTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_task)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Create a new task"
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
        val titleText = titleEditLayout.editText?.text.toString()
        if (titleText.isEmpty()) {
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
}
