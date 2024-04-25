package com.example.dissertation

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.getSystemService
import com.example.dissertation.databinding.ActivityMainBinding
import com.example.dissertation.databinding.AddalarmBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar

class AddAlarmActivity : AppCompatActivity() {

    private lateinit var binding : AddalarmBinding
    private lateinit var picker : MaterialTimePicker
    private lateinit var calender : Calendar
    private lateinit var alarmManager : AlarmManager
    private lateinit var pendingIntent : PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddalarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()



        binding.EditTime.setOnClickListener{
            PickTime()

        }
        binding.SetTime.setOnClickListener {
            SetTime()

        }
        binding.CancelTime.setOnClickListener {
            cancel()

        }





    }






    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.MainPage -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.AddAlarm -> {
                val intent = Intent(this, AddAlarmActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name : CharSequence = "MedicationAlarmChannel"
            val desc = "Medication Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MedicineAlarm", name, importance)
            channel.description = desc
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun PickTime(){
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Please choose an alarm time.")
            .build()
        picker.show(supportFragmentManager, "MedicineAlarm")

        picker.addOnPositiveButtonClickListener {
            if(picker.hour > 12){
                binding.Time.text =
                    String.format("%02d", picker.hour - 12) + " : " + String.format("%02d", picker.minute) + "PM"

            }else{
                binding.Time.text =
                    String.format("%02d", picker.hour) + " : " + String.format("%02d", picker.minute) + "AM"
            }


        calender = Calendar.getInstance()
        calender[Calendar.HOUR_OF_DAY] = picker.hour
        calender[Calendar.MINUTE] = picker.minute
        calender[Calendar.SECOND] = 0
        calender[Calendar.MILLISECOND] = 0
        }
    }

    private fun SetTime(){
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, Receiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,calender.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent
        )

        Toast.makeText(this, "Alarm Has Been Set.", Toast.LENGTH_SHORT).show()
    }

    private fun cancel(){
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, Receiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Alarm has been cancelled.", Toast.LENGTH_SHORT).show()
    }
}