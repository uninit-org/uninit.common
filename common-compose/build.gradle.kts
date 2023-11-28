plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    `maven-publish`
}

kotlin {

    androidTarget()
    jvm("desktop")
    iosArm64().binaries.framework {
        baseName = "uninitCommonCompose"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.serialization.json)
                implementation(libs.kotlinx.coroutines.core)

                compileOnly(compose.runtime)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                compileOnly(compose.components.resources)
                compileOnly(compose.foundation)

                compileOnly(libs.ktor.client.core)
                compileOnly(libs.ktor.client.negotiation)

                compileOnly(libs.koin.core)
                compileOnly(libs.koin.compose)

                implementation(project(":common"))
            }
            resources.srcDirs("resources")
        }

        val androidMain by getting {}
        val desktopMain by getting {
            dependencies {
                implementation(libs.jvm.gson)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "uninit.common.compose"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

publishing {
    var version = project.version.toString()
    val ci = System.getenv("CI") != null
    if (ci) {
        val commitHash = System.getenv("GITHUB_SHA").slice(0..6)
        version += "-#$commitHash"
    }
    repositories {
        if (ci) {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/uninit/uninit.common")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }

        // maven {
        //     name = "uninit Reposilite"
        //     url = uri("https://repo.uninit.dev/repository/maven-public/")
        //     credentials {
        //         username = "common"
        //         password = project.findProperty("reposilite.password") ?: System.getenv("REPOSILITE_PASSWORD")
        //     }
        // } 
        // TODO: Soon™
        
    }
    publications {
        
    }

}