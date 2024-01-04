plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "nodomain.pacjo.smartspacer.plugin"
    compileSdk = 34

    defaultConfig {
        applicationId = "nodomain.pacjo.smartspacer.plugin"
        minSdk = 29
        targetSdk = 34
        versionCode = 10
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
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
            applicationId = "localbattery"
            applicationId = "nodomain.pacjo.smartspacer.plugin.localbattery"
        }
        create("genericweather") {
            applicationId = "genericweather"
            applicationId = "nodomain.pacjo.smartspacer.plugin.genericweather"
        }
        create("sleepasandroid") {
            applicationId = "sleepasandroid"
            applicationId = "nodomain.pacjo.smartspacer.plugin.sleepasandroid"
        }
        create("duolingo") {
            applicationId = "duolingo"
            applicationId = "nodomain.pacjo.smartspacer.plugin.duolingo"
        }
    }

    sourceSets {
        getByName("localbattery") {
            setRoot("src/localbattery/src")
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
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
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.foundation:foundation-android:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("com.kieronquinn.smartspacer:sdk-plugin:1.0.3")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.6")

    "genericweatherImplementation"("com.google.code.gson:gson:2.10.1")
}