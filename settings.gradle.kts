pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
        id("org.jetbrains.dokka") version "1.9.20"
        id("org.jetbrains.kotlinx.kover") version "0.8.1"
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
        id("com.vanniktech.maven.publish") version "0.28.0"
    }
}

rootProject.name = "kotlin-util-base"

include(
    "kotlin-util",
    "kotlin-util-coroutine",
)
