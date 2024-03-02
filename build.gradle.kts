// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val agp_version by extra("8.2.2")
    dependencies {
        classpath ("com.google.gms:google-services:4.4.1")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}
plugins {
    id ("com.android.application") version "8.2.2" apply false
    id ("com.android.library") version "8.0.2" apply false
    id ("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id ("com.google.firebase.firebase-perf") version "1.4.2" apply false

    id ("com.google.dagger.hilt.android") version "2.49" apply false
    id ("com.google.gms.google-services") version "4.3.15" apply false
    id ("com.google.firebase.crashlytics") version "2.9.9" apply false
//    id 'com.gladed.androidgitversion' version '0.4.14' apply false
}