package com.example.dissertation

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.*


@Entity(tableName="Alarms")

data class Alarms(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var desc : String,
    var date : Date,
    var priority : Int,



    ) : java.io.Serializable