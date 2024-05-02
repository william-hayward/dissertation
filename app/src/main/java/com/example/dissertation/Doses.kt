package com.example.dissertation


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Doses")

data class Doses(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var nOfDoses : Int,
    var medication : String

)