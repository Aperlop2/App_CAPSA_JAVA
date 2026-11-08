# 🩺 java_CAPSA — App de Gestión y Supervisión de Cuidadores

> Aplicación Android nativa (Java) para **administrar cuidadores, citas y evidencias de servicio con geolocalización en tiempo real**. Proyecto académico de código abierto orientado a la trazabilidad y supervisión del trabajo de cuidado.

[![Android](https://img.shields.io/badge/Platform-Android_14_%28API_34%29-3DDC84?logo=android&style=flat-square)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java-orange?logo=openjdk&style=flat-square)](https://www.java.com)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFA200?logo=firebase&style=flat-square)](https://firebase.google.com)
[![Gradle](https://img.shields.io/badge/Build-Gradle%208.4%20%28Kotlin%20DSL%29-023030?logo=gradle&style=flat-square)](https://gradle.org)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

---

## 🎥 Demostración en video (≈ 2 min)

<a href="https://www.youtube.com/shorts/0xEZRN4gTUk">
  <img src="https://i.ytimg.com/vi/0xEZRN4gTUk/oar2.jpg" width="240" alt="Demo java_CAPSA"/>
</a>

---

## 📑 Índice

- [Descripción del problema](#-descripción-del-problema)
- [Características principales](#-características-principales)
- [Capturas de pantalla](#-capturas-de-pantalla)
- [Arquitectura técnica](#-arquitectura-técnica)
- [Stack tecnológico](#-stack-tecnológico)
- [Modelo de datos](#-modelo-de-datos)
- [Requisitos previos](#-requisitos-previos)
- [Instalación y ejecución](#-instalación-y-ejecución)
- [Configuración de credenciales](#-configuración-de-credenciales)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Decisiones de diseño](#-decisiones-de-diseño)
- [Proceso de desarrollo y aprendizaje](#-proceso-de-desarrollo-y-aprendizaje)
- [Roadmap / Mejoras futuras](#-roadmap--mejoras-futuras)
- [Licencia y autor](#-licencia-y-autor)

---

## 🔍 Descripción del problema

La supervisión del trabajo de cuidadores (personas mayores, pacientes en domicilio) suele llevarse en papel o chats informales, sin registro verificable de **dónde**, **cuándo** y **cómo** se prestó cada servicio.

**java_CAPSA** cierra esa brecha con dos flujos diferenciados:

- 👤 **Cuidador:** registra la asistencia con **foto + ubicación GPS obtenida en el momento** (geocodificación inversa a dirección legible) y consulta sus citas asignadas desde un panel móvil con navegación inferior.
- 🛡️ **Administrador:** gestiona el alta/edición de cuidadores y citas, visualiza su **posición en Google Maps** y revisa el historial de evidencias con notificaciones.

---

## ✨ Características principales

| Módulo | Funcionalidad | Tecnología clave |
|---|---|---|
| **Autenticación** | Login, registro de admin/cuidador, recuperación de contraseña, cierre de sesión | Firebase Authentication |
| **Seguridad de credenciales** | Hash de contraseñas con salt antes de persistir | jBCrypt (`BCrypt.hashpw` + `gensalt`) |
| **Gestión de cuidadores** | Alta, edición, listado de activos con imágenes | Firebase Realtime Database, RecyclerView |
| **Gestión de citas** | Crear, editar, eliminar; retorno de datos entre actividades | `startActivityForResult`, Adapter pattern |
| **Evidencia de servicio** | Captura de foto + timestamp + dirección actual | Camera API, Fused Location Provider, Geocoder |
| **Mapa en tiempo real** | Marcadores de cuidadores, centrado automático, info-windows personalizados | Google Maps SDK, `SupportMapFragment` |
| **Notificaciones** | Panel emergente de notificaciones recientes con lista virtualizada | AlertDialog + RecyclerView |
| **Personalización visual** | Tema claro/oscuro persistente entre sesiones | SharedPreferences + `AppCompatDelegate` |
| **Sincronización offline** | Persistencia local de la base en tiempo real | `FirebaseDatabase.setPersistenceEnabled(true)` |
| **Almacenamiento de archivos** | Respaldo de evidencias multimedia | Firebase Storage |
| **Integración web** | POST de cuidadores/evidencias a backend propio (PHP/MySQL) | Volley (singleton `RequestQueue`) |

### Permisos declarados (`AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
```

## 🏗️ Arquitectura técnica

Aplicación modular por dominio dentro de un único módulo `app` (coherente con el alcance académico), organizada en capas reconocibles:

```
┌───────────────────────────────────────────────────────────┐
│  PRESENTACIÓN  Activities (19) + Layouts XML + Adapters    │
│  LoginPrincipal · gestionadministradores · Dashboard…      │
├───────────────────────────────────────────────────────────┤
│  DOMINIO  Modelos de datos                                 │
│  Cita · Cuidador · Evidencia · Notificacion                │
├───────────────────────────────────────────────────────────┤
│  DATOS / SERVICIOS                                         │
│  FirebaseAuth ─ RTDB ─ Firestore/Storage ─ SQLite Helper   │
│  VolleySingleton (red HTTP) ─ FusedLocation (GPS)          │
├───────────────────────────────────────────────────────────┤
│  INFRAESTRUCTURA                                           │
│  MyApp (Application): init Firebase + persistencia offline │
└───────────────────────────────────────────────────────────┘
```

**Patrones y prácticas aplicadas:**

- **Singleton** — `VolleySingleton` centraliza la `RequestQueue` y evita múltiples colas HTTP por actividad.
- **Adapter Pattern** — `CitaAdapter`, `CuidadorAdapter`, `EvidenciaAdapter` desacoplan modelos de la vista (`RecyclerView`).
- **Inicialización centralizada** — `MyApp extends Application` configura Firebase una sola vez por proceso y habilita caché offline.
- **Flujo protegido por autenticación** — guardas tipo `verificarAutenticacion()` redirigen al login si no hay sesión activa.
- **Configuración moderna de builds** — Gradle con **Kotlin DSL** y *Version Catalog* (`gradle/libs.versions.toml`) para gobernar dependencias de forma tipada y centralizada.

---

## 🧰 Stack tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 8 (source/target compatibility) |
| Plataforma | Android SDK | `minSdk/targetSdk/compileSdk = 34` |
| Build system | Gradle + AGP (Kotlin DSL, Version Catalog) | Gradle 8.4 · AGP 8.3.0 |
| UI | Material Components, ConstraintLayout, RecyclerView | Material 1.12.0 · ConstraintLayout 2.2.0 |
| Autenticación | Firebase Authentication | BOM 33.7.0 |
| Base de datos | Firebase Realtime Database (+ Firestore disponible) | BOM 33.7.0 |
| Archivos | Firebase Storage | 21.0.1 |
| Geolocalización | Play Services Location (Fused Location Provider) | 21.3.0 |
| Mapas | Google Maps SDK for Android | 19.0.0 |
| Red HTTP | Volley | 1.2.1 |
| Criptografía | jBCrypt (hashing de contraseñas) | 0.4 |
| Pruebas | JUnit 4 · Espresso · AndroidJUnitRunner | 4.13.2 · 3.6.1 |

---

## 🗄️ Modelo de datos

**Firebase Realtime Database (esquema lógico):**

```json
{
  "usuarios": {
    "administradores": { "<uid>": { "nombre": "...", "email": "...", "pass_hash": "$2a$10$..." } },
    "cuidadores":      { "<uid>": { "nombre": "...", "email": "...", "estado": "activo", "lat": 0.0, "lng": 0.0 } }
  }
}
```

**SQLite local (`DatabaseHelper` — tabla de evidencias):**

```sql
CREATE TABLE evidencias (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_cuidador TEXT,
    ubicacion       TEXT,
    descripcion     TEXT,
    foto            TEXT,   -- codificada en Base64
    fecha_hora      DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

> La migración de esquema se controla por versión (`DATABASE_VERSION = 2`) con `ALTER TABLE` incremental en `onUpgrade()`, preservando los datos existentes.

---

## ⚙️ Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Android Studio | Hedgehog o superior (JBR 17 incluido) |
| JDK | 17 |
| Android SDK Platform | API 34 |
| Dispositivo/Emulador | Android 14 (API 34) con Play Services |
| Cuenta Google Cloud | Con **Maps SDK for Android** habilitado |
| Proyecto Firebase | En el mismo proyecto de Google Cloud |

---

## 🚀 Instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone https://github.com/Aperlop2/App_CAPSA_JAVA.git
cd App_CAPSA_JAVA

# 2. Colocar la configuración de Firebase (ver sección siguiente)
#    app/google-services.json

# 3. Sincronizar y compilar
./gradlew clean assembleDebug

# 4. Instalar en dispositivo/emulador conectado
adb install app/build/outputs/apk/debug/app-debug.apk

# (Opcional) Ejecutar pruebas unitarias e instrumentadas
./gradlew test connectedAndroidTest
```

También puedes abrir la carpeta raíz directamente en **Android Studio** y pulsar ▶ *Run*.

---

## 🔑 Configuración de credenciales

1. **Firebase:** crea un proyecto en [console.firebase.google.com](https://console.firebase.google.com), añade la app Android con `applicationId = com.example.java_capsa`, activa **Authentication (Email/Password)**, **Realtime Database**, **Firestore** y **Storage**, y descarga `google-services.json` dentro de `app/`.
2. **Google Maps:** habilita *Maps SDK for Android* en Google Cloud Console, genera una API key restringida al paquete `com.example.java_capsa` + firma SHA-1 de tu `debug.keystore`, y sustituye el valor de `<meta-data android:name="com.google.android.geo.API_KEY">` en `AndroidManifest.xml`.
3. **Backend opcional (Volley):** si usas el respaldo PHP/MySQL, ajusta la URL del endpoint en `ScanActivity` y `AgregarNuevoCuidador` (actualmente apuntan a una IP de red local).

> 🔒 **Buenas prácticas de secretos aplicadas en el repo:** `google-services.json`, keystores (`.jks/.keystore`) y la carpeta `.idea/` están excluidos vía `.gitignore`; las claves deben inyectarse por variables de entorno o CI/CD en producción. Nunca versionar credenciales reales.

---

## 📂 Estructura del proyecto

```
App_CAPSA_JAVA/
├── app/
│   ├── build.gradle.kts            # Dependencias (Firebase, Maps, Volley, BCrypt)
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml     # Permisos, actividades y API key de Maps
│       ├── java/com/example/java_capsa/
│       │   ├── MainActivity.java · LoginPrincipal.java · RecuperarContrasena.java
│       │   ├── gestionadministradores.java    # Panel admin + tema claro/oscuro
│       │   ├── DashboardCuidadorActivity.java # Navegación inferior del cuidador
│       │   ├── ScanActivity.java              # Foto + GPS + Geocoder + subida
│       │   ├── MapaTiempoReal.java            # Google Maps + marcadores
│       │   ├── GestionDeCitas / AgregarCita / EditarCita
│       │   ├── GestionDeCuidadores / AgregarNuevoCuidador / EditarCuidador
│       │   ├── HistorialEvidencias.java       # SQLite + adapter
│       │   ├── Modelos: Cita · Cuidador · Evidencia
│       │   ├── Adapters: CitaAdapter · CuidadorAdapter · EvidenciaAdapter
│       │   └── Infra: MyApp · DatabaseHelper · VolleySingleton
│       └── res/                    # 35+ layouts, drawables, fuentes, temas
├── gradle/libs.versions.toml       # Version Catalog (dependencias centralizadas)
├── build.gradle.kts · settings.gradle.kts
└── README.md
```

---

## 🧠 Decisiones de diseño

| Decisión | Justificación | Alternativa descartada |
|---|---|---|
| **Java puro** sobre Kotlin | Alineado con los requisitos del currículo académico y legibilidad para evaluación docente | Kotlin (curva adicional para el equipo) |
| **Realtime Database + persistencia offline** | Sincronización instantánea de posiciones/estados de cuidadores con caché nativa | Solo Firestore (offline manual por colección) |
| **BCrypt para hashing local** | La contraseña nunca se guarda en texto plano; hash con salt adaptable | MD5/SHA-1 (inseguros frente a GPU cracking) |
| **Volley para POST a backend propio** | Cola de peticiones simple, reintentos e integración con ciclo de vida | Retrofit (overkill para 2 endpoints académicos) |
| **Geocodificación inversa on-device** | Dirección legible junto a la evidencia sin coste de API adicional | API externa de geocoding (límites de cuota) |
| **Version Catalog (`libs.versions.toml`)** | Versiones únicas y tipadas para todo el proyecto, actualización sencilla | Declaración dispersa en bloques `buildscript` |

---

## 📈 Proceso de desarrollo y aprendizaje

Proyecto desarrollado en **Git con historial de commits verificable**, avanzando por iteraciones funcionales: autenticación → gestión CRUD → geolocalización → mapas → evidencias → tematización.

Competencias demostradas: integración multi-servicio (Auth + BD + Storage + Maps + HTTP), manejo de permisos en tiempo de ejecución, diseño de migraciones de base de datos locales, patrones de arquitectura móvil y configuración profesional de builds con Gradle Kotlin DSL.

---

## 🗺️ Roadmap / Mejoras futuras

- [ ] Migración progresiva de actividades críticas a **Kotlin** con `ViewModel` + `LiveData`.
- [ ] **Security Rules** de Firebase por rol (admin vs. cuidador).
- [ ] Externar URLs de backend a `BuildConfig`/`local.properties` (elimina IPs hardcodeadas).
- [ ] Guardar la foto en archivo (FileProvider) en lugar de bitmap/Base64 para reducir uso de memoria.
- [ ] Cobertura de pruebas unitarias en casos de uso (auth, adapters, helpers).
- [ ] CI con GitHub Actions: `assembleDebug` + `lint` + `test` en cada PR.
- [ ] Backend/API documentada (OpenAPI) y refactor de endpoints a HTTPS.

---

## 🤝 Contribuir

Al ser un proyecto académico de código abierto, las mejoras son bienvenidas:

```bash
git checkout -b feature/nombre-mejora
git commit -m "feat(modulo): descripcion clara del cambio"
git push origin feature/nombre-mejora
```

Abre un Pull Request describiendo el problema resuelto y cómo probarlo.

---

## 📄 Licencia y autor

Distribuido bajo licencia **MIT** (ver `LICENSE`). Proyecto académico de código abierto.

**Autor:** ANA CRISTINA PÉREZ LÓPEZ — Aperlop2 (https://github.com/Aperlop2) · anacristinaperez.dev01@gmail.com · LINKEDIN/(https://www.linkedin.com/in/ana-cristina-perez-lopez/)

> Si este proyecto te resulta útil, considéralo una ⭐ — ayuda a visibilizar el trabajo.
