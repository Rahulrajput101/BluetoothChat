package com.ondevop.bluetoothchat.domain.chat

data class BluetoothMessage (
    val message: String,
    val senderName: String,
    val isFromLocal: Boolean
)