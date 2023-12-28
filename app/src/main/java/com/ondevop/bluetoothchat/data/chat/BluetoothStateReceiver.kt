package com.ondevop.bluetoothchat.data.chat

import android.content.BroadcastReceiver
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Build

class BluetoothStateReceiver(
    private val onSateChanged: (isConnected: Boolean , BluetoothDevice) -> Unit
) : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        val device= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )
        }else{
            intent?.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE
            )
        }

        when(intent?.action){
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                onSateChanged(true , device ?: return)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                onSateChanged(true , device ?: return)
            }

        }
    }

}