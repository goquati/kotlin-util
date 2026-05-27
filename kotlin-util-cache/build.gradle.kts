import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
        }
        val commonMain by getting {
            explicitApi()
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            compilerOptions {
                allWarningsAsErrors = true
                apiVersion.set(KotlinVersion.KOTLIN_2_3)
                languageVersion.set(KotlinVersion.KOTLIN_2_3)
                freeCompilerArgs.add("-Xreturn-value-checker=full")
            }
            dependencies {
                api(project(":kotlin-util"))
                val kotlinxCoroutineVersion: String by rootProject.extra
                implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("io.kotest:kotest-assertions-core")
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
