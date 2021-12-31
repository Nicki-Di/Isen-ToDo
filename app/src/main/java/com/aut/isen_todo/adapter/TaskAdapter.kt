package com.aut.isen_todo.adapter

import android.app.*
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.aut.isen_todo.*
import com.aut.isen_todo.Notification
import com.aut.isen_todo.model.TaskModel
import com.google.android.material.card.MaterialCardView
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(
    private val context: Context,
    private val tasksList: MutableList<TaskModel>
) : RecyclerView.Adapter<TaskAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val cardview: MaterialCardView = view.findViewById(R.id.main_cardview)
        val checkbox: CheckBox = view.findViewById(R.id.task_checkbox)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_task_button)
        val mailButton: ImageButton = view.findViewById(R.id.mail_task_button)
        val notificationButton: ImageButton = view.findViewById(R.id.notification_task_button)
        val notificationTimeTextView: TextView = view.findViewById(R.id.notification_time)
        val task = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_layout, parent, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        return ItemViewHolder(adapterLayout)
    }

    fun updateCheckBoxTextStyle(holder: ItemViewHolder) {
        if (holder.checkbox.isChecked)
            holder.checkbox.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        else
            holder.checkbox.apply {
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = tasksList[position]

        val color = when (item.type) {
            0 -> R.color.type0
            1 -> R.color.type1
            2 -> R.color.type2
            else -> R.color.type3
        }
        holder.cardview.strokeColor = context.getColor(color)

        holder.checkbox.text = item.title
        holder.checkbox.isChecked = when (item.done) {
            1 -> true
            else -> false
        }

        updateCheckBoxTextStyle(holder)

        Log.d("pm", item.id.toString())
        Log.d("pm", item.done.toString())
        Log.d("pm", item.notifTime.toString())

        holder.checkbox.setOnClickListener {
            val db = DBHelper(context, null)
            if (holder.checkbox.isChecked)

                db.updateTaskStatus(item, done = 1)
            else
                db.updateTaskStatus(item, done = 0)
            updateCheckBoxTextStyle(holder)
        }

        holder.deleteButton.setOnClickListener {
            val db = DBHelper(context, null)
            if (db.deleteTask(item) == 1) {
                tasksList.remove(item)
                notifyDataSetChanged()
            }
        }

        if (item.type == 1) {
            mailTaskDetail(holder, item)
        }


        if (item.type == 2) {
            getNotificationTime(holder, item)
            setNotificationTime(holder, item)
        }


    }

    override fun getItemCount() = tasksList.size


    fun mailTaskDetail(holder: ItemViewHolder, item: TaskModel) {
        holder.mailButton.visibility = VISIBLE
        holder.mailButton.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + ""))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Delegating task " + item.title)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {

            }
        }
    }

    fun getNotificationTime(holder: ItemViewHolder, item: TaskModel) {
        if (item.notifTime.after(Timestamp(System.currentTimeMillis()))) {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.ENGLISH)
            holder.notificationTimeTextView.text = dateFormat.format(Date(item.notifTime.getTime()))
            holder.notificationTimeTextView.visibility = VISIBLE
        } else
            holder.notificationTimeTextView.visibility = INVISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setNotificationTime(holder: ItemViewHolder, item: TaskModel) {
        holder.notificationButton.visibility = VISIBLE
        holder.notificationButton.setOnClickListener {
            val mTimePicker: TimePickerDialog
            val mDatePicker: DatePickerDialog
            var defaultTime: Timestamp
            if (item.notifTime.after(Timestamp(System.currentTimeMillis())))
                defaultTime = item.notifTime
            else
                defaultTime = Timestamp(System.currentTimeMillis())

            var notificationHour = 0
            var notificationMinute = 0
            var notifYear = 0
            var notifMonth = 0
            var notifDay = 0

            mTimePicker =
                TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        val db = DBHelper(context, null)
                        val notifTime = Timestamp(
                            notifYear, notifMonth, notifDay, hourOfDay, minute, 28, 4
                        )
                        db.updateTaskStatus(item, notifTime = notifTime)
                        item.notifTime = notifTime
                        notifyDataSetChanged()

                        // Set Notification
                        scheduleNotification(
                            item.id, item.title, "Do the ' ${item.title} ' Now .. !",
                            notifTime.time
                        )

                    }

                }, defaultTime.hours, defaultTime.minutes, false)


            mDatePicker =
                DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { view, theYear, monthOfYear, dayOfMonth ->
                        notifYear = theYear - 1900
                        notifMonth = monthOfYear
                        notifDay = dayOfMonth
                        mTimePicker.show()
                    }, defaultTime.year + 1900, defaultTime.month, defaultTime.date
                )
            mDatePicker.datePicker.minDate = System.currentTimeMillis()

            mDatePicker.show()

        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(id: Int, title: String, message: String, time: Long) {
        val intent = Intent(context, Notification::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        intent.putExtra(notificationIDExtra, id)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(context)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(context)

        AlertDialog.Builder(context)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Work: " + title +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "ToDo Notif Channel..!"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}