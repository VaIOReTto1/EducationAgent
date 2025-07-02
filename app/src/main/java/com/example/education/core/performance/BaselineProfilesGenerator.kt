package com.example.education.core.performance

import android.util.Log

/**
 * Baseline Profiles 生成器
 * 
 * 为关键用户旅程生成性能优化配置
 */
object BaselineProfilesGenerator {
    
    private const val TAG = "BaselineProfilesGenerator"
    
    /**
     * 生成主要用户流程的 Baseline Profiles
     */
    fun generateProfiles(): List<String> {
        Log.d(TAG, "生成Baseline Profiles配置")
        
        return listOf(
            // 应用启动流程
            "Lcom/example/education/EducationApplication;",
            "Lcom/example/education/MainActivity;",
            "Lcom/example/education/navigation/EducationNavigation;",
            
            // 用户认证和角色管理
            "Lcom/example/education/core/user/RoleManager;",
            "Lcom/example/education/core/user/UserModule;",
            
            // 核心数据库操作
            "Lcom/example/education/core/database/EducationDatabase;",
            "Lcom/example/education/core/database/dao/CourseDao;",
            "Lcom/example/education/core/database/dao/UserDao;",
            "Lcom/example/education/core/database/dao/LearningProgressDao;",
            
            // 网络和智能体服务
            "Lcom/example/education/core/network/DifyApiService;",
            "Lcom/example/education/agent/AgentRepositoryImpl;",
            "Lcom/example/education/core/service/AgentServiceImpl;",
            
            // 教师端关键流程
            "Lcom/example/education/feature_teacher/dashboard/TeacherDashboardScreen;",
            "Lcom/example/education/feature_teacher/dashboard/TeacherDashboardViewModel;",
            "Lcom/example/education/feature_teacher/dashboard/usecase/GetTeachingStatsUseCase;",
            
            // 学生端关键流程
            "Lcom/example/education/feature_student/reader/StudentReaderScreen;",
            "Lcom/example/education/feature_student/reader/StudentReaderViewModel;",
            "Lcom/example/education/feature_student/reader/usecase/UpdateLearningProgressUseCase;",
            
            // 聊天功能（高频使用）
            "Lcom/example/education/ui/chat/ChatScreen;",
            "Lcom/example/education/ui/chat/ChatViewModel;",
            
            // Compose UI 组件
            "Lcom/example/education/core/common_ui/theme/Theme;",
            "Lcom/example/education/core/common_ui/theme/Type;",
            
            // 数据同步（后台关键任务）
            "Lcom/example/education/core/sync/FirebaseSyncRepository;",
            "Lcom/example/education/core/sync/SyncWorker;",
            
            // Repository层（数据访问热点）
            "Lcom/example/education/core/repository/CourseRepositoryImpl;",
            
            // 工具类（频繁调用）
            "Lcom/example/education/core/utils/DateTimeUtils;",
            "Lcom/example/education/core/utils/FileUtils;"
        )
    }
    
    /**
     * 获取关键启动路径的方法签名
     */
    fun getStartupMethods(): List<String> {
        return listOf(
            // Application 启动
            "Lcom/example/education/EducationApplication;->onCreate()V",
            
            // MainActivity 启动
            "Lcom/example/education/MainActivity;->onCreate(Landroid/os/Bundle;)V",
            "Lcom/example/education/MainActivity;->setContent(Lkotlin/jvm/functions/Function2;)V",
            
            // 数据库初始化
            "Lcom/example/education/core/database/EducationDatabase;->getDatabase(Landroid/content/Context;)Lcom/example/education/core/database/EducationDatabase;",
            
            // 网络模块初始化
            "Lcom/example/education/core/network/NetworkModule;->provideOkHttpClient()Lokhttp3/OkHttpClient;",
            "Lcom/example/education/core/network/NetworkModule;->provideDifyApiService(Lokhttp3/OkHttpClient;)Lcom/example/education/core/network/DifyApiService;",
            
            // 角色管理初始化
            "Lcom/example/education/core/user/RoleManager;->getCurrentRole()Lkotlinx/coroutines/flow/Flow;"
        )
    }
    
    /**
     * 获取UI渲染关键路径
     */
    fun getUIRenderingMethods(): List<String> {
        return listOf(
            // Compose 主题设置
            "Lcom/example/education/core/common_ui/theme/ThemeKt;->EducationTheme(ZZLkotlin/jvm/functions/Function2;)V",
            
            // 关键 Composable 函数
            "Lcom/example/education/feature_teacher/dashboard/TeacherDashboardScreenKt;->TeacherDashboardScreen(Landroidx/navigation/NavController;Lcom/example/education/feature_teacher/dashboard/TeacherDashboardViewModel;Landroidx/compose/runtime/Composer;I)V",
            "Lcom/example/education/feature_student/reader/StudentReaderScreenKt;->StudentReaderScreen(Ljava/lang/String;Landroidx/navigation/NavController;Lcom/example/education/feature_student/reader/StudentReaderViewModel;Landroidx/compose/runtime/Composer;I)V",
            "Lcom/example/education/ui/chat/ChatScreenKt;->ChatScreen(Landroidx/navigation/NavController;Lcom/example/education/ui/chat/ChatViewModel;Landroidx/compose/runtime/Composer;I)V",
            
            // ViewModel 状态管理
            "Lcom/example/education/feature_teacher/dashboard/TeacherDashboardViewModel;->loadDashboardData()V",
            "Lcom/example/education/feature_student/reader/StudentReaderViewModel;->loadChapterContent(Ljava/lang/String;)V",
            "Lcom/example/education/ui/chat/ChatViewModel;->sendMessage(Ljava/lang/String;)V"
        )
    }
    
    /**
     * 输出完整的 Baseline Profiles 配置文件内容
     */
    fun generateBaselineProfilesContent(): String {
        Log.d(TAG, "生成完整的Baseline Profiles配置")
        
        val profiles = generateProfiles()
        val startupMethods = getStartupMethods()
        val uiMethods = getUIRenderingMethods()
        
        return buildString {
            appendLine("# Education App Baseline Profiles")
            appendLine("# Generated for performance optimization")
            appendLine("")
            
            appendLine("# Core Classes")
            profiles.forEach { profile ->
                appendLine(profile)
            }
            
            appendLine("")
            appendLine("# Startup Critical Methods")
            startupMethods.forEach { method ->
                appendLine(method)
            }
            
            appendLine("")
            appendLine("# UI Rendering Methods")
            uiMethods.forEach { method ->
                appendLine(method)
            }
        }
    }
} 