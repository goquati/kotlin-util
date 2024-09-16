import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("multiplatform")
    `maven-publish`
    id("com.vanniktech.maven.publish")
}

val artifactId = "kotlin-util"
val descriptionStr = "Enhanced Kotlin util functions for simpler code"

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
                apiVersion.set(KotlinVersion.KOTLIN_2_0)
                languageVersion.set(KotlinVersion.KOTLIN_2_0)
            }
            dependencies {}
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("io.kotest:kotest-assertions-core:5.9.0")
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
        description = descriptionStr
        url = rootProject.extra["url"] as String
        licenses {
            license {
                name = rootProject.extra["licenseName"] as String
                url = rootProject.extra["licenseUrl"] as String
            }
        }
        developers {
            developer {
                id = rootProject.extra["developerId"] as String
                name = rootProject.extra["developerName"] as String
                url = rootProject.extra["developerUrl"] as String
            }
        }
        scm {
            url = rootProject.extra["scmUrl"] as String
            connection = rootProject.extra["scmConnection"] as String
            developerConnection = rootProject.extra["scmDeveloperConnection"] as String
        }
    }
    publishToMavenCentral(
        SonatypeHost.CENTRAL_PORTAL,
        automaticRelease = true,
    )
    signAllPublications()
}
