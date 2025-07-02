plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.education"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.education"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // 添加 Dify API 配置
        buildConfigField("String", "DIFY_API_KEY", "\"app-zfuqOwt7yPevhnLoPx1yAtoQ\"")
        buildConfigField("String", "DIFY_BASE_URL", "\"https://api.dify.ai/v1/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.coroutines)
    
    // Hilt 依赖注入
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    kapt(libs.hilt.compiler)
    
    // Room 数据库
    implementation(libs.bundles.room)
    kapt(libs.room.compiler)
    
    // 网络请求
    implementation(libs.bundles.network)
    kapt(libs.moshi.codegen)
    
    // 导航
    implementation(libs.navigation.compose)
    
    // 数据存储
    implementation(libs.bundles.datastore)
    
    // WorkManager 后台任务
    implementation(libs.workmanager)
    
    // 分页
    implementation(libs.bundles.paging)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.realtime)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    
    // 图像加载
    implementation(libs.coil.compose)
    
    // Accompanist
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)
    
    // 序列化
    implementation(libs.kotlinx.serialization.json)

    // 测试依赖
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}