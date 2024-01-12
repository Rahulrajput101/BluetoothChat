package com.ondevop.bluetoothchat.presentation

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ondevop.bluetoothchat.presentation.components.ChatScreen
import com.ondevop.bluetoothchat.presentation.components.DeviceScreen
import com.ondevop.bluetoothchat.ui.theme.BluetoothChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ /* no need */}

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.S){
                perms[android.Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if( canEnableBluetooth && !isBluetoothEnabled){
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }

        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN
                )
            )
        }

        setContent {
            BluetoothChatTheme {
                val viewModel = hiltViewModel<BluetoothViewModel>()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(key1  = state.errorMessage){
                    state.errorMessage?.let {message ->
                        Toast.makeText(
                            applicationContext,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                LaunchedEffect(key1  = state.isConnected){
                    if(state.isConnected){

                        Toast.makeText(
                            applicationContext,
                            "connected",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }



                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when{
                        state.isConnecting -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                  CircularProgressIndicator()
                                  Spacer(modifier = Modifier.height(6.dp))
                                  Text(
                                      text = "Connecting..."
                                  )
                            }
                        }

                        state.isConnected -> {
                            ChatScreen(
                                state = state,
                                onDisconnect = viewModel::disconnectFromDevice,
                                onSendMessage =viewModel::sendMessage
                            )
                        }

                        else -> {
                            DeviceScreen(
                                state = state,
                                onStartScan = viewModel::startScan
                                ,
                                onStopScan = viewModel::stopScan,
                                onDeviceClick = {
                                    viewModel.connectToDevice(it)
                                },
                                onStartServer = {
                                    viewModel.waitForInComingConnection()
                                }

                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BluetoothChatTheme {
        Greeting("Android")
    }
}