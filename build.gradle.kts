import com.vanniktech.maven.publish.SonatypeHost
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.28.0"
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.kotlinx.kover") version "0.8.1"
}

group = "io.github.klahap.kotlin.util"
version = System.getenv("KOTLIN_COLLECTION_UTIL_VERSION") ?: "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-assertions-core:5.9.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.9"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    languageVersion = "1.9"
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
        url = "https://github.com/klahap/kotlin-util"
        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/klahap/kotlin-util/blob/main/LICENSE"
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
            url = "https://github.com/klahap/kotlin-util"
            connection = "scm:git:https://github.com/klahap/kotlin-util.git"
            developerConnection = "scm:git:git@github.com:klahap/kotlin-util.git"
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
