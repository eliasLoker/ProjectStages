// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.3.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:${GlobalDependencies.kotlinVersion}")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:${GlobalDependencies.daggerHiltVersion}")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:${GlobalDependencies.navigationVersion}")
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