// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath ("com.google.gms:google-services:4.3.15")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.9.4")
    }
}
plugins {
    id ("com.android.application") version "7.4.0" apply false
    id ("com.android.library") version "7.4.0" apply false
    id ("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id ("com.google.firebase.firebase-perf") version "1.4.2" apply false
}