package com.example.dissertation

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.dissertation.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var picker : MaterialTimePicker
    private lateinit var calender : Calendar
    private lateinit var alarmManager : AlarmManager
    private lateinit var pendingIntent : PendingIntent

    val dosesList = mutableListOf<Doses>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        //initDatabase()
        getFromDatabase()

        println(dosesList)

        binding = ActivityMainBinding.inflate(layoutInflater)
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

        if (dosesList.isNotEmpty()) {
            var firstDoses: Doses? = dosesList.firstOrNull()
            var nOfDoses: Int = firstDoses?.nOfDoses ?: 0

            if (nOfDoses == 0){
                binding.Doses.text = "You have currently taken no doses of medication but don't worry!" +
                        " \nSet an alarm to start taking medicines!"
            }else{
            binding.Doses.text = "You have currently taken " + String.format(nOfDoses.toString()) +
                    " doses of your medication. \n Well Done!"
            }
        }

        var nv = findViewById<NavigationView>(R.id.nv)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        nv.setNavigationItemSelectedListener {
            val returnValue = true
            when(it.itemId){
                R.id.MainPage -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            returnValue
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

    fun initDatabase(){
        lifecycleScope.launch {
            val db = DosesDatabase.getDatabase(application)

                var dose = Doses(0, 0, "Sertraline")
                var id = 0L

                withContext(Dispatchers.IO) {
                    id = db.DosesDao().insert(dose)
                }
            }

        }

    fun getFromDatabase(){
        lifecycleScope.launch {
            val db = DosesDatabase.getDatabase(application)


            var id = 1L

            withContext(Dispatchers.IO) {
                var dose = db.DosesDao().getDosesById(id)
                dosesList.clear()
                if (dose != null) {
                    dosesList.add(dose)
                }

            }

        }

    }

}