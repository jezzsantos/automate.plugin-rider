import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.date
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {

    id("java")
    alias(libs.plugins.kotlin)
    id("org.jetbrains.intellij.platform")
    alias(libs.plugins.changelog)
    alias(libs.plugins.jvmwrapper)
}

group = providers.gradleProperty("thisPluginGroup").get()
version = providers.gradleProperty("thisPluginVersion").get()

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(17)
}

repositories {
    maven { setUrl("https://cache-redirector.jetbrains.com/maven-central") }

    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10")
    //noinspection GradlePackageUpdate
    implementation("com.microsoft.azure:applicationinsights-core:2.6.4") // HACK: we need to stay on this older version of AppInsights for as long as possible

    intellijPlatform {
        val platformVer: String = providers.gradleProperty("platformVersion").get()
        rider(platformVer)
        jetbrainsRuntime()
        pluginVerifier()
        zipSigner()
        instrumentationTools()

        testFramework(TestFrameworkType.Bundled)
    }
}

intellijPlatform {
    instrumentCode = true
    pluginConfiguration {
        val plugInVer: String = providers.gradleProperty("thisPluginVersion").get()
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
    val plugInVer: String = providers.gradleProperty("thisPluginVersion").get()
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
        val gradleVer: String = providers.gradleProperty("gradleVersion").get()
        gradleVersion = gradleVer
    }

    publishPlugin {
        dependsOn("patchChangelog")
        val plugInVer: String = providers.gradleProperty("thisPluginVersion").get()
        val items: String = if (plugInVer.split("-").size > 1)
            plugInVer.split("-")[1]
        else
            "default"
        token.set(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
        channels.set(listOf(items))
    }

    patchPluginXml {
        val plugInVer: String = providers.gradleProperty("thisPluginVersion").get()
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
            testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
            testImplementation("org.mockito:mockito-core:4.8.0")
            testImplementation("org.testng:testng:7.7.0")
            testRuntimeOnly("org.junit.support:testng-engine:1.0.4")
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

