import com.vanniktech.maven.publish.SonatypeHost
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "1.9.25"
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.28.0"
    id("org.jetbrains.kotlinx.kover") version "0.8.1"
}

group = "io.github.klahap.kotlin.util"
version = System.getenv("KOTLIN_COLLECTION_UTIL_VERSION") ?: "1.0-SNAPSHOT"
val githubId = "klahap/kotlin-util"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-assertions-core:5.9.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

kotlin {
    explicitApi()
    compilerOptions {
        allWarningsAsErrors = true
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

mavenPublishing {
    coordinates(
        groupId = group as String,
        artifactId = "kotlin-util",
        version = version as String
    )
    pom {
        name = "kotlin-util"
        description = "Enhanced Kotlin collection functions for simpler code"
        url = "https://github.com/$githubId"
        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/$githubId/blob/main/LICENSE"
            }
        }
        developers {
            developer {
                id = "klahap"
                name = "Klaus Happacher"
                email = "k.happacher@gmail.com"
                url = "https://github.com/klahap"
            }
        }
        scm {
            url = "https://github.com/$githubId"
            connection = "scm:git:https://github.com/$githubId.git"
            developerConnection = "scm:git:git@github.com:$githubId.git"
        }
    }
    publishToMavenCentral(
        SonatypeHost.CENTRAL_PORTAL,
        automaticRelease = true,
    )
    signAllPublications()
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
