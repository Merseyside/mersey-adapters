import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName

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

subprojects {
    gradle.taskGraph.whenReady {
        if (getBaseName().get() == "adapters-coroutine-ext") {
            tasks.matching { it.name == "javaDocReleaseGeneration" }.configureEach {
                // See: https://youtrack.jetbrains.com/issue/KTIJ-19005/JDK-17-PermittedSubclasses-requires-ASM9-exception-multiple-times-per-second-during-analysis
                enabled = false
            }
        }
    }
}