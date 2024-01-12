package com.ondevop.bluetoothchat.presentation

import com.ondevop.bluetoothchat.domain.chat.BluetoothDevice
import com.ondevop.bluetoothchat.domain.chat.BluetoothMessage

data class BluetoothUiState(
    val scannedDevice: List<BluetoothDevice> = emptyList(),
    val paredDevices: List<BluetoothDevice> = emptyList(),
    val isConnected : Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList()

)
