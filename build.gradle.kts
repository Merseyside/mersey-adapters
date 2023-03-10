buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(common.plugins.versionChecker)
    `nexus-config`
}

allprojects {
    group = "io.github.merseyside"
    version = "1.0.0"
}

tasks.register("clean", Delete::class).configure {
    group = "build"
    delete(rootProject.buildDir)
}