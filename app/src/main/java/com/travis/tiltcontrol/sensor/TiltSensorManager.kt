package com.travis.tiltcontrol.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TiltReading(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
)

class TiltSensorManager(context: Context) : SensorEventListener {

    private val sensorManager =
        context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _tiltData = MutableStateFlow(TiltReading())
    val tiltData: StateFlow<TiltReading> = _tiltData.asStateFlow()

    val isAvailable: Boolean
        get() = accelerometer != null

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        _tiltData.value = TiltReading(
            x = event.values[0],
            y = event.values[1],
            z = event.values[2]
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used, but required by the SensorEventListener interface
    }
}