package com.aut.isen_todo

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.aut.isen_todo.adapter.TaskAdapter
import com.aut.isen_todo.model.TaskModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception

class TasksActivity : AppCompatActivity() {

    var tasksList = mutableListOf<TaskModel>()
    var adapter: TaskAdapter = TaskAdapter(this, tasksList)
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTaskFloatingButton: FloatingActionButton


    // Load Tasks from the Database and Show!
    @SuppressLint("Range")
    fun getTasks() {
        val db = DBHelper(this, null)

        tasksList.clear()

        // below is the variable for cursor
        // we have called method to get
        // all names from our database
        // and add to name text view
        val cursor = db.getTask()

        // moving the cursor to first position and
        // appending value in the text view
        cursor!!.moveToFirst()
        try {
            val id = cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL))
            val title = cursor.getString(cursor.getColumnIndex(DBHelper.TITLE_COl))
            val type = cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_COL))
            val done = cursor.getInt(cursor.getColumnIndex(DBHelper.DONE_COL))

            val task = TaskModel(id, title, type, done)
            tasksList.add(task)


            // moving our cursor to next
            // position and appending values
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL))
                val title = cursor.getString(cursor.getColumnIndex(DBHelper.TITLE_COl))
                val type = cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_COL))
                val done = cursor.getInt(cursor.getColumnIndex(DBHelper.DONE_COL))

                val task = TaskModel(id, title, type, done)
                tasksList.add(task)
            }
            tasksRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun radioButtonStyles(btn1:RadioButton, btn2:RadioButton){
        btn1.setOnClickListener {
            btn1.setTextColor(this.getColor(R.color.white))
            btn2.setTextColor(this.getColor(R.color.urgnet))
        }
        btn2.setOnClickListener {
            btn2.setTextColor(this.getColor(R.color.white))
            btn1.setTextColor(this.getColor(R.color.not_urgent))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun openAddTaskSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.add_task, null)

        val urgent = view.findViewById<RadioButton>(R.id.urgent)
        val notUrgent = view.findViewById<RadioButton>(R.id.not_urgent)
        val important = view.findViewById<RadioButton>(R.id.important)
        val notImportant = view.findViewById<RadioButton>(R.id.not_important)
        radioButtonStyles(urgent, notUrgent)
        radioButtonStyles(important, notImportant)



        var addTaskButton = view.findViewById<Button>(R.id.add_button)
        addTaskButton.setOnClickListener {
            val urgency = view.findViewById<RadioGroup>(R.id.urgency)
            val importance = view.findViewById<RadioGroup>(R.id.importance)
            val editText = view.findViewById<EditText>(R.id.editText)

            val taskTitle = editText.text.toString()

            val isUrgent = urgency.checkedRadioButtonId == R.id.urgent
            val isImportant = importance.checkedRadioButtonId == R.id.important


            var taskType = 0
            if (isUrgent && !isImportant) taskType = 1
            else if (!isUrgent && isImportant) taskType = 2
            else if (isUrgent && isImportant) taskType = 3

            val newTask = TaskModel(0, taskTitle, taskType, 0)

            val dbHelper = DBHelper(this, null)
            dbHelper.addTask(newTask)

            getTasks()
            dialog.dismiss()

        }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        tasksRecyclerView = findViewById<RecyclerView>(R.id.tasks_recyclerview)
        tasksRecyclerView.adapter = adapter

        addTaskFloatingButton = findViewById(R.id.add_task_floating_button)
        addTaskFloatingButton.setOnClickListener {
            openAddTaskSheet()
        }

        getTasks()

    }
}