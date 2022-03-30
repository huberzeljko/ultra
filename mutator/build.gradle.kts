@file:Suppress("PropertyName")

import Deps.Test.configureJvmTests
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka")
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    api(project(":meta"))

    kapt(Deps.google_auto_service)

    // Test /////////////////////////////////////////////////////////////////////////

    kaptTest(project(":mutator"))

    Deps.Test {
        jvmTestDeps()
    }
}

kotlin {
    sourceSets {
        test {
            kotlin.srcDir("src/examples")
        }
    }
}

kapt {
    useBuildCache = true
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    configureJvmTests {
        dependsOn("generateDocs")
    }

    create("generateDocs", JavaExec::class) {
        main = "de.peekandpoke.ultra.mutator.examples.GenerateDocsKt"
        classpath = sourceSets.getByName("test").runtimeClasspath
    }
}

apply(from = "./../maven.publish.gradle.kts")
