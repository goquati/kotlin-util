import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        val slf4jVersion = "2.0.13"
        val jvmMain by getting {
            explicitApi()
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            compilerOptions {
                allWarningsAsErrors = true
                apiVersion.set(KotlinVersion.KOTLIN_2_2)
                languageVersion.set(KotlinVersion.KOTLIN_2_2)
            }
            dependencies {
                implementation("org.slf4j:slf4j-api:$slf4jVersion")
                implementation("org.jetbrains.kotlin:kotlin-reflect")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.slf4j:slf4j-simple:$slf4jVersion")
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("io.kotest:kotest-assertions-core")
                implementation("io.mockk:mockk")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
            }
        }
    }
}

kover {
    reports {
        verify {
            CoverageUnit.values().forEach { covUnit ->
                rule("minimal ${covUnit.name.lowercase()} coverage rate") {
                    minBound(100, coverageUnits = covUnit)
                }
            }
        }
    }
}
