plugins {
    with(catalogPlugins.plugins) {
        plugin(android.library)
        plugin(kotlin.android)
        id(mersey.android.extension.id())
        id(mersey.kotlin.extension.id())
        id("org.jetbrains.dokka")
    }
    `maven-publish-plugin`
}

android {
    namespace = "com.merseyside.adapters.coroutines"
    compileSdk = Application.compileSdk

    defaultConfig {
        minSdk = Application.minSdk
    }

    lint {
        lintConfig = rootProject.file(".lint/config.xml")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("proguard-rules.pro")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

kotlinExtension {
    setCompilerArgs(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xcontext-receivers"
    )
}

dependencies {
    api(projects.adaptersCore)
}

afterEvaluate {
    tasks.named("javaDocReleaseGeneration")
        .configure { // See: https://youtrack.jetbrains.com/issue/KTIJ-19005/JDK-17-PermittedSubclasses-requires-ASM9-exception-multiple-times-per-second-during-analysis
            enabled = false
        }
}
