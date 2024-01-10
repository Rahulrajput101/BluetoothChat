package com.ondevop.bluetoothchat.domain.chat

sealed interface ConnectionResult {
    object ConnectionEstablished : ConnectionResult

    data class TransferredSucceeded(val message: BluetoothMessage): ConnectionResult
    data class Error(val message : String): ConnectionResult
}