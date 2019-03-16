package com.task.locationbasedtaskreminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.task.locationbasedtaskreminder.composetask.ComposeTaskActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val COMPOSE_TASK_REQUEST = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        add_task.setOnClickListener { view ->
            val intent = Intent(this@MainActivity, ComposeTaskActivity::class.java)
            startActivityForResult(intent, COMPOSE_TASK_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == COMPOSE_TASK_REQUEST && resultCode == Activity.RESULT_OK){
            //Reload map
        }
    }
}
