import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js {
        browser()
        nodejs()
    }
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
        }
        val commonMain by getting {
            explicitApi()
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            compilerOptions {
                allWarningsAsErrors = true
                apiVersion.set(KotlinVersion.KOTLIN_2_2)
                languageVersion.set(KotlinVersion.KOTLIN_2_2)
            }
            dependencies {}
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
        filters {
            excludes {
                classes(
                    "de.quati.kotlin.util.Success",
                    "de.quati.kotlin.util.Failure",
                )
            }
        }
        verify {
            CoverageUnit.values().forEach { covUnit ->
                rule("minimal ${covUnit.name.lowercase()} coverage rate") {
                    minBound(100, coverageUnits = covUnit)
                }
            }
        }
    }
}
