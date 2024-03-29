plugins {
    with(catalogPlugins.plugins) {
        plugin(android.library)
        plugin(kotlin.android)
        plugin(kotlin.serialization)
        id(mersey.android.extension.id())
        id(mersey.kotlin.extension.id())
        plugin(kotlin.kapt)
    }
    `maven-publish-plugin`
}

android {
    namespace = "com.merseyside.adapters.core"
    compileSdk = Application.compileSdk

    defaultConfig {
        minSdk = Application.minSdk
    }
    
    buildFeatures.dataBinding = true

    lint {
        lintConfig = rootProject.file(".lint/config.xml")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    sourceSets.getByName("main") {
        res.srcDirs(
            "src/main/res/",
            "src/main/res/layouts/view",
            "src/main/res/layouts/list",
            "src/main/res/value/values-light",
            "src/main/res/value/values-night"
        )
    }
}

kotlinExtension {
    setCompilerArgs(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xcontext-receivers"
    )
}

val androidLibz = listOf(
    androidLibs.appCompat,
    androidLibs.material,
    androidLibs.recyclerView,
    androidLibs.coroutines,
    androidLibs.lifecycleLiveDataKtx
)

val merseyLibs = listOf(
    common.mersey.kotlin.ext,
    androidLibs.mersey.utils
)

dependencies {
    androidLibz.forEach { lib -> api(lib) }
    merseyLibs.forEach { lib -> api(lib) }
}