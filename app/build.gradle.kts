plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "se.plugghjalpen"
    compileSdk = 34

    defaultConfig {
        applicationId = "se.plugghjalpen"
        minSdk = 26
        targetSdk = 34
        // CI passes the GitHub run number so every build is newer than the previous one
        val build = (project.findProperty("buildNumber") as String?)?.toIntOrNull() ?: 1
        versionCode = build
        versionName = "1.0.$build"
    }

    signingConfigs {
        // Fixed key (personal sideloaded app) so updates can be installed over older builds.
        create("plugg") {
            storeFile = file("plugghjalpen-release.jks")
            storePassword = "plugghjalpen"
            keyAlias = "plugg"
            keyPassword = "plugghjalpen"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("plugg")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("plugg")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("androidx.core:core:1.13.1")
}
