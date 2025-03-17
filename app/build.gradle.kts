plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Para Firebase
}

android {
    namespace = "com.example.java_capsa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.java_capsa"
        minSdk = 26 // Ajustado para compatibilidad con Firebase y otras librerías
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    // Librerías de Android
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Google Play Services
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1") // GPS y geolocalización

    // Fragmentos
    implementation("androidx.fragment:fragment-ktx:1.6.2") // Versión estable recomendada para Kotlin

    // Volley (HTTP Requests)
    implementation("com.android.volley:volley:1.2.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0")) // Gestiona versiones automáticamente
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-appcheck-debug")


    // Seguridad
    implementation("org.mindrot:jbcrypt:0.4")

    // Glide (Carga de imágenes)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Pruebas
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    
}
