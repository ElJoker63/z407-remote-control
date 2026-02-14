package com.example.z407remotecontrol.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.z407remotecontrol.data.model.ConnectionState
import com.example.z407remotecontrol.ui.theme.BaseColors
import com.example.z407remotecontrol.ui.theme.Emerald
import com.example.z407remotecontrol.ui.theme.Gray
import com.example.z407remotecontrol.ui.theme.Red
import com.example.z407remotecontrol.ui.theme.Slate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun Test() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Z407 Remote")
                })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Preview de los 3 estados del botón
            Text("Estado: Desconectado", fontWeight = FontWeight.Bold)
            ConnectionButton(
                state = ConnectionState.DISCONNECTED,
                onConnect = { },
                onDisconnect = { }
            )

            Text("Estado: Buscando", fontWeight = FontWeight.Bold)
            ConnectionButton(
                state = ConnectionState.SCANNING,
                onConnect = { },
                onDisconnect = { }
            )

            Text("Estado: Conectado", fontWeight = FontWeight.Bold)
            ConnectionButton(
                state = ConnectionState.CONNECTED,
                onConnect = { },
                onDisconnect = { }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    // Observar estados del ViewModel
    val connectionState by viewModel.connectionState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Z407 Remote")
                })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón de conexión
            ConnectionButton(
                state = connectionState,
                onConnect = { viewModel.connect() },
                onDisconnect = { viewModel.disconnect() }
            )


            // Controles de volumen (solo visible cuando está conectado)
            if (connectionState == ConnectionState.CONNECTED) {
                Spacer(modifier = Modifier.weight(1f))

                Row {
                    Column {
                        AudioButtons(onUp = {
                            viewModel.volumeUp()
                        }, onDown = {
                            viewModel.volumeDown()
                        })

                        Spacer(modifier = Modifier.height(16.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Volume Icon",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            tint = Gray.gray800
                        )

                        Text(
                            "Volume",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                    Spacer(modifier = Modifier.width(64.dp))
                    Column {
                        AudioButtons(onUp = {
                            viewModel.bassUp()
                        }, onDown = {
                            viewModel.bassDown()
                        })

                        Spacer(modifier = Modifier.height(16.dp))

                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Bass Icon",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            tint = Gray.gray800
                        )

                        Text(
                            "Bass",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Row {
                    ControlButton(
                        icon = Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = "Toggle Mute",
                        onClick = {
                            viewModel.toggleMute()
                        },
                        color = Red.red500)
                }

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Row {
                    ControlButton(
                        icon = Icons.Default.Bluetooth,
                        contentDescription = "Bluetooth",
                        onClick = {
                            viewModel.setBluetoothMode()
                        },
                        onLongPress = {
                            viewModel.pairBluetooth()
                        })
                    ControlButton(
                        icon = Icons.Default.Usb, contentDescription = "USB", onClick = {
                            viewModel.setUsbMode()
                        })
                    ControlButton(
                        icon = Icons.Default.Cable, contentDescription = "AUX", onClick = {
                            viewModel.setAuxMode()
                        })
                }

                Spacer(modifier = Modifier.weight(1f))

                Row{
                    OutlinedButton(
                        onClick = {
                            viewModel.factoryReset()
                        }
                    ) {
                        Text("Factory Reset", fontSize = 18.sp)
                    }
                }

            }
        }
    }
}

@Composable
fun ConnectionButton(
    state: ConnectionState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    val (text, subtext, backgroundColor, contentColor, icon, onClick) = when (state) {
        ConnectionState.DISCONNECTED -> {
            Tuple6(
                "Disconnected",
                "Press to search",
                Red.red500,
                Color.White,
                Icons.Default.Bluetooth,
                onConnect
            )
        }
        ConnectionState.SCANNING,
        ConnectionState.CONNECTING -> {
            Tuple6(
                "Searching",
                "Looking for device...",
                Gray.gray200,
                Gray.gray800,
                Icons.Default.Bluetooth
            ) {}

        }
        ConnectionState.CONNECTED -> {
            Tuple6(
                "Connected",
                "Press to disconnect",
                Emerald.emerald500,
                Color.White,
                Icons.Default.Bluetooth,
                onDisconnect
            )
        }
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtext,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Indicador visual adicional
            when (state) {
                ConnectionState.SCANNING,
                ConnectionState.CONNECTING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = contentColor
                    )
                }
                else -> {
                    Canvas(
                        modifier = Modifier.size(12.dp)
                    ) {
                        drawCircle(color = contentColor)
                    }
                }
            }
        }
    }
}

// Clase auxiliar para tuplas
private data class Tuple6<A, B, C, D, E, F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F
)

@Preview(showBackground = true)
@Composable
fun AudioButtonsPreview() {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Botones de Audio", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Mantén presionado para cambio rápido", fontSize = 14.sp, color = Gray.gray600)

        AudioButtons(
            onUp = { },  // Click normal: +1
            onDown = { } // Click normal: -1
            // Long press: +1 cada 100ms mientras mantienes presionado
        )
    }
}

@Composable
fun AudioButtons(
    onUp: () -> Unit,
    onDown: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = BaseColors.white, shape = CircleShape)
            .border(
                border = BorderStroke(1.dp, Slate.slate200), shape = CircleShape
            )
            .padding(4.dp)
    ) {
        ControlButton(
            icon = Icons.Default.Add,
            contentDescription = "Subir volumen",
            onClick = onUp,
            onLongPressRepeat = onUp // Se repite mientras mantiene presionado
        )

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier
                .width(64.dp)
                .padding(5.dp, 20.dp),
            color = Slate.slate200
        )

        ControlButton(
            icon = Icons.Default.Remove,
            contentDescription = "Bajar volumen",
            onClick = onDown,
            onLongPressRepeat = onDown // Se repite mientras mantiene presionado
        )
    }
}

@Composable
fun ControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    onLongPressRepeat: (() -> Unit)? = null, // Nuevo: se repite continuamente
    longPressDelay: Long = 2000L,
    repeatDelay: Long = 100L, // Delay entre repeticiones (100ms por defecto)
    color: Color? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val longPressListener by rememberUpdatedState(onLongPress)
    val repeatListener by rememberUpdatedState(onLongPressRepeat)

    // Long Press simple (UNA VEZ) - para Bluetooth pairing
    if (onLongPress != null) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(longPressDelay)
                longPressListener?.invoke()
            }
        }
    }

    // Long Press REPETITIVO (continuamente mientras mantiene presionado) - para volumen/bass
    if (onLongPressRepeat != null) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                // Espera inicial antes de empezar a repetir (500ms)
                delay(500L)
                // Mientras siga presionado, repite la acción
                while (isPressed) {
                    repeatListener?.invoke()
                    delay(repeatDelay.coerceIn(1L, Long.MAX_VALUE))
                }
            }
        }
    }

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        interactionSource = interactionSource,
        colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
            contentColor = color ?: Gray.gray800
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp)
        )
    }
}