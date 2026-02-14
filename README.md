
# Z407 Remote Control - Android App

Aplicación Android para controlar los altavoces **Logitech Z407** mediante Bluetooth Low Energy (BLE).

<p align="center">  
  <img src="https://img.shields.io/badge/Android-13%2B-3DDC84?style=flat&logo=android&logoColor=white" alt="Android 13+">  
  <img src="https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin">  
  <img src="https://img.shields.io/badge/Jetpack%20Compose-1.5-4285F4?style=flat&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">  
</p>  

## ✨ Características

### Control de Audio
- 🔊 Control de volumen (subir/bajar)
- 🎵 Control de graves/bajos (subir/bajar)
- 🔇 Activar/desactivar silencio (mute)

### Gestión de Entradas
- 📱 Cambiar a entrada Bluetooth
- 🎧 Cambiar a entrada AUX
- 💾 Cambiar a entrada USB

### Conectividad
- 🔗 Conexión automática a Z407 via BLE
- 🔄 Retry automático con detección de errores
- 📡 Manejo inteligente de keep-alive
- 🔌 Desconexión segura

### Interfaz
- 🎨 UI moderna con Material Design 3
- 🌓 Paleta de colores personalizada (Slate/Emerald/Red)
- ⚡ Animaciones fluidas
- 📱 Responsive design

## 📋 Requisitos

- **Android 13.0 (API 33) o superior** - Solo APIs modernas
- **Bluetooth Low Energy (BLE)** habilitado
- **Altavoces Logitech Z407**

## 🏗️ Arquitectura

El proyecto sigue **Clean Architecture** con separación clara de responsabilidades:

### Estructura del Proyecto

```  
app/src/main/java/com/example/z407remotecontrol/  
├── data/                          # Capa de Datos  
│   ├── model/  
│   │   └── ConnectionState.kt     # Estados de conexión  
│   └── repository/  
│       └── AudioControlRepository.kt  
│  
├── domain/                        # Capa de Dominio  
│   └── usecase/  
│       └── AudioControlUseCase.kt # Lógica de negocio  
│  
├── ui/                            # Capa de Presentación  
│   ├── theme/                     # Tema y colores  
│   └── views/  
│       ├── HomeScreen.kt          # UI principal  
│       └── HomeViewModel.kt       # Estado de UI  
│  
└── services/                      # Capa de Infraestructura  
 └── BleService.kt              # Comunicación BLE
 ```  

### Flujo de Datos

```  
HomeScreen → HomeViewModel → AudioControlUseCase → AudioControlRepository → BleService → Android BLE APIs  
```  

## 🔧 Tecnologías y Librerías

### Core
- **Kotlin** - Lenguaje moderno y conciso
- **Jetpack Compose** - UI declarativa
- **Material Design 3** - Componentes modernos

### Arquitectura
- **Clean Architecture** - Separación por capas
- **MVVM** - Patrón de diseño
- **Repository Pattern** - Abstracción de datos

### Concurrencia
- **Coroutines** - Programación asíncrona
- **Flow** - Manejo reactivo de estados
- **StateFlow** - Estado observable

### Permisos
- **@RequiresPermission** - Documentación de permisos
- Runtime permissions - Android 13+

## 🔐 Permisos Requeridos

```xml  
<!-- Permisos BLE para Android 13+ -->  
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />  
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />  
```  

## 🔌 UUIDs de los Z407

Basado en el reverse engineering de [freundTech](https://github.com/freundTech/logi-z407-reverse-engineering):

```kotlin  
SERVICE_UUID  = 0000fdc2-0000-1000-8000-00805f9b34fb  
COMMAND_UUID  = c2e758b9-0e78-41e0-b0cb-98a593193fc5  
RESPONSE_UUID = b84ac9c6-29c5-46d4-bba1-9d534784330f  
```  

## 📡 Comandos Implementados

| Comando | Hex | Descripción |  
|---------|-----|-------------|  
| Handshake | `8405` | Inicializa la conexión con el dispositivo |  
| Keep Alive | `8400` | Respuesta al keep-alive del dispositivo |  
| Volume Up | `8002` | Incrementa el volumen en 1 nivel |  
| Volume Down | `8003` | Decrementa el volumen en 1 nivel |  
| Bass Up | `8000` | Incrementa los graves en 1 nivel |  
| Bass Down | `8001` | Decrementa los graves en 1 nivel |  
| Mute Toggle | `8004` | Activa/desactiva silencio |  
| Input Bluetooth | `8101` | Cambia a entrada Bluetooth |  
| Input AUX | `8102` | Cambia a entrada auxiliar |  
| Input USB | `8103` | Cambia a entrada USB |  
| Bluetooth Pair | `8200` | Inicia emparejamiento Bluetooth |  
| Factory Reset | `8300` | Restaura configuración de fábrica |  

## 🚀 Instalación

### Requisitos Previos
- Android Studio Hedgehog (2023.1.1) o superior
- Android SDK 33 (Android 13) o superior
- Dispositivo físico con Android 13+ (los emuladores no soportan BLE real)

### Pasos

1. **Clonar el repositorio**
 ```bash  
  git clone https://github.com/tu-usuario/z407remotecontrol.git  cd z407remotecontrol 
```  
2. **Abrir en Android Studio**
- File → Open
   - Selecciona la carpeta del proyecto

3. **Sincronizar Gradle**
- El proyecto sincronizará automáticamente
   - O ejecuta: `./gradlew build`

4. **Ejecutar la aplicación**
- Conecta tu dispositivo Android 13+
   - Click en "Run" o presiona `Shift + F10`

## 📱 Guía de Uso

1. **Enciende los altavoces Z407**
2. **Abre la aplicación**
3. **Concede permisos de Bluetooth** cuando se soliciten
4. **Presiona el botón de conexión** (rojo: desconectado, gris: buscando, verde: conectado)
5. **Usa los controles**:
   - Botones +/- para volumen
   - Botones +/- para graves
   - Iconos para cambiar entrada de audio

## 🎨 Paleta de Colores

```kotlin  
// Slate (Grises)  
Slate.slate50   = #f8fafc  
Slate.slate200  = #e2e8f0  
Slate.slate800  = #1e293b  
  
// Emerald (Verdes)  
Emerald.emerald500 = #10b981  
  
// Red (Rojos)  
Red.red500 = #ef4444  
```  

## 🔍 Características Técnicas

### Manejo de Conexión BLE
- ✅ Retry automático (hasta 3 intentos) en error 133
- ✅ Delay estratégico antes de conectar (500ms)
- ✅ Manejo de keep-alive automático
- ✅ Notificaciones habilitadas con descriptor CCCD
- ✅ Limpieza automática de recursos

### Estados de Conexión
```kotlin  
enum class ConnectionState {  DISCONNECTED,  // Sin conexión  
  SCANNING,      // Buscando dispositivo  
  CONNECTING,    // Conectando...  
  CONNECTED // Conectado y listo}  
```  

### Validación de Permisos
- Permisos documentados con `@RequiresPermission`
- Validación en tiempo de ejecución
- UI responsive según estado de permisos

## 🤝 Créditos

Basado en el reverse engineering de [freundTech](https://github.com/freundTech/logi-z407-reverse-engineering)

## 🐛 Problemas Conocidos

- ⚠️ Requiere Android 13+ (API 33)
- ⚠️ No funciona en emuladores sin BLE físico
- ⚠️ Los altavoces deben estar encendidos y en modo emparejamiento

---  

<p align="center">  
  Hecho con ❤️ y Kotlin  
</p>