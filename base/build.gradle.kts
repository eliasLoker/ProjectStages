plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.projectstages.base"
    compileSdk = 33

    defaultConfig {
        minSdk = 23
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    Dependencies.getBaseDependencies().forEach {
        when(it.dependencyType) {
            is Dependencies.DependencyType.Implementation -> implementation(it.dependency)
            is Dependencies.DependencyType.TestImplementation -> testImplementation(it.dependency)
            is Dependencies.DependencyType.AndroidTestImplementation -> androidTestImplementation(it.dependency)
            else -> {
                /*
                do nothing
                */
            }
        }
    }
}