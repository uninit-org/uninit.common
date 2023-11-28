@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    `maven-publish`
}

kotlin {

    androidTarget()
    jvm("desktop")
    iosArm64().binaries.framework {
        baseName = "uninitCommon"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
            }
            resources.srcDirs("resources")
        }

        val androidMain by getting {}
        val desktopMain by getting {
            dependencies {
                implementation(libs.jvm.gson)
            }
        }

        
        val iosArm64Main by getting {}
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "uninit.common"

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

true