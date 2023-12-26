package com.ondevop.bluetoothchat.presentation

import com.ondevop.bluetoothchat.domain.chat.BluetoothDevice

data class BluetoothUiState(
    val scannedDevice: List<BluetoothDevice> = emptyList(),
    val paredDevices: List<BluetoothDevice> = emptyList(),
)
