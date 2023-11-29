import java.text.SimpleDateFormat
import java.util.*

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
    group = "uninit"
    extra["root-maven-url"] = "https://repo.uninit.dev/"
    val ci = System.getenv("CI") != null
    val isRelease = System.getenv("GITHUB_EVENT_NAME") == "release"

    version = "0.0.1"
    if (ci && !isRelease) {
        version = "${version.toString()}-${SimpleDateFormat("YYYYMMdd").format(Date(System.currentTimeMillis()))}-${System.getenv("GITHUB_SHA").slice(0..6)}" // todo: add date
    }
    if (!ci) {
        version = "${version.toString()}-LOCAL#${System.currentTimeMillis()}"
    }

    /*
     * If SNAPSHOT:
     * version = "VERSION-YYYYMMdd-HASH"
     * If not CI:
     * version = "VERSION-LOCAL#TIMESTAMP"
     * If RELEASE:
     * version = "VERSION"
     */

    val mavenRepo: PublishingExtension.() -> Unit = {
        repositories {
            maven {
                name = "uninit"
                url = uri("${extra["root-maven-url"]}${if (ci) "snapshots" else "releases"}")
                credentials {
                    username = "admin"
                    password = System.getenv("REPOSILITE_PASSWORD")
                }
            }
        }
    }

    extra["maven-repository"] = mavenRepo
}

true // Needed to make the Suppress annotation work for the plugins block