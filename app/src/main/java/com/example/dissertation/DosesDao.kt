package com.example.dissertation

import androidx.room.*

@Dao
interface DosesDao {

    @Query("SELECT * FROM doses WHERE id=:id")
    fun getDosesById(id: Long): Doses?

    @Query("SELECT * FROM doses")
    fun getAllDoses(): List<Doses>

    @Query("UPDATE doses SET nOfDoses = nOfDoses +1 WHERE id=:id")
    fun addDose(id: Long)

    @Insert
    fun insert(doses: Doses) : Long

    @Update
    fun update(doses: Doses)


}