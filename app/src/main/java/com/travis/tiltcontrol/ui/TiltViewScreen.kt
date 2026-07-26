package com.travis.tiltcontrol.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.travis.tiltcontrol.sensor.PendulumSimulator
import com.travis.tiltcontrol.sensor.TiltAudioManager
import com.travis.tiltcontrol.viewmodel.TiltViewModel
import kotlinx.coroutines.delay
import kotlin.math.sqrt

@Composable
fun TiltViewScreen(
    viewModel: TiltViewModel,
    machineId: Long
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity

    val machine by viewModel.selectedMachine.collectAsState()
    val reading by viewModel.getCurrentTiltReading().collectAsState()

    val simulator = remember { PendulumSimulator() }
    var bobX by remember { mutableStateOf(0f) }
    var bobY by remember { mutableStateOf(0f) }

    val audioManager = remember { TiltAudioManager(context) }

    LaunchedEffect(machineId) {
        viewModel.loadMachine(machineId)
    }

    DisposableEffect(Unit) {
        viewModel.startSensor()
        onDispose {
            viewModel.stopSensor()
        }
    }

    // Stop all sound immediately if the app is backgrounded, minimized,
    // or the screen otherwise leaves the foreground — not just when
    // navigating to a different screen within the app
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                audioManager.stopAlarm()
                audioManager.setProximityVolume(0f)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Physics loop: steps the pendulum simulation once per rendered frame,
    // independent of how often new sensor samples arrive
    LaunchedEffect(machine) {
        var lastFrameTime = withFrameNanos { it }
        while (true) {
            withFrameNanos { frameTime ->
                val dt = (frameTime - lastFrameTime) / 1_000_000_000f
                lastFrameTime = frameTime

                val targetX = reading.x - (machine?.baselineX ?: 0f)
                val targetY = reading.y - (machine?.baselineY ?: 0f)

                simulator.update(targetX, targetY, dt)
                bobX = simulator.x
                bobY = simulator.y
            }
        }
    }

    // Audio loop: proximity tone volume rises near the ring;
    // full alarm siren + vibration once it actually hits the edge
    LaunchedEffect(Unit) {
        try {
            while (true) {
                val threshold = machine?.tiltThreshold ?: 2.5f
                val distance = sqrt(bobX * bobX + bobY * bobY)
                val ratio = (distance / threshold).coerceIn(0f, 1f)

                if (ratio >= 1f) {
                    audioManager.startAlarm()
                } else {
                    audioManager.stopAlarm()
                    audioManager.setProximityVolume(ratio * ratio)
                }

                delay(30)
            }
        } finally {
            audioManager.release()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = machine?.name ?: "Loading...")

            Column(modifier = Modifier.padding(vertical = 24.dp)) {
                TiltBobView(
                    offsetX = bobX,
                    offsetY = bobY,
                    threshold = machine?.tiltThreshold ?: 2.5f
                )
            }

            Text(text = "Tilt threshold: ${"%.1f".format(machine?.tiltThreshold ?: 2.5f)}")

            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                OutlinedButton(onClick = { viewModel.adjustThreshold(-0.1f) }) {
                    Text("−")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(onClick = { viewModel.adjustThreshold(0.1f) }) {
                    Text("+")
                }
            }

            Button(
                onClick = {
                    viewModel.calibrate()
                    simulator.reset()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Calibrate")
            }

            OutlinedButton(
                onClick = { activity?.finishAffinity() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Exit App")
            }
        }
    }
}