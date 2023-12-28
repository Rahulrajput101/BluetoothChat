package com.ondevop.bluetoothchat.domain.chat

sealed interface ConnectionResult {
    object ConnectionEstablished : ConnectionResult
    data class Error(val message : String): ConnectionResult
}