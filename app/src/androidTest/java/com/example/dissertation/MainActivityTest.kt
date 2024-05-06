package com.example.dissertation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Mock
    lateinit var mockAlarmManager: AlarmManager

    @Mock
    lateinit var mockPendingIntent: PendingIntent


    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var database: DosesDatabase
    private lateinit var dosesDao: DosesDao


    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DosesDatabase::class.java
        ).build()
        dosesDao = database.DosesDao()

        MockitoAnnotations.openMocks(this)
        scenario = ActivityScenario.launch(MainActivity::class.java)

    }

    @Test
    fun testSetTime() {
        scenario.onActivity { activity ->
            activity.alarmManager = mockAlarmManager

            val expectedIntent = Intent(activity, Receiver::class.java)
            val requestCode = 0

            `when`(activity.getSystemService(AlarmManager::class.java)).thenReturn(mockAlarmManager)
            `when`(PendingIntent.getBroadcast(activity, requestCode, expectedIntent, PendingIntent.FLAG_MUTABLE))
                .thenReturn(mockPendingIntent)

            activity.SetTime()

            verify(mockAlarmManager).setRepeating(
                AlarmManager.RTC_WAKEUP, activity.calender.timeInMillis,
                AlarmManager.INTERVAL_DAY, mockPendingIntent
            )
        }
    }

    @After
    fun tearDown() {
        scenario.close()
        database.close()
    }

    @Test
    fun testCancel() {
        scenario.onActivity { activity ->
            activity.alarmManager = mockAlarmManager

            val expectedIntent = Intent(activity, Receiver::class.java)
            val requestCode = 0

            `when`(activity.getSystemService(AlarmManager::class.java)).thenReturn(mockAlarmManager)
            `when`(PendingIntent.getBroadcast(activity, requestCode, expectedIntent, PendingIntent.FLAG_MUTABLE))
                .thenReturn(mockPendingIntent)

            activity.cancel()

            verify(mockAlarmManager).cancel(mockPendingIntent)
        }
    }


    @Test
    fun testInitDatabase() {
        // Insert a dose into the database
        val dose = Doses(0, 0, "Sertraline")
        dosesDao.insert(dose)

        // Retrieve the dose from the database
        val retrievedDose = dosesDao.getDosesById(1L)

        // Assert that the retrieved dose matches the inserted dose
        assert(retrievedDose != null)
        assert(retrievedDose?.id?.toInt() == 1)
        assert(retrievedDose?.nOfDoses == 0)
        assert(retrievedDose?.medication == "Sertraline")
    }

    @Test
    fun testGetFromDatabase() {
        // Insert a dose into the database
        val dose = Doses(0, 0, "Sertraline")
        dosesDao.insert(dose)

        // Retrieve the dose from the database
        val dosesList = dosesDao.getAllDoses()

        // Assert that the list contains the inserted dose
        assert(dosesList.isNotEmpty())
        assert(dosesList.size == 1)
        assert(dosesList[0].id.toInt() == 1)
        assert(dosesList[0].nOfDoses == 0)
        assert(dosesList[0].medication == "Sertraline")
    }

}