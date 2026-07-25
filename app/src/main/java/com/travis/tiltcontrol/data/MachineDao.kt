package com.travis.tiltcontrol.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MachineDao {

    @Query("SELECT * FROM machines WHERE locationId = :locationId ORDER BY name ASC")
    fun getMachinesForLocation(locationId: Long): Flow<List<MachineEntity>>

    @Query("SELECT * FROM machines WHERE id = :machineId")
    suspend fun getMachineById(machineId: Long): MachineEntity?

    @Insert
    suspend fun insertMachine(machine: MachineEntity): Long

    @Update
    suspend fun updateMachine(machine: MachineEntity)

    @Delete
    suspend fun deleteMachine(machine: MachineEntity)
}