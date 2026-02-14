package com.example.z407remotecontrol.services

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.UUID

/**
 * Servicio BLE genérico para manejo de conexiones Bluetooth Low Energy
 *
 * Requiere Android 13+ (API 33+)
 *
 * @RequiresPermission - Documenta qué permisos necesita cada función
 *   - BLUETOOTH_SCAN: Para buscar dispositivos BLE
 *   - BLUETOOTH_CONNECT: Para conectar/desconectar dispositivos BLE
 *   - El IDE te avisará si llamas estas funciones sin los permisos adecuados
 */
class BleService(private val context: Context) {

    companion object {
        private const val TAG = "BleService"
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 2000L
        private const val CONNECT_DELAY_MS = 500L
    }

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private var bluetoothGatt: BluetoothGatt? = null
    private var retryCount = 0
    private var pendingDevice: BluetoothDevice? = null

    // Callbacks para la capa superior
    var onServicesDiscovered: ((BluetoothGatt) -> Unit)? = null
    var onCharacteristicChanged: ((ByteArray) -> Unit)? = null
    var onConnectionSuccess: (() -> Unit)? = null
    var onConnectionFailed: ((String) -> Unit)? = null
    var onMaxRetriesReached: (() -> Unit)? = null


    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.d(TAG, "📡 onConnectionStateChange - status: $status, newState: $newState")

            when {
                status == 133 -> {
                    Log.e(TAG, "❌ Error 133 (GATT_ERROR) - Reintentando... (${retryCount + 1}/$MAX_RETRIES)")
                    cleanup()

                    if (retryCount < MAX_RETRIES) {
                        retryCount++
                        Handler(Looper.getMainLooper()).postDelayed({
                            pendingDevice?.let { device ->
                                Log.d(TAG, "🔄 Reintentando conexión...")
                                connectToDevice(device)
                            }
                        }, RETRY_DELAY_MS)
                    } else {
                        Log.e(TAG, "❌ Máximo de reintentos alcanzado")
                        retryCount = 0
                        pendingDevice = null
                        onMaxRetriesReached?.invoke()
                    }
                }
                newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS -> {
                    Log.d(TAG, "✅ Dispositivo conectado")
                    retryCount = 0
                    pendingDevice = null
                    gatt?.discoverServices()
                    onConnectionSuccess?.invoke()
                }
                newState == BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "🔌 Dispositivo desconectado")
                    cleanup()
                }
                status != BluetoothGatt.GATT_SUCCESS -> {
                    Log.e(TAG, "❌ Error de conexión - status: $status")
                    onConnectionFailed?.invoke("Error de conexión: $status")
                    cleanup()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS && gatt != null) {
                Log.d(TAG, "✅ Servicios descubiertos")
                onServicesDiscovered?.invoke(gatt)
            } else {
                Log.e(TAG, "❌ Error descubriendo servicios: $status")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            onCharacteristicChanged?.invoke(value)
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "✅ Descriptor escrito exitosamente")
            } else {
                Log.e(TAG, "❌ Error escribiendo descriptor: $status")
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "✅ Característica escrita exitosamente")
            } else {
                Log.e(TAG, "❌ Error escribiendo característica: $status")
            }
        }
    }

    private val scanCallback = object : ScanCallback() {
        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                Log.d(TAG, "📱 Dispositivo encontrado: ${device.address}")
                stopScan()
                connectToDevice(device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "❌ Error en escaneo: $errorCode")
            onConnectionFailed?.invoke("Error en escaneo: $errorCode")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(serviceUuid: UUID) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e(TAG, "❌ Bluetooth no disponible o no habilitado")
            return
        }

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(serviceUuid))
            .build()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bluetoothAdapter.bluetoothLeScanner?.startScan(
            listOf(scanFilter),
            scanSettings,
            scanCallback
        )

        Log.d(TAG, "🔍 Escaneo iniciado para UUID: $serviceUuid")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        Log.d(TAG, "⏸️ Escaneo detenido")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectToDevice(device: BluetoothDevice) {
        pendingDevice = device

        Log.d(TAG, "🔗 Conectando a ${device.address}...")

        Handler(Looper.getMainLooper()).postDelayed({
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        }, CONNECT_DELAY_MS)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun enableNotifications(characteristic: BluetoothGattCharacteristic): Boolean {
        val gatt = bluetoothGatt ?: return false

        gatt.setCharacteristicNotification(characteristic, true)

        val descriptor = characteristic.getDescriptor(
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        )

        return if (descriptor != null) {
            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            true
        } else {
            false
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, data: ByteArray): Boolean {
        val gatt = bluetoothGatt ?: return false

        val result = gatt.writeCharacteristic(
            characteristic,
            data,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )

        return result == BluetoothGatt.GATT_SUCCESS
    }


    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun disconnect() {
        Log.d(TAG, "🔌 Desconectando...")
        stopScan()
        retryCount = 0
        pendingDevice = null
        bluetoothGatt?.disconnect()
        cleanup()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun cleanup() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}

