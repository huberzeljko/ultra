import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.TaskContainerScope
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

@Suppress("MemberVisibilityCanBePrivate")
object Deps {
    operator fun invoke(block: Deps.() -> Unit) {
        this.block()
    }

    // //////////////////////////////////////////////////////////////////
    const val kotlinVersion = "1.6.21"
    // //////////////////////////////////////////////////////////////////

    // Publishing //////////////////////////////////////////////////////
    // TODO: Upgrade to 0.15.x -> Beware: configuration changes are necessary
    const val mavenPublishVersion = "0.14.2"
    // //////////////////////////////////////////////////////////////////

    // https://mvnrepository.com/artifact/com.auth0/java-jwt
    private const val auth0_java_jwt_version = "3.19.1"
    const val auth0_java_jwt = "com.auth0:java-jwt:$auth0_java_jwt_version"

    // TODO: check if we can update to 3.x
    private const val diffutils_version = "2.2"
    const val diffutils = "com.github.wumpz:diffutils:$diffutils_version"

    // https://mvnrepository.com/artifact/org.jetbrains.dokka/dokka-gradle-plugin
    // Dokka gradle plugin org.jetbrains.dokka
    const val dokkaVersion = "1.6.21"

    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    private const val google_auto_service_version = "1.0.1"
    const val google_auto_service = "com.google.auto.service:auto-service:$google_auto_service_version"

    // https://jitpack.io/#matfax/klassindex
    // TODO: remove this library and build out own!
    private const val klassIndexVersion = "4.1.0-rc.1"
    const val klassIndexLib = "com.github.matfax.klassindex:library:$klassIndexVersion"
    const val klassIndexProcessor = "com.github.matfax.klassindex:processor:$klassIndexVersion"

    // https://mvnrepository.com/artifact/com.github.tschuchortdev/kotlin-compile-testing
    private const val kotlin_compiletesting_version = "1.4.8"
    const val kotlin_compiletesting = "com.github.tschuchortdev:kotlin-compile-testing:$kotlin_compiletesting_version"

    // https://mvnrepository.com/artifact/com.squareup/kotlinpoet
    private const val kotlinpoet_version = "1.11.0"
    const val kotlinpoet = "com.squareup:kotlinpoet:$kotlinpoet_version"

    // https://kotlinlang.org/docs/releases.html#release-details
    private const val kotlinx_coroutines_version = "1.6.0"
    const val kotlinx_coroutines_core =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines_version"

    // https://kotlinlang.org/docs/releases.html#release-details
    private const val kotlinx_serialization_version = "1.3.2"
    const val kotlinx_serialization_core =
        "org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinx_serialization_version"
    const val kotlinx_serialization_json =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version"

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-datetime
    private const val kotlinx_datetime_version = "0.3.2"
    const val kotlinx_datetime_common = "org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime_version"

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-css
    private const val kotlinx_wrappers_css_version = "1.0.0-pre.332-kotlin-1.6.21"
    const val kotlinx_wrappers_css =
        "org.jetbrains.kotlin-wrappers:kotlin-css:$kotlinx_wrappers_css_version"

    // https://mvnrepository.com/artifact/com.soywiz.korlibs.klock/klock
    private const val korlibs_klock_version = "2.7.0"
    const val korlibs_klock_common = "com.soywiz.korlibs.klock:klock:$korlibs_klock_version"
    const val korlibs_klock_js = "com.soywiz.korlibs.klock:klock-js:$korlibs_klock_version"
    const val korlibs_klock_jvm = "com.soywiz.korlibs.klock:klock-jvm:$korlibs_klock_version"

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    private const val slf4j_version = "1.7.36"
    const val slf4j_api = "org.slf4j:slf4j-api:$slf4j_version"

    // // NPM dependencies /////////////////////////////////////////////////////////////////////////

    // // Test dependencies ////////////////////////////////////////////////////////////////////////

    object Test {

        operator fun invoke(block: Test.() -> Unit) {
            this.block()
        }

        // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
        const val logback_version = "1.2.11"
        const val logback_classic = "ch.qos.logback:logback-classic:$logback_version"

        // https://mvnrepository.com/artifact/io.kotest/kotest-common
        const val kotest_version = "5.3.0"
        const val kotest_assertions_core = "io.kotest:kotest-assertions-core:$kotest_version"
        const val kotest_framework_api = "io.kotest:kotest-framework-api:$kotest_version"

        const val kotest_assertions_core_jvm = "io.kotest:kotest-assertions-core-jvm:$kotest_version"
        const val kotest_runner_junit_jvm = "io.kotest:kotest-runner-junit5-jvm:$kotest_version"

        const val kotest_assertions_core_js = "io.kotest:kotest-assertions-core-js:$kotest_version"
        const val kotest_framework_api_js = "io.kotest:kotest-framework-api-js:$kotest_version"
        const val kotest_framework_engine_js = "io.kotest:kotest-framework-engine-js:$kotest_version"

        fun KotlinDependencyHandler.commonTestDeps() {
            implementation(kotest_assertions_core)
            implementation(kotest_framework_api)
        }

        fun KotlinDependencyHandler.jsTestDeps() {
            implementation(kotest_assertions_core_js)
            implementation(kotest_framework_api_js)
            implementation(kotest_framework_engine_js)
        }

        fun KotlinDependencyHandler.jvmTestDeps() {
            implementation(logback_classic)
            implementation(kotest_assertions_core_jvm)
            implementation(kotest_runner_junit_jvm)
        }

        fun DependencyHandlerScope.jvmTestDeps() {
            testImplementation(logback_classic)
            testImplementation(kotest_assertions_core_jvm)
            testImplementation(kotest_runner_junit_jvm)
        }

        fun TaskContainerScope.configureJvmTests(
            configure: org.gradle.api.tasks.testing.Test.() -> Unit = {}
        ) {
            listOfNotNull(
                findByName("test") as? org.gradle.api.tasks.testing.Test,
                findByName("jvmTest") as? org.gradle.api.tasks.testing.Test,
            ).firstOrNull()?.apply {
                useJUnitPlatform { }

                filter {
                    isFailOnNoMatchingTests = false
                }

//                testLogging {
//                    showExceptions = true
//                    showStandardStreams = true
//                    events = setOf(
//                        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
//                        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
//                    )
//                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//                }

                configure()
            }
        }
    }
}

private fun DependencyHandlerScope.testImplementation(dep: String) =
    add("testImplementation", dep)
