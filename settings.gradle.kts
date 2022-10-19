pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// buildSrc 不需要通过 include 导入
// Android 库只保留 build.gradle androidManifest.xml, src
// project(":bieming-core").projectDir = new File(rootDir,"libraries/core") 设置别名
// rootProject.name = "JianMoWeather"

include(":app")
include(":data-android")
include(":common-ui-compose")
include(":core-navigation")
include(":core-ui")
include(":ui-weather")
include(":ui-favorites")
include(":ui-more")
include(":ui-province")
include(":ui-city")
include(":ui-station")
include(":ui-district")
include(":ui-setting")
include(":ui-web")
include(":model")
include(":shared")
include(":data-datastore")