import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    // Apply the official Android application plugin
    id("com.android.application")
    // Apply the official Kotlin Android plugin
    kotlin("android")
    // Apply Kotlin KAPT for annotation processors (needed by Room)
    kotlin("kapt")

}

android {
    namespace = "com.example.flighttracker"  // Change if desired
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.flighttracker"
        minSdk = 21
        targetSdk = 35
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

    // Java + Kotlin compile options
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val retrofitVersion = "2.9.0"

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Retrofit + GSON
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

// Room
    implementation(libs.androidx.room.runtime.v261)
    kapt(libs.androidx.room.compiler.v261)
    implementation(libs.androidx.room.ktx.v261)

// WorkManager
    implementation(libs.androidx.work.runtime.ktx.v290)

// Coroutines
    implementation(libs.kotlinx.coroutines.core.v173)
    implementation(libs.kotlinx.coroutines.android.v173)



    // -- AndroidX core libraries --
    implementation(libs.androidx.core.ktx.v1150)
    implementation(libs.androidx.appcompat.v170)
    implementation(libs.material.v180)
    implementation(libs.androidx.constraintlayout.v221)

    // -- ROOM (for database) --
    val roomVersion = "2.5.1"
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.play.services.maps)


    // -- WorkManager (for background jobs) --
    val workVersion = "2.8.1"
    implementation(libs.androidx.work.runtime.ktx)

    // -- Coroutines (optional but recommended) --
    val coroutinesVersion = "1.6.4"
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // (Optional) Retrofit + Gson if you want real flight APIs
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // -- Testing libs --
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v121)
    androidTestImplementation(libs.androidx.espresso.core.v361)
}
