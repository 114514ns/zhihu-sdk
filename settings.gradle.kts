pluginManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

plugins {
    //id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "zhihu-sdk"