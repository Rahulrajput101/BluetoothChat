package com.ondevop.bluetoothchat.data.chat

import android.bluetooth.BluetoothManager
import com.ondevop.bluetoothchat.domain.chat.BluetoothMessage

fun String.toBluetoothMessage(isFromLocalUser : Boolean) : BluetoothMessage {
     val name = substringBeforeLast("#")
     val message = substringAfter("#")

    return BluetoothMessage(
        senderName = name,
        message = message,
        isFromLocal = isFromLocalUser
    )
}
fun BluetoothMessage.toByteArray() : ByteArray{
     return "$senderName#$message".encodeToByteArray()
}