import org.jetbrains.dokka.DokkaConfiguration.Visibility
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")
}

repositories {
    mavenCentral()
}
subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")

    repositories {
        mavenCentral()
    }

    val githubUser = "goquati"
    val githubProject = "kotlin-util"
    group = "io.github.$githubUser"
    version = System.getenv("GIT_TAG_VERSION") ?: "1.0-SNAPSHOT"
    rootProject.ext {
        set("url", "https://github.com/$githubUser/$githubProject")

        set("licenseName", "MIT License")
        set("licenseUrl", "https://github.com/$githubUser/$githubProject/blob/main/LICENSE")

        set("developerId", githubUser)
        set("developerName", githubUser)
        set("developerUrl", "https://github.com/$githubUser")

        set("scmUrl", "https://github.com/${githubUser}/${githubProject}")
        set("scmConnection", "scm:git:https://github.com/${githubUser}/${githubProject}.git")
        set("scmDeveloperConnection", "scm:git:git@github.com:${githubUser}/${githubProject}.git")
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
}

tasks.dokkaHtml {
    moduleName.set("io.github.goquati")
    moduleVersion = version as String

    dokkaSourceSets {
        register("commonMainSet") {
            displayName.set("common")
            sourceRoots.from(
                rootDir.resolve("kotlin-util/src/commonMain/kotlin"),
                rootDir.resolve("kotlin-util-coroutine/src/commonMain/kotlin"),
            )
        }
        register("jvmMainSet") {
            displayName.set("jvm")
            sourceRoots.from(
                rootDir.resolve("kotlin-util/src/jvmMain/kotlin"),
                rootDir.resolve("kotlin-utill-coroutine/src/jvmMain/kotlin"),
            )
        }
        configureEach {
            documentedVisibilities.set(setOf(Visibility.PUBLIC, Visibility.PROTECTED))
            includeNonPublic = false
            skipEmptyPackages = true
            suppressGeneratedFiles = false
        }
    }
}