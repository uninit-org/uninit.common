@Suppress("DSL_SCOPE_VIOLATION")
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

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
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
    var versionStr = project.version.toString()
    val ci = System.getenv("CI") != null && System.getenv("GITHUB_EVENT_NAME") != "release"
    var repo = "releases"
    if (ci) {
        val commitHash = System.getenv("GITHUB_SHA").slice(0..6)
        versionStr += "-#$commitHash"
        repo = "snapshots"
    }
    repositories {
         maven {
             name = "uninit"
             url = uri("https://repo.uninit.dev/$repo")
             credentials {
                 username = "admin"
                 password = System.getenv("REPOSILITE_PASSWORD")
             }
         }
        
    }
    publications {
        create<MavenPublication>("uninit.common") {
            groupId = "uninit"
            artifactId = "common"
            version = versionStr
        }
    }

}

true