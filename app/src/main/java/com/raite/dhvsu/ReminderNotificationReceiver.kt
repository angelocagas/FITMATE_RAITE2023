package com.raite.dhvsu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoName = intent.getStringExtra("todoName")

        val notificationBuilder = NotificationCompat.Builder(context, "TodoReminderChannel")
            .setSmallIcon(R.drawable.logonobg)
            .setContentTitle("Todo Reminder")
            .setContentText("Time to do: $todoName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(longArrayOf(100, 500, 100, 500))


        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(0, notificationBuilder.build())


    }
}
