package com.example.z407remotecontrol.data.repository

import android.Manifest
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.z407remotecontrol.data.model.ConnectionState
import com.example.z407remotecontrol.services.BleService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Repository para control de audio Z407
 * Data Layer - Maneja la comunicación BLE y comandos específicos del dispositivo
 */
class AudioControlRepository(context: Context) {

    companion object {
        private const val TAG = "AudioControlRepo"

        // UUIDs para Z407
        private val SERVICE_UUID = UUID.fromString("0000fdc2-0000-1000-8000-00805f9b34fb")
        private val COMMAND_UUID = UUID.fromString("c2e758b9-0e78-41e0-b0cb-98a593193fc5")
        private val RESPONSE_UUID = UUID.fromString("b84ac9c6-29c5-46d4-bba1-9d534784330f")

        // Comandos Z407
        private const val CMD_BASS_UP = "8000"
        private const val CMD_BASS_DOWN = "8001"
        private const val CMD_VOLUME_UP = "8002"
        private const val CMD_VOLUME_DOWN = "8003"
        private const val CMD_MUTE_TOGGLE = "8004"
        private const val CMD_INPUT_BLUETOOTH = "8101"
        private const val CMD_INPUT_AUX = "8102"
        private const val CMD_INPUT_USB = "8103"
        private const val CMD_BLUETOOTH_PAIR = "8200"
        private const val CMD_FACTORY_RESET = "8300"
        private const val CMD_HANDSHAKE = "8405"
        private const val CMD_KEEPALIVE = "8400"
    }

    private val bleService = BleService(context)
    private var commandCharacteristic: BluetoothGattCharacteristic? = null

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    var onMaxRetriesReached: (() -> Unit)? = null

    private var isCallbacksSetup = false

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun setupBleCallbacks() {
        if (isCallbacksSetup) return
        isCallbacksSetup = true
        bleService.onServicesDiscovered = { gatt ->
            val service = gatt.getService(SERVICE_UUID)
            commandCharacteristic = service?.getCharacteristic(COMMAND_UUID)
            val responseCharacteristic = service?.getCharacteristic(RESPONSE_UUID)

            responseCharacteristic?.let { characteristic ->
                bleService.enableNotifications(characteristic)
                Log.d(TAG, "✅ Notificaciones habilitadas")
            }

            Handler(Looper.getMainLooper()).postDelayed({
                sendCommand(CMD_HANDSHAKE)
            }, 200)
        }

        bleService.onCharacteristicChanged = { data ->
            handleDeviceResponse(data)
        }

        bleService.onConnectionSuccess = {
            _connectionState.value = ConnectionState.CONNECTED
        }

        bleService.onConnectionFailed = { error ->
            Log.e(TAG, "❌ Error: $error")
            _connectionState.value = ConnectionState.DISCONNECTED
        }

        bleService.onMaxRetriesReached = {
            onMaxRetriesReached?.invoke()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun connect() {
        setupBleCallbacks()
        _connectionState.value = ConnectionState.SCANNING
        bleService.startScan(SERVICE_UUID)
        Log.d(TAG, "🔍 Buscando Z407...")
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun disconnect() {
        bleService.disconnect()
        _connectionState.value = ConnectionState.DISCONNECTED
        Log.d(TAG, "🔌 Desconectado")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun volumeUp() {
        sendCommand(CMD_VOLUME_UP)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun volumeDown() {
        sendCommand(CMD_VOLUME_DOWN)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun bassUp() {
        sendCommand(CMD_BASS_UP)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun bassDown() {
        sendCommand(CMD_BASS_DOWN)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun toggleMute() {
        sendCommand(CMD_MUTE_TOGGLE)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setBluetoothMode() {
        sendCommand(CMD_INPUT_BLUETOOTH)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setAuxMode() {
        sendCommand(CMD_INPUT_AUX)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setUsbMode() {
        sendCommand(CMD_INPUT_USB)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun pairBluetooth() {
        sendCommand(CMD_BLUETOOTH_PAIR)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun factoryReset() {
        sendCommand(CMD_FACTORY_RESET)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun sendCommand(command: String) {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            Log.w(TAG, "⚠️ No conectado")
            return
        }

        commandCharacteristic?.let { characteristic ->
            val bytes = command.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            bleService.writeCharacteristic(characteristic, bytes)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun handleDeviceResponse(data: ByteArray) {
        val hex = data.joinToString("") { "%02x".format(it) }

        when (hex) {
            "d40501" -> sendCommand(CMD_KEEPALIVE)
            "d40001" -> Log.d(TAG, "✅ Handshake OK")
        }
    }
}

