@file:Suppress("PropertyName")

import Deps.Test.commonTestDeps
import Deps.Test.configureJvmTests
import Deps.Test.jsTestDeps
import Deps.Test.jvmTestDeps

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.dokka")
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

repositories {
    mavenCentral()
}

kotlin {
    targets {

        js(IR) {
            browser()
        }

        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }
        }
    }

    // Note that the Kotlin metadata is here, too.

    configure(listOf(targets["metadata"], jvm(), js())) {
        mavenPublication {
            val targetPublication = this@mavenPublication
            tasks.withType<AbstractPublishToMaven>()
                .matching { it.publication == targetPublication }
                .all { onlyIf { findProperty("isMainHost") == "true" } }
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {

        val commonMain by getting {
            dependencies {
                api(Deps.kotlinx_html)
                api(Deps.kotlinx_wrappers_css)
            }
        }

        val commonTest by getting {
            dependencies {
                commonTestDeps()
            }
        }

        js().compilations["main"].defaultSourceSet {
            dependencies {
            }
        }

        js().compilations["test"].defaultSourceSet {
            dependencies {
                jsTestDeps()
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                jvmTestDeps()
            }
        }
    }
}

tasks {
    configureJvmTests()
}

apply(from = "./../maven.publish.gradle.kts")
