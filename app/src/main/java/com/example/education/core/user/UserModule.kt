package com.example.education.core.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 用户模块的依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "education_user_prefs")
    
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
} 