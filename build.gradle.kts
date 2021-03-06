// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val kotlin = "1.5.21"
    val navigation = "2.3.5"

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")

        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.35")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$navigation")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
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