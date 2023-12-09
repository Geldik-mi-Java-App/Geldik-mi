plugins {
    id("com.android.application")
}

android {
    namespace = "com.oguzcanaygun.loginregister"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.oguzcanaygun.loginregister"
        minSdk = 24
        targetSdk = 33
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Apply the Android Gradle Plugin version in the buildscript block
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
        // Replace "7.0.0" with the actual version you want to use
    }
}
