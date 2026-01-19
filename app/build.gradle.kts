import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dev.mb_labs.travelshare"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "dev.mb_labs.travelshare"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //read MAPS_API_KEY from local.properties
        val keystoreFile = project.rootProject.file("local.properties")
        val properties = Properties()
        if (keystoreFile.exists()) {
            properties.load(keystoreFile.inputStream())
        }
        val mapsKey = properties.getProperty("MAPS_API_KEY") ?: ""

        manifestPlaceholders["MAPS_API_KEY"] = mapsKey
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //networking (Retrofit & Gson)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //image Loading (Glide)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //security
    implementation("androidx.security:security-crypto:1.1.0")

    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    //Google Places SDK
    implementation("com.google.android.libraries.places:places:3.3.0")

    //Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version") //for Java

    //Gson for converting Lists to Strings (TypeConverters)
    implementation("com.google.code.gson:gson:2.10.1")
}