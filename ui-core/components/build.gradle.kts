plugins {
    alias(libs.plugins.jianmoweather.compose.android.library)
}

android {
    namespace = "dev.shuanghua.ui.core.components"
}

dependencies {
    implementation(project(":ui-core:compose"))
    implementation(project(":shared"))
    implementation(project(":data-android:model"))
}