package com.travis.tiltcontrol.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.travis.tiltcontrol.data.LocationEntity
import com.travis.tiltcontrol.data.MachineEntity
import com.travis.tiltcontrol.data.TiltDatabase
import com.travis.tiltcontrol.sensor.TiltReading
import com.travis.tiltcontrol.sensor.TiltSensorManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TiltViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TiltDatabase.getDatabase(application)
    private val locationDao = database.locationDao()
    private val machineDao = database.machineDao()

    val sensorManager = TiltSensorManager(application)

    // Currently selected machine, loaded when the user navigates to the tilt screen
    private val _selectedMachine = MutableStateFlow<MachineEntity?>(null)
    val selectedMachine: StateFlow<MachineEntity?> = _selectedMachine.asStateFlow()

    // All locations, observed live from the database
    fun getAllLocations(): Flow<List<LocationEntity>> = locationDao.getAllLocations()

    // Machines for a specific location, observed live from the database
    fun getMachinesForLocation(locationId: Long): Flow<List<MachineEntity>> =
        machineDao.getMachinesForLocation(locationId)

    fun addLocation(name: String) {
        viewModelScope.launch {
            locationDao.insertLocation(LocationEntity(name = name))
        }
    }

    fun addMachine(locationId: Long, name: String) {
        viewModelScope.launch {
            machineDao.insertMachine(
                MachineEntity(locationId = locationId, name = name)
            )
        }
    }

    fun deleteLocation(location: LocationEntity) {
        viewModelScope.launch {
            locationDao.deleteLocation(location)
        }
    }

    fun deleteMachine(machine: MachineEntity) {
        viewModelScope.launch {
            machineDao.deleteMachine(machine)
        }
    }

    fun loadMachine(machineId: Long) {
        viewModelScope.launch {
            _selectedMachine.value = machineDao.getMachineById(machineId)
        }
    }

    fun startSensor() = sensorManager.start()

    fun stopSensor() = sensorManager.stop()

    fun getCurrentTiltReading(): StateFlow<TiltReading> = sensorManager.tiltData

    fun calibrate() {
        val machine = _selectedMachine.value ?: return
        val reading = sensorManager.tiltData.value

        viewModelScope.launch {
            val calibrated = machine.copy(
                baselineX = reading.x,
                baselineY = reading.y,
                baselineZ = reading.z
            )
            machineDao.updateMachine(calibrated)
            _selectedMachine.value = calibrated
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stop()
    }
}