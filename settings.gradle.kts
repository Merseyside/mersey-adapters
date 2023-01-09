enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()

        google()
    }

    val group = "io.github.merseyside"
    val catalogVersions = "1.6.5"
    versionCatalogs {
        val androidLibs by creating {
            from("$group:catalog-version-android:$catalogVersions")
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