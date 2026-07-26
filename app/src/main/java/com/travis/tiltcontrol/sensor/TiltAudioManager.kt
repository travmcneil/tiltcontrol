package com.travis.tiltcontrol.sensor

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.math.sin

class TiltAudioManager(context: Context) {

    private val sampleRate = 44100

    // --- Proximity tone (quiet hum that gets louder near the ring) ---
    private val proximityHz = 700.0
    private var proximityTrack: AudioTrack? = null

    // --- Alarm siren (plays once the bob hits the outer ring) ---
    private val alarmLowHz = 500.0
    private val alarmHighHz = 900.0
    private val alarmLoopSeconds = 0.6
    private var alarmTrack: AudioTrack? = null
    private var alarmActive = false

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    init {
        proximityTrack = buildLoopingTone(
            frequencies = listOf(proximityHz),
            loopSeconds = 1.0
        )
        proximityTrack?.setVolume(0f)
        proximityTrack?.play()

        // Alternating low/high tone = classic siren/alarm feel
        alarmTrack = buildLoopingTone(
            frequencies = listOf(alarmLowHz, alarmHighHz),
            loopSeconds = alarmLoopSeconds
        )
        alarmTrack?.setVolume(0f)
        alarmTrack?.play()
    }

    private fun buildLoopingTone(frequencies: List<Double>, loopSeconds: Double): AudioTrack {
        val numSamples = (sampleRate * loopSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val segmentLength = numSamples / frequencies.size

        for (i in 0 until numSamples) {
            val freq = frequencies[minOf(i / segmentLength, frequencies.size - 1)]
            val angle = 2.0 * Math.PI * freq * i / sampleRate
            buffer[i] = (sin(angle) * Short.MAX_VALUE * 0.8).toInt().toShort()
        }

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBufferSize, buffer.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.setLoopPoints(0, numSamples - 1, -1)
        return track
    }

    fun setProximityVolume(volume: Float) {
        proximityTrack?.setVolume(volume.coerceIn(0f, 1f))
    }

    fun startAlarm() {
        if (!alarmActive) {
            alarmActive = true
            proximityTrack?.setVolume(0f)
            alarmTrack?.setVolume(1f)
            vibrate()
        }
    }

    fun stopAlarm() {
        if (alarmActive) {
            alarmActive = false
            alarmTrack?.setVolume(0f)
        }
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(300)
        }
    }

    fun release() {
        proximityTrack?.stop()
        proximityTrack?.release()
        proximityTrack = null

        alarmTrack?.stop()
        alarmTrack?.release()
        alarmTrack = null
    }
}