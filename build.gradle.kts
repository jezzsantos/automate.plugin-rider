import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.date
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {

    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellijPlatform)
    alias(libs.plugins.changelog)
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// Set the JVM language level used to build the project.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    maven { setUrl("https://cache-redirector.jetbrains.com/maven-central") }

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(libs.googleGson)
    implementation(libs.microsoftApplicationInsights)

    intellijPlatform {
        val platformVer: String = providers.gradleProperty("platformVersion").get()
        // HACK: Do not set useInstaller = true because installer version for some reason do not contain libs/testFramework.jar
        rider(platformVer, useInstaller = false)
        pluginVerifier()
        zipSigner()
        instrumentationTools()

        // HACK: TestFrameworkType.Platform as of this moment does not work for rider and the warning on this page is wrong.
        // https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html#testing
        testFramework(TestFrameworkType.Bundled)
    }
}

intellijPlatform {
    instrumentCode = true
    pluginConfiguration {
        val plugInVer: String = providers.gradleProperty("pluginVersion").get()
        version = plugInVer
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }
    pluginVerification {
        ides {
            recommended()
        }
    }
}

changelog {
    val plugInVer: String = providers.gradleProperty("pluginVersion").get()
    version.set(plugInVer)
    path.set(file("CHANGELOG.md").canonicalPath)
    header.set(provider { "[${version.get()}] - ${date("yyyy-MM-dd")}" })
    headerParserRegex.set("""(\d+\.\d+\.\d+[\-\w]*)""".toRegex())
    introduction.set(
        """
        All notable changes to this project are documented in this file.

> The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
    """.trimIndent()
    )
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Notes", "Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
    lineSeparator.set("\n")
    combinePreReleases.set(true)
}

tasks {

    wrapper {
        gradleVersion = libs.versions.gradle.get()
    }

    publishPlugin {
        dependsOn("patchChangelog")
        val plugInVer: String = providers.gradleProperty("pluginVersion").get()
        val items: String = if (plugInVer.split("-").size > 1)
            plugInVer.split("-")[1]
        else
            "default"
        token.set(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
        channels.set(listOf(items))
    }

    patchPluginXml {
        val plugInVer: String = providers.gradleProperty("pluginVersion").get()
        val versionExists = changelog.has(plugInVer)
        if (versionExists) {
            changeNotes.set(provider {
                changelog.renderItem(
                    changelog
                        .get(plugInVer)
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML
                )
            }
            )
        } else {
            changeNotes.set(provider {
                changelog.renderItem(
                    changelog
                        .getUnreleased()
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML
                )
            }
            )
        }
    }

    test {
        dependencies {
            testImplementation(libs.junitJupiter)
            testRuntimeOnly(libs.junitJupiterEngine)
            testImplementation(libs.mockito)
            testImplementation(libs.testNg)
            testRuntimeOnly(libs.testNgEngine)
        }

        systemProperty("LOCAL_ENV_RUN", "true") //For use with 'BaseTestWithSolution' and TestNG
        useJUnitPlatform()
        minHeapSize = "512m"
        maxHeapSize = "1024m"
        testLogging {
            showStandardStreams = true
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    runIde {
        autoReload = false
        maxHeapSize = "2G"
    }
}

