package com.aut.isen_todo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent


class FirstPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_page)

        val doBtn: Button = findViewById(R.id.button)
        doBtn.setOnClickListener {
            changeActivity("3")
        }

        val scheduleBtn: Button = findViewById(R.id.button2)
        scheduleBtn.setOnClickListener {
            changeActivity("2")
        }

        val delegateBtn: Button = findViewById(R.id.button4)
        delegateBtn.setOnClickListener {
            changeActivity("1")
        }

        val deleteBtn: Button = findViewById(R.id.button3)
        deleteBtn.setOnClickListener {
            changeActivity("0")
        }

    }

    private fun changeActivity(type: String) {
        val intent = Intent(this, TasksActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }
}