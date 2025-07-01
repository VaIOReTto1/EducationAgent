// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

// 全局配置已在settings.gradle.kts中统一管理

// 清理任务
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// 版本管理
extensions.configure<org.gradle.api.plugins.ExtraPropertiesExtension> {
    set("compileSdk", 35)
    set("targetSdk", 35)
    set("minSdk", 24)
    set("versionCode", 1)
    set("versionName", "1.0.0")
    set("jvmTarget", "17")
}