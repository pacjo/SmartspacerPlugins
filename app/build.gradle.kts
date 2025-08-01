import com.google.protobuf.gradle.proto

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.protobuf")
}

android {
    namespace = "nodomain.pacjo.smartspacer.plugin"
    compileSdk = 36

    defaultConfig {
        applicationId = "nodomain.pacjo.smartspacer.plugin"
        minSdk = 29
        targetSdk = 36

        // 1000 * major + 100 * minor + patch (assigned randomly to force patch updates in manager)
        versionCode = 1600
        versionName = "1.6"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "default"
    productFlavors {
        create("localbattery") {
            applicationIdSuffix = ".localbattery"
        }
        create("genericweather") {
            applicationIdSuffix = ".genericweather"

            buildTypes {
                release {
                    isShrinkResources = false
                }
            }
        }
        create("sleepasandroid") {
            applicationIdSuffix = ".sleepasandroid"
        }
        create("duolingo") {
            applicationIdSuffix = ".duolingo"
        }
        create("livelygreeting") {
            applicationIdSuffix = ".livelygreeting"
        }
        create("anki") {
            applicationIdSuffix = ".anki"
        }
    }

    sourceSets {
        getByName("localbattery") {
            setRoot("src/localbattery/src")

            proto {
                srcDir("src/localbattery/src/proto")
            }
        }
        getByName("genericweather") {
            setRoot("src/genericweather/src")
        }
        getByName("sleepasandroid") {
            setRoot("src/sleepasandroid/src")
        }
        getByName("duolingo") {
            setRoot("src/duolingo/src")
        }
        getByName("livelygreeting") {
            setRoot("src/livelygreeting/src")
        }
        getByName("anki") {
            setRoot("src/anki/src")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.foundation:foundation-android:1.8.3")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("com.kieronquinn.smartspacer:sdk-plugin:1.1")
    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.datastore:datastore-preferences:1.2.0-alpha02")

    implementation("com.mikepenz:iconics-core:5.5.0-compose01")
    implementation("com.mikepenz:iconics-compose:5.5.0-compose01")
    implementation("com.mikepenz:community-material-typeface:7.0.96.1-kotlin")
    implementation("com.mikepenz:simple-icons-typeface:17.0.0.2")

    "genericweatherImplementation"("com.google.code.gson:gson:2.13.1")

    "localbatteryImplementation"("androidx.datastore:datastore:1.1.7")
    "localbatteryImplementation"("com.google.protobuf:protobuf-javalite:4.31.1")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.0"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}