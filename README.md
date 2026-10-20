THERIONLABS SpA — Captura de Leads en Terreno (Duoc UC)

Aplicación móvil nativa desarrollada para la captura, validación y gestión de prospectos académicos en terreno durante ferias vocacionales y eventos educativos para Duoc UC, desarrollada por THERIONLABS SpA.

📋 Resumen del Caso
Los ejecutivos en terreno suelen enfrentar dificultades para recopilar información debido a conexiones de red inestables o nulas en recintos educativos. Este proyecto soluciona el problema implementando una arquitectura Offline-First, permitiendo la captura confiable, la validación estricta de datos en tiempo real, el registro de credenciales fotográficas y la persistencia local instantánea.

🚀 Características Principales

Arquitectura MVVM limpia: Separación estricta de responsabilidades entre la UI reactiva, los ViewModels con flujos StateFlow y la capa de repositorio.
Persistencia local con Room: Almacenamiento SQLite mediante LeadDatabase, LeadDao y LeadEntity, gestionando el estado de sincronización (estaSincronizado = false) para subidas posteriores.
Monitoreo de Red Reactivo: Integración de NetworkMonitor con ConnectivityManager que informa en vivo si el dispositivo está en línea o guardando en búfer local.
Validaciones en tiempo real: Verificación de campos obligatorios con expresiones regulares (nombres con longitud mínima, email estándar y teléfonos de 9 dígitos chilenos).
Acceso a Cámara Nativa: Captura instantánea de fotografías de respaldo (credenciales/documentos) y preview en miniatura antes de guardar.
Diseño Corporativo e Institucional: Interfaz moderna en Modo Oscuro con los colores corporativos de Duoc UC (Azul Marino #0F172A, Amarillo/Oro #FFAE00 y Esmeralda #10B981).

🛠️ Tecnologías y Librerías

Lenguaje: Kotlin
Framework UI: Jetpack Compose (Material 3)
Procesamiento de Anotaciones: Kotlin Symbol Processing (KSP 2.0.21-1.0.28)
Base de Datos: Room (v2.6.1)
Navegación: Jetpack Navigation Compose
Ciclo de vida y Estado: AndroidX Lifecycle ViewModel Compose, StateFlow y Corrutinas

📂 Estructura del Proyecto

com.example.therionlabs_spa/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── LeadDao.kt
│   │   └── LeadEntity.kt
│   └── repository/
│       └── LeadRepository.kt
├── ui/
│   ├── components/
│   │   └── ValidatedTextField.kt
│   ├── navigation/
│   │   └── AppNavigation.kt
│   ├── screens/
│   │   ├── LeadFormScreen.kt
│   │   └── LeadListScreen.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/
│       ├── LeadFormUiState.kt
│       ├── LeadFormViewModel.kt
│       └── ValidationUtils.kt
└── util/
    ├── CameraManager.kt
    └── NetworkMonitor.kt


⚙️ Configuración y Ejecución

Clonar el repositorio:
git clone https://github.com/tu-usuario/THERIONLABS_SpA.git

Abrir en Android Studio: Abrir la carpeta raíz como proyecto de Gradle existente.

Verificar gradle.properties:
Asegúrate de contar con la propiedad requerida para KSP:

android.disallowKotlinSourceSets=false


Sincronizar y Compilar:
Ejecutar un Sync Project with Gradle Files.
Ejecutar en un emulador con API 33+ (ej. Pixel 8 con Android 14/15).

👤 Autores
Matias Iturrieta — Ingenieria en Informatica
Dalexandro Castillo - Ingenieria en Informatica
Emerson Marchant -  Ingenieria en Informatica
Institución: Duoc UC · Escuela de Informática y Telecomunicaciones
