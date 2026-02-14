package com.example.z407remotecontrol.domain.usecase

import android.Manifest
import androidx.annotation.RequiresPermission
import com.example.z407remotecontrol.data.repository.AudioControlRepository
import com.example.z407remotecontrol.data.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

/**
 * Use Case para control de audio
 * Domain Layer - Lógica de negocio
 */
class AudioControlUseCase(
    private val repository: AudioControlRepository
) {

    val connectionState: StateFlow<ConnectionState> = repository.connectionState

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun connectToDevice() = repository.connect()

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun disconnect() = repository.disconnect()

    // Todas las funciones de control requieren BLUETOOTH_CONNECT
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun increaseVolume() = repository.volumeUp()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun decreaseVolume() = repository.volumeDown()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun increaseBass() = repository.bassUp()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun decreaseBass() = repository.bassDown()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun toggleMute() = repository.toggleMute()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun switchToBluetoothMode() = repository.setBluetoothMode()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun switchToAuxMode() = repository.setAuxMode()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun switchToUsbMode() = repository.setUsbMode()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startBluetoothPairing() = repository.pairBluetooth()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun performFactoryReset() = repository.factoryReset()

    fun setOnMaxRetriesReached(callback: () -> Unit) {
        repository.onMaxRetriesReached = callback
    }
}


