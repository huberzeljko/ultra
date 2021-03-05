@file:Suppress("PropertyName")

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

val google_auto_version: String by project
val kotlinpoet_version: String by project
val logback_version: String by project
val kotlintest_version: String by project

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    api(kotlin("reflect"))

    api(project(":meta"))

    kapt("com.google.auto.service:auto-service:$google_auto_version")

    kaptTest(project(":mutator"))

    testImplementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotlintest_version")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotlintest_version")
}

kotlin {
    sourceSets {
        test {
            kotlin.srcDir("src/examples")
        }
    }
}

kapt {
    useBuildCache = false
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }

    dependsOn("generateDocs")
}

tasks {

    create("generateDocs", JavaExec::class) {
        main = "de.peekandpoke.ultra.mutator.examples.GenerateDocsKt"
        classpath = sourceSets.getByName("test").runtimeClasspath
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

apply(from = "./../maven.publish.gradle.kts")
