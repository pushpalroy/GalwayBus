import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
}


val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
try {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}
catch(e: Exception) {
}

val versionMajor = 1
val versionMinor = 1

val versionNum: String? by project

fun versionCode(): Int {
    versionNum?.let {
        val code: Int = (versionMajor * 1000000) + (versionMinor * 1000) + it.toInt()
        println("versionCode is set to $code")
        return code
    } ?: return 1
}

fun versionName(): String {
    versionNum?.let {
        val name = "${versionMajor}.${versionMinor}.${versionNum}"
        println("versionName is set to $name")
        return name
    } ?: return "1.0"
}


android {
    compileSdk = AndroidSdk.compile

    signingConfigs {

        getByName("debug") {
            keyAlias = "debug"
            keyPassword = "android"
            storeFile= file("../debug.jks")
            storePassword = "android"
        }

        create("release") {
            storeFile = file("/Users/joreilly/dev/keystore/galwaybus_android.jks")
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storePassword = keystoreProperties["storePassword"] as String?
            enableV2Signing = true
        }
    }

    defaultConfig {
        applicationId = "dev.johnoreilly.galwaybus"
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target

        this.versionCode = versionCode()
        this.versionName = versionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val googleMapsKey = System.getenv("GOOGLE_API_KEY") ?: "test"
        resValue("string", "google_maps_key", googleMapsKey)
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }


    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += setOf("META-INF/*.kotlin_module")
        }
    }

    namespace = "dev.johnoreilly.galwaybus"
}


dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    implementation(platform("com.google.firebase:firebase-bom:26.2.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("androidx.activity:activity-compose:1.7.2")


    with(Deps.Koin) {
        implementation(core)
        implementation(android)
    }

    with(Deps.Compose) {
        implementation(ui)
        implementation(uiGraphics)
        implementation(uiTooling)
        implementation(foundationLayout)
        implementation(material)
        implementation(materialIconsExtended)
        implementation(navigation)
        implementation(accompanistPlaceholder)
        implementation(accompanistSwipeRefresh)
        implementation(mapsCompose)
        implementation(mapsComposeUtils)

        implementation(material3)
        implementation(material3WindowSizeClass)
    }
    implementation("io.github.pushpalroy:jetlime:1.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")
    implementation("com.google.maps.android:android-maps-utils:2.3.0")

    with(Deps.PlayServices) {
        implementation(maps)
        implementation(location)
    }

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(project(":SharedCode"))
}

