package com.example.education.core.network

import com.example.education.BuildConfig
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 网络模块 - 提供Retrofit和OkHttp配置
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 提供JSON序列化器
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    /**
     * 提供HTTP日志拦截器
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * 提供Flipper网络插件（仅Debug构建）
     */
    @Provides
    @Singleton
    fun provideNetworkFlipperPlugin(): NetworkFlipperPlugin? {
        return if (BuildConfig.DEBUG && BuildConfig.ENABLE_FLIPPER) {
            NetworkFlipperPlugin()
        } else {
            null
        }
    }

    /**
     * 提供OkHttp客户端
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkFlipperPlugin: NetworkFlipperPlugin?
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                // 添加API Key到请求头
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.DIFY_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .apply {
                // 添加Flipper网络拦截器（仅Debug构建）
                networkFlipperPlugin?.let { plugin ->
                    addNetworkInterceptor(FlipperOkhttpInterceptor(plugin))
                }
            }
            .build()
    }

    /**
     * 提供Retrofit实例
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.DIFY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
}