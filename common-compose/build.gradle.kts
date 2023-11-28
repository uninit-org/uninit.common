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
    var versionStr = project.version.toString()
    val ci = System.getenv("CI") != null && System.getenv("GITHUB_EVENT_NAME") != "released"
    var repo = "releases"
    if (ci) {
        val commitHash = System.getenv("GITHUB_SHA").slice(0..6)
        versionStr += "-#$commitHash"
        repo = "snapshots"
    }
    val a = System.getenv("GITHUB_EVENT_NAME")
    repositories {
        maven {
            name = "uninit"
            url = uri("https://repo.uninit.dev/$repo$a")
            credentials {
                username = "admin"
                password = System.getenv("REPOSILITE_PASSWORD")
            }
        }

    }
    publications {
        create<MavenPublication>("uninit.common.compose") {
            groupId = "uninit"
            artifactId = "common.compose"
            version = versionStr
        }
    }

}