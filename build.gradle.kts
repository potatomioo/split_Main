plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
//    alias(libs.plugins.google.playServices) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.kotlinCocoapods) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

apply<MyPlugin>()

class MyPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        println("Avishisht's Plugin Applied")
    }
}