pluginManagement {
    val intellijPluginVersion: String by settings

    plugins {
        id("org.jetbrains.intellij.platform").version(intellijPluginVersion) // so that we can read the version from gradle.properties
    }

    repositories {
        maven { setUrl("https://cache-redirector.jetbrains.com/plugins.gradle.org") }

    }

    resolutionStrategy {
        eachPlugin {
            // Gradle has to map a plugin dependency to Maven coordinates - '{groupId}:{artifactId}:{version}'. It tries
            // to do use '{plugin.id}:{plugin.id}.gradle.plugin:version'.
            // This doesn't work for rdgen, so we provide some help
            if (requested.id.id == "com.jetbrains.rdgen") {
                useModule("com.jetbrains.rd:rd-gen:${requested.version}")
            }
        }
    }
}

rootProject.name = "automate"