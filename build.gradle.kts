// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val kotlinVersion by rootProject.extra { "1.7.21" }
    val navVersion by rootProject.extra { "2.5.3" }
    val daggerHiltVersion by rootProject.extra { "2.42" }

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.3.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:$daggerHiltVersion")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:${navVersion}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}