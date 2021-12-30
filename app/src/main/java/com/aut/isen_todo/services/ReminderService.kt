package com.aut.isen_todo.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.aut.isen_todo.DBHelper
import com.aut.isen_todo.MainActivity

class ReminderService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, RingtoneManager.TYPE_NOTIFICATION)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val reminderId = intent?.getLongExtra("reminderId", 0)
        val databaseHandler = DBHelper(this, null)
        val reminder = databaseHandler.getReminderById(reminderId ?: 0)
        showAlarmNotification(reminder)
        return START_STICKY
    }

    @SuppressLint("Range")
    private fun showAlarmNotification(reminder: Cursor?) {

        val id = reminder?.getInt(reminder.getColumnIndex(DBHelper.ID_COL))
        val title = reminder?.getString(reminder.getColumnIndex(DBHelper.TITLE_COl))
        val time = reminder?.getLong(reminder.getColumnIndex(DBHelper.NTFTIME_COL))

        if (id != null) {
            createNotificationChannel(id)
        }

        val builder = NotificationCompat.Builder(this, id.toString())
            .setContentTitle(title) //set title of notification
            .setAutoCancel(true) // makes auto cancel of notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //set priority of notification

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //notification message will get at NotificationView
        notificationIntent.putExtra("reminderId", id)
        notificationIntent.putExtra("from", "Notification")

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)
        val notification = builder.build()

        // Add as notification
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (id != null) {
            manager.notify(id, notification)
        }
    }

    private fun createNotificationChannel(id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                id.toString(),
                "Notification Broadcast Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        tts?.stop()
        tts?.shutdown()
    }

}