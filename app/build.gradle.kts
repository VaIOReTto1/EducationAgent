plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "com.example.education"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.education"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "com.example.education.HiltTestRunner"
        
        // Room数据库配置
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
        
        // API Key配置 - 在local.properties中设置DIFY_API_KEY
        val apiKey = project.findProperty("DIFY_API_KEY") as String? ?: "app-zfuqOwt7yPevhnLoPx1yAtoQ"
        buildConfigField("String", "DIFY_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "DIFY_BASE_URL", "\"https://api.dify.ai/v1\"")
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            
            // 启用Flipper和LeakCanary
            buildConfigField("boolean", "ENABLE_FLIPPER", "true")
            buildConfigField("boolean", "ENABLE_LEAKCANARY", "true")
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // 启用R8优化
            buildConfigField("boolean", "ENABLE_FLIPPER", "false")
            buildConfigField("boolean", "ENABLE_LEAKCANARY", "false")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    // 测试配置
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // 核心Android库
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose BOM和UI组件
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    
    // Hilt依赖注入
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Room数据库
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)
    
    // 网络请求
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    
    // 分页
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    
    // WorkManager
    implementation(libs.workmanager)
    implementation(libs.workmanager.hilt)
    
    // DataStore
    implementation(libs.datastore.preferences)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    
    // 序列化
    implementation(libs.kotlinx.serialization.json)
    
    // 调试工具 (仅Debug构建)
    debugImplementation(libs.leakcanary)
    debugImplementation(libs.flipper)
    debugImplementation(libs.flipper.network)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    // 测试库
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

// Room数据库配置
room {
    schemaDirectory("$projectDir/schemas")
}