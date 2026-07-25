package com.travis.tiltcontrol.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.travis.tiltcontrol.viewmodel.TiltViewModel

@Composable
fun TiltViewScreen(
    viewModel: TiltViewModel,
    machineId: Long
) {
    val machine by viewModel.selectedMachine.collectAsState()
    val reading by viewModel.getCurrentTiltReading().collectAsState()

    // Load the machine's saved data once when this screen first appears
    LaunchedEffect(machineId) {
        viewModel.loadMachine(machineId)
    }

    // Start the sensor when this screen appears, stop it when the user leaves
    DisposableEffect(Unit) {
        viewModel.startSensor()
        onDispose {
            viewModel.stopSensor()
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
                    offsetX = reading.x - (machine?.baselineX ?: 0f),
                    offsetY = reading.y - (machine?.baselineY ?: 0f),
                    threshold = machine?.tiltThreshold ?: 2.5f
                )
            }

            Button(onClick = { viewModel.calibrate() }) {
                Text("Calibrate")
            }
        }
    }
}