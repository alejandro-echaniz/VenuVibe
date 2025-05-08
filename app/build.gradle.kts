import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

val secretProps = Properties().apply {
    file("secrets.properties").takeIf { it.exists() }
        ?.inputStream()
        ?.use { load(it) }
}



android {
    namespace = "com.example.venuvibe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.venuvibe"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue("string", "google_maps_key", secretProps.getProperty("MAPS_API_KEY", ""))
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.gms:play-services-ads:23.5.0" )  // Import advertisements
    implementation(platform("com.google.firebase:firebase-bom:33.5.0")) // Import the Firebase BoM
    implementation ("com.google.android.material:material:1.7.0")       // Import clock face time picker
    implementation("com.google.android.libraries.places:places:2.7.0")  // Import Places SDK
    implementation("com.google.android.gms:play-services-maps:18.1.0")  // import for Maps Dependency
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
}