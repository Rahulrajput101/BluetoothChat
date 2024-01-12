package com.ondevop.bluetoothchat.presentation

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ondevop.bluetoothchat.domain.chat.BluetoothController
import com.ondevop.bluetoothchat.domain.chat.BluetoothDevice
import com.ondevop.bluetoothchat.domain.chat.BluetoothDeviceDomain
import com.ondevop.bluetoothchat.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
     val state = combine(
         bluetoothController.scannedDevices,
         bluetoothController.pairedDevices,
         _state
     ){ scannedDevices,paredDevices,state ->
         state.copy(
             scannedDevice = scannedDevices,
             paredDevices = paredDevices,
             messages = if(state.isConnected) state.messages else emptyList()
         )
     }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var connectionJob : Job? = null

    init {
        bluetoothController.isConnected.onEach {isConnected ->
            _state.update {it.copy(isConnected = isConnected)
            }
        }.launchIn(viewModelScope)

        bluetoothController.error.onEach { error ->
            _state.update { it.copy(errorMessage = error) }
        }
    }

    fun startScan(){
        bluetoothController.startDiscovery()
    }

    fun stopScan(){
        bluetoothController.stopDiscovery()
    }

    fun connectToDevice(device: BluetoothDeviceDomain){
        _state.update { it.copy(isConnecting = true) }
       connectionJob =  bluetoothController.connectToDevice(device)
            .listen()
    }


    fun disconnectFromDevice(){
        connectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update { it.copy(
            isConnecting = false,
            isConnected = false
        ) }
    }

    fun waitForInComingConnection() {
       _state.update { it.copy(isConnecting = true)}
        connectionJob = bluetoothController.startBluetoothServer().listen()
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.trySendMessage(message)
            if(bluetoothMessage != null){
                _state.update { it.copy(
                    messages = it.messages + bluetoothMessage
                ) }
            }
        }
    }


    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when(result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update { it.copy(
                        isConnected = true,
                        isConnecting = false,
                        errorMessage = null
                    ) }
                }
                is ConnectionResult.TransferredSucceeded -> {
                    _state.update { it.copy(
                        messages = it.messages + result.message
                    ) }
                }
                is ConnectionResult.Error -> {
                    _state.update { it.copy(
                        isConnected = false,
                        isConnecting = false,
                        errorMessage = result.message
                    ) }
                }
            }
        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _state.update { it.copy(
                    isConnected = false,
                    isConnecting = false,
                ) }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}