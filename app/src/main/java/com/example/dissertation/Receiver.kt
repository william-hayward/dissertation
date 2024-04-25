package com.example.dissertation

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class Receiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        val intent = Intent(p0, NotificationActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(p0, 0, intent, 0)

        val builder = NotificationCompat.Builder(p0!!, "MedicineAlarm")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Medicine Alarm!")
            .setContentText("Its Time To Take Your Medicine!")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val NotificationManager = NotificationManagerCompat.from(p0)
        NotificationManager.notify(1, builder.build())
    }
}