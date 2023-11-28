@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
}


buildscript {
    // extra[""]
    repositories {
        gradlePluginPortal()
    }

    dependencies {
    }
}

allprojects {
    group = "uninit.common"
    version = "0.1.0"
}

subprojects {
    buildDir = file(rootProject.buildDir.absolutePath + "/" + project.name)
}

true // Needed to make the Suppress annotation work for the plugins block