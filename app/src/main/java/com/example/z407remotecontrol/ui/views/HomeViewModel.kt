package com.example.z407remotecontrol.ui.views

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.z407remotecontrol.data.repository.AudioControlRepository
import com.example.z407remotecontrol.data.model.ConnectionState
import com.example.z407remotecontrol.domain.usecase.AudioControlUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para HomeScreen
 * Presentation Layer
 *
 * Los permisos BLE se validan en tiempo de ejecución en HomeScreen
 * mediante RequestPermissions antes de llamar estas funciones
 */
@SuppressLint("MissingPermission")
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AudioControlRepository(application)
    private val audioControlUseCase = AudioControlUseCase(repository)

    val connectionState: StateFlow<ConnectionState> =
        audioControlUseCase.connectionState.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ConnectionState.DISCONNECTED
        )

    init {
        audioControlUseCase.setOnMaxRetriesReached {
            // TODO: Mostrar toast o snackbar
        }
    }

    fun connect() = viewModelScope.launch {
        audioControlUseCase.connectToDevice()
    }

    fun disconnect() = viewModelScope.launch {
        audioControlUseCase.disconnect()
    }

    fun volumeUp() = viewModelScope.launch {
        audioControlUseCase.increaseVolume()
    }

    fun volumeDown() = viewModelScope.launch {
        audioControlUseCase.decreaseVolume()
    }

    fun bassUp() = viewModelScope.launch {
        audioControlUseCase.increaseBass()
    }

    fun bassDown() = viewModelScope.launch {
        audioControlUseCase.decreaseBass()
    }

    fun factoryReset() = viewModelScope.launch {
        audioControlUseCase.performFactoryReset()
    }

    fun setBluetoothMode() = viewModelScope.launch {
        audioControlUseCase.switchToBluetoothMode()
    }

    fun setAuxMode() = viewModelScope.launch {
        audioControlUseCase.switchToAuxMode()
    }

    fun setUsbMode() = viewModelScope.launch {
        audioControlUseCase.switchToUsbMode()
    }

    fun toggleMute() = viewModelScope.launch {
        audioControlUseCase.toggleMute()
    }

    fun pairBluetooth() = viewModelScope.launch {
        audioControlUseCase.startBluetoothPairing()
    }
}


