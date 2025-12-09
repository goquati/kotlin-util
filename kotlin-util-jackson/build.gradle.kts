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
                apiVersion.set(KotlinVersion.KOTLIN_2_2)
                languageVersion.set(KotlinVersion.KOTLIN_2_2)
            }
            dependencies {
                api(project(":kotlin-util"))
                val jacksonVersion: String by rootProject.extra
                implementation("tools.jackson.core:jackson-databind:$jacksonVersion")
            }
        }
        val commonTest by getting {
            compilerOptions {
                freeCompilerArgs.add("-Xannotation-default-target=param-property")
            }
            dependencies {
                implementation("tools.jackson.module:jackson-module-kotlin")
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("io.kotest:kotest-assertions-core")
            }
        }
    }
}

val CoverageUnit.bound
    get() = when (this) {
        CoverageUnit.LINE -> 76
        CoverageUnit.INSTRUCTION -> 73
        CoverageUnit.BRANCH -> 47
    }
kover {
    reports {
        verify {
            CoverageUnit.values().forEach { covUnit ->
                rule("minimal ${covUnit.name.lowercase()} coverage rate") {
                    minBound(covUnit.bound, coverageUnits = covUnit)
                }
            }
        }
    }
}