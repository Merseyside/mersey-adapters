enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()

        google()
    }

    val group = "io.github.merseyside"
    val catalogVersions = "1.7.5"
    versionCatalogs {
        val androidLibs by creating {
            from("$group:catalog-version-android:$catalogVersions")
            version("dagger", "2.46.1")
            version("mersey-android", "2.0.7")
        }

        val common by creating {
            from("$group:catalog-version-common:$catalogVersions")
        }

        val catalogPlugins by creating {
            from("$group:catalog-version-plugins:$catalogVersions")
        }
    }
}

include(":app")

include(":adapters-core")
include(":adapters")
include(":adapters-delegates")
include(":adapters-compose")
include(":adapters-coroutine-ext")

rootProject.name = "mersey-adapters"
