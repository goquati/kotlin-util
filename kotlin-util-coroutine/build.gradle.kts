import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("multiplatform")
    `maven-publish`
    id("com.vanniktech.maven.publish")
}

val artifactId = "kotlin-coroutine-util"
val descriptionStr = "Enhanced Kotlin coroutine util functions for simpler code"

kotlin {
    jvm()
    js {
        browser()
        nodejs()
    }
    //macosX64()
    //macosArm64()
    //linuxX64()
    //linuxArm64()
    //mingwX64()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
        }
        val commonMain by getting {
            explicitApi()
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            compilerOptions {
                allWarningsAsErrors = true
                apiVersion.set(KotlinVersion.KOTLIN_2_0)
                languageVersion.set(KotlinVersion.KOTLIN_2_0)
            }
            dependencies {
                implementation(project(":kotlin-util"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("io.kotest:kotest-assertions-core:5.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0-RC")
            }
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = rootProject.group as String,
        artifactId = artifactId,
        version = rootProject.version as String
    )
    pom {
        name = artifactId
        description = descriptionStr
    }
}
