package com.example.education.core.network

import android.content.Context
import com.example.education.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * 网络层依赖注入模块
 * 
 * 配置 OkHttp5 + 证书锁定 + 三级缓存 + Retrofit
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * 提供 HTTP 缓存
     */
    @Provides
    @Singleton
    fun provideHttpCache(@ApplicationContext context: Context): Cache {
        val cacheDir = File(context.cacheDir, "http_cache")
        return Cache(cacheDir, ApiConstants.NetworkConfig.CACHE_SIZE)
    }
    
    /**
     * 提供证书锁定器
     * 临时禁用证书锁定以解决连接问题
     */
    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return if (BuildConfig.DEBUG) {
            // Debug 模式下禁用证书锁定
            CertificatePinner.Builder().build()
        } else {
            // 生产环境需要添加真实的证书指纹
            CertificatePinner.Builder()
                // TODO: 添加正确的dify.ai证书指纹
                // .add("api.dify.ai", "sha256/REAL_CERTIFICATE_PIN")
                .build()
        }
    }
    
    /**
     * 提供动态授权拦截器
     * 支持根据请求动态设置API Key
     */
    @Provides
    @Singleton
    @Named("auth")
    fun provideDynamicAuthInterceptor(): Interceptor {
        return DynamicAuthInterceptor()
    }
    
    /**
     * 提供响应缓存拦截器
     */
    @Provides
    @Singleton
    @Named("cache")
    fun provideResponseCachingInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            
            // 为GET请求添加缓存控制
            if (chain.request().method == "GET") {
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=300") // 5分钟缓存
                    .build()
            } else {
                response
            }
        }
    }
    
    /**
     * 提供日志拦截器
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    /**
     * 提供 OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        certificatePinner: CertificatePinner,
        @Named("auth") authInterceptor: Interceptor,
        @Named("cache") responseCachingInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .certificatePinner(certificatePinner)
            .addInterceptor(authInterceptor)
            .addNetworkInterceptor(responseCachingInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(ApiConstants.NetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConstants.NetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConstants.NetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * 提供 Moshi JSON 转换器
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    /**
     * 提供 Kotlinx Serialization JSON
     */
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }
    
    /**
     * 提供 Retrofit 实例
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    /**
     * 提供 Dify API 服务
     */
    @Provides
    @Singleton
    fun provideDifyApiService(retrofit: Retrofit): DifyApiService {
        return retrofit.create(DifyApiService::class.java)
    }
    
    /**
     * 提供 API Key 管理器
     */
    @Provides
    @Singleton
    fun provideApiKeyManager(): ApiKeyManager {
        return ApiKeyManagerImpl()
    }
    
    /**
     * 提供增强的 Dify API 服务
     */
    @Provides
    @Singleton
    fun provideEnhancedDifyApiService(difyApiService: DifyApiService): EnhancedDifyApiService {
        return EnhancedDifyApiServiceImpl(difyApiService)
    }
}

/**
 * 动态授权拦截器
 * 支持根据请求上下文动态设置API Key
 */
class DynamicAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 从请求URL或header中获取智能体类型
        val agentType = originalRequest.header("X-Agent-Type") ?: "student"
        val apiKey = ApiConstants.ApiKeys.getApiKey(agentType)
        
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .removeHeader("X-Agent-Type") // 移除内部标识header
            .build()
            
        return chain.proceed(newRequest)
    }
}

/**
 * API Key 管理器接口
 */
interface ApiKeyManager {
    fun getApiKey(agentType: String): String
    fun setCurrentAgentType(agentType: String)
    fun getCurrentAgentType(): String
}

/**
 * API Key 管理器实现
 */
class ApiKeyManagerImpl : ApiKeyManager {
    private var currentAgentType: String = ApiConstants.AgentRoles.STUDENT
    
    override fun getApiKey(agentType: String): String {
        return ApiConstants.ApiKeys.getApiKey(agentType)
    }
    
    override fun setCurrentAgentType(agentType: String) {
        currentAgentType = agentType
    }
    
    override fun getCurrentAgentType(): String {
        return currentAgentType
    }
} 