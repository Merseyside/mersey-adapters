allprojects {
    plugins.withId("org.gradle.maven-publish") {
        group = "io.github.merseyside"
        version = androidLibs.versions.mersey.adapters.get()
    }
}

tasks.register("clean", Delete::class).configure {
    group = "build"
    delete(rootProject.buildDir)
}