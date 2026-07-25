package com.travis.tiltcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.travis.tiltcontrol.ui.LocationListScreen
import com.travis.tiltcontrol.ui.MachineListScreen
import com.travis.tiltcontrol.ui.TiltViewScreen
import com.travis.tiltcontrol.ui.theme.TiltControlTheme
import com.travis.tiltcontrol.viewmodel.TiltViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TiltControlTheme {
                TiltControlApp()
            }
        }
    }
}

@Composable
fun TiltControlApp() {
    val navController: NavHostController = rememberNavController()
    val viewModel: TiltViewModel = viewModel()

    NavHost(navController = navController, startDestination = "locations") {

        composable("locations") {
            LocationListScreen(
                viewModel = viewModel,
                onLocationClick = { locationId ->
                    navController.navigate("machines/$locationId")
                }
            )
        }

        composable("machines/{locationId}") { backStackEntry ->
            val locationId = backStackEntry.arguments
                ?.getString("locationId")
                ?.toLongOrNull() ?: return@composable

            MachineListScreen(
                viewModel = viewModel,
                locationId = locationId,
                onMachineClick = { machineId ->
                    navController.navigate("tilt/$machineId")
                }
            )
        }

        composable("tilt/{machineId}") { backStackEntry ->
            val machineId = backStackEntry.arguments
                ?.getString("machineId")
                ?.toLongOrNull() ?: return@composable

            TiltViewScreen(
                viewModel = viewModel,
                machineId = machineId
            )
        }
    }
}