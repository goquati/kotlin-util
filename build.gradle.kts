import org.jetbrains.dokka.DokkaConfiguration.Visibility
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
    id("com.vanniktech.maven.publish")
}

repositories {
    mavenCentral()
}

val githubUser = "goquati"
val githubProject = "kotlin-util"
val groupStr = "io.github.$githubUser"
val versionStr = System.getenv("GIT_TAG_VERSION") ?: "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
    apply(plugin = "com.vanniktech.maven.publish")

    repositories {
        mavenCentral()
    }

    group = groupStr
    version = versionStr

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

    mavenPublishing {
        pom {
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
                url = "https://github.com/${githubUser}/${githubProject}"
                connection = "scm:git:https://github.com/${githubUser}/${githubProject}.git"
                developerConnection = "scm:git:git@github.com:${githubUser}/${githubProject}.git"
            }
        }
        publishToMavenCentral(
            SonatypeHost.CENTRAL_PORTAL,
            automaticRelease = true,
        )
        signAllPublications()
    }
}

tasks.dokkaHtml {
    moduleName.set(groupStr)
    moduleVersion = versionStr

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


val publishInit: MavenPublishBaseExtension.(artifactId: String, descriptionStr: String) -> Unit =
    { artifactId: String, descriptionStr: String ->
        coordinates(
            groupId = groupStr,
            artifactId = artifactId,
            version = versionStr
        )
        pom {
            name = artifactId
            description = descriptionStr
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
                url = "https://github.com/${githubUser}/${githubProject}"
                connection = "scm:git:https://github.com/${githubUser}/${githubProject}.git"
                developerConnection = "scm:git:git@github.com:${githubUser}/${githubProject}.git"
            }
        }
        publishToMavenCentral(
            SonatypeHost.CENTRAL_PORTAL,
            automaticRelease = true,
        )
        signAllPublications()
    }
