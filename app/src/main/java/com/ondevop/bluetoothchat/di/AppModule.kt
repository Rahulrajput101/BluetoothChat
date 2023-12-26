package com.ondevop.bluetoothchat.di

import android.content.Context
import com.ondevop.bluetoothchat.data.chat.AndroidBluetoothController
import com.ondevop.bluetoothchat.domain.chat.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothController(
        @ApplicationContext app : Context
    ) : BluetoothController{
        return AndroidBluetoothController(app)
    }
}