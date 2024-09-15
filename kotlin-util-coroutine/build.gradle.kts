import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.kotlinx.kover") version "0.8.1"
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.28.0"
    id("org.jetbrains.dokka") version "1.9.20"
}

val githubUser = "goquati"
val githubProject = "kotlin-util"
val artifactId = "kotlin-coroutine-util"
group = "io.github.$githubUser"
version = System.getenv("GIT_TAG_VERSION") ?: "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

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

kover {
    reports {
        verify {
            CoverageUnit.values().forEach { covUnit ->
                rule("minimal ${covUnit.name.lowercase()} coverage rate") {
                    minBound(100, coverageUnits = covUnit)
                }
            }
        }
        filters {
            excludes {
                annotatedBy(
                    "io.github.goquati.kotlin.coroutine.util.KoverIgnore"
                )
            }
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = group as String,
        artifactId = artifactId,
        version = version as String
    )
    pom {
        name = artifactId
        description = "Enhanced Kotlin coroutine util functions for simpler code"
        url = "https://github.com/$githubUser/$githubProject"
        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/$githubUser/$githubProject/blob/main/LICENSE"
            }
        }
        developers {
            developer {
                id = githubUser
                name = githubUser
                url = "https://github.com/$githubUser"
            }
        }
        scm {
            url = "https://github.com/$githubUser/$githubProject"
            connection = "scm:git:https://github.com/$githubUser/$githubProject.git"
            developerConnection = "scm:git:git@github.com:$githubUser/$githubProject.git"
        }
    }
    publishToMavenCentral(
        SonatypeHost.CENTRAL_PORTAL,
        automaticRelease = true,
    )
    signAllPublications()
}

tasks.dokkaHtml.configure {
    moduleVersion = version as String
}
