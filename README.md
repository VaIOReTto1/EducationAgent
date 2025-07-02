# 🎓 智能教学平台 Android App

一个基于 **Offline-First** 架构的智能教学平台，支持教师/学生端切换，集成 AI 工作流编排对话系统。

## ✨ 核心特性

### 🚀 技术亮点
- **纯 Kotlin + Jetpack Compose** - 现代化 UI 开发
- **Clean Architecture + MVI** - 单向数据流，状态可预测
- **Offline-First** - Room 本地缓存 + WorkManager 增量同步
- **Firebase 实时同步** - SharedFlow ↔ Firebase Realtime DB 秒级同步
- **AI 智能体集成** - 基于 Dify API 的五智能体对话系统
- **Material 3 设计** - 支持深色模式和无障碍功能

### 🤖 五大智能体系统
1. **Curriculum（课程规划）** - AI 辅助课程设计和教学计划
2. **Tutoring（个性化辅导）** - 基于学习风格的个性化指导
3. **Assessment（评估反馈）** - 智能学习评估和进度分析
4. **KB（知识检索）** - 智能知识库搜索和推荐
5. **Dialogue（对话管理）** - 自然语言交互和智能路由

### 📱 双端功能

#### 教师端
- 📊 **教学仪表盘** - 实时统计教学效率指数和学生学习效果
- 🧠 **AI 备课助手** - 智能课程规划和教学内容生成
- 📚 **知识库管理** - 智能内容组织和检索
- 📝 **智能评估** - 自动化学习评估和反馈

#### 学生端
- 📖 **智能阅读器** - 自适应章节学习和进度跟踪
- 👨‍🏫 **AI 个性化辅导** - 基于学习数据的智能指导
- 📈 **学习进度** - 可视化学习统计和目标管理
- 💬 **智能对话** - 自然语言学习助手

## 🛠 技术栈

### 核心框架
- **Kotlin 2.0.20** - 现代化编程语言
- **Jetpack Compose** - 声明式 UI 框架
- **Material 3** - Google 最新设计系统

### 架构组件
- **Hilt** - 依赖注入框架
- **Room** - 本地数据库 + FTS5 全文搜索
- **Paging 3** - 数据分页加载
- **WorkManager** - 后台任务调度
- **Navigation Compose** - 导航管理

### 网络 & 同步
- **OkHttp 5** - HTTP 客户端 + 证书锁定
- **Retrofit** - RESTful API 客户端
- **Moshi** - JSON 序列化
- **Firebase Realtime Database** - 实时数据同步

### 性能优化
- **Baseline Profiles** - 编译时性能优化
- **Macrobenchmark** - 性能测试工具
- **Coil** - 图片加载库

## 🚀 快速开始

### 环境要求
- **Android Studio**: Electric Eel | 2022.1.1 或更高版本
- **Kotlin**: 2.0.20
- **Android Gradle Plugin**: 8.7.0
- **最低 SDK**: 24 (Android 7.0)
- **目标 SDK**: 35 (Android 15)

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-repo/education-platform.git
   cd education-platform
   ```

2. **配置 API Key**
   
   在 `app/src/main/java/com/example/education/core/network/ApiConstants.kt` 中配置 Dify API Key：
   
   ```kotlin
   object ApiConstants {
       const val DIFY_API_KEY = "app-zfuqOwt7yPevhnLoPx1yAtoQ"
       // ... 其他配置
   }
   ```

3. **配置 Firebase**
   
   - 在 Firebase Console 创建新项目
   - 下载 `google-services.json` 文件到 `app/` 目录
   - 替换项目中的示例文件

4. **构建项目**
   ```bash
   ./gradlew build
   ```

5. **运行应用**
   ```bash
   ./gradlew installDebug
   ```

## 📁 项目结构

```
📦 com.example.education
├─ 📱 app                          # 应用入口和导航
│   ├─ MainActivity.kt             # 主界面，支持角色切换
│   └─ navigation/                 # 导航配置
├─ 🧠 agent/                       # AI 智能体模块
│   ├─ AgentRepository.kt          # 智能体仓库接口
│   ├─ AgentRepositoryImpl.kt      # Dify API 集成实现
│   └─ StudentContext.kt           # 学生学习上下文
├─ 🏗 core/                        # 核心基础模块
│   ├─ network/                    # 网络层
│   │   ├─ DifyApiService.kt       # Dify API 服务
│   │   └─ models/                 # 数据模型
│   ├─ database/                   # 数据库层
│   │   ├─ EducationDatabase.kt    # Room 数据库
│   │   ├─ entities/               # 数据实体
│   │   └─ dao/                    # 数据访问对象
│   ├─ repository/                 # 仓库层
│   ├─ sync/                       # 数据同步
│   │   ├─ FirebaseSyncRepository.kt # Firebase 同步
│   │   └─ SyncWorker.kt           # 后台同步任务
│   ├─ user/                       # 用户管理
│   │   └─ RoleManager.kt          # 角色切换管理
│   └─ service/                    # 业务服务
├─ 👨‍🏫 feature_teacher/             # 教师端功能
│   └─ dashboard/                  # 教学仪表盘
│       ├─ TeacherDashboardScreen.kt
│       ├─ TeacherDashboardViewModel.kt
│       └─ usecase/                # 业务用例
├─ 👨‍🎓 feature_student/             # 学生端功能
│   └─ reader/                     # 学习阅读器
│       ├─ StudentReaderScreen.kt
│       ├─ StudentReaderViewModel.kt
│       └─ usecase/                # 业务用例
└─ 💬 ui/                          # 通用 UI 组件
    ├─ chat/                       # 聊天界面
    └─ theme/                      # 主题配置
```

## 🔧 配置说明

### Dify API 配置

项目使用 Dify 工作流编排对话型应用 API，需要配置以下参数：

```kotlin
// ApiConstants.kt
object ApiConstants {
    const val DIFY_API_KEY = "app-zfuqOwt7yPevhnLoPx1yAtoQ"
    const val BASE_URL = "https://api.dify.ai/v1/"
    
    // 智能体端点映射
    val AGENT_ENDPOINTS = mapOf(
        "curriculum" to "chat-messages",
        "tutoring" to "chat-messages", 
        "assessment" to "chat-messages",
        "kb" to "chat-messages",
        "dialogue" to "chat-messages"
    )
}
```

### Firebase 配置

1. 创建 Firebase 项目
2. 启用 Realtime Database
3. 配置安全规则：

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "courses": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "learning_progress": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

### 网络安全配置

应用使用 OkHttp 证书锁定确保网络安全：

```xml
<!-- network_security_config.xml -->
<network-security-config>
    <domain-config>
        <domain includeSubdomains="true">api.dify.ai</domain>
        <pin-set>
            <pin digest="SHA-256">AAAAAAAAAAAAAAAAAAAAAA=</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

## 🏗 架构设计

### MVI 架构模式

```kotlin
// ViewModel 示例
@HiltViewModel
class TeacherDashboardViewModel @Inject constructor(
    private val getTeachingStatsUseCase: GetTeachingStatsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadStats -> loadTeachingStats()
            is DashboardIntent.RefreshData -> refreshData()
        }
    }
}
```

### Offline-First 数据流

```
本地操作 → Room 数据库 → SharedFlow 变更通知 → WorkManager 后台同步 → Firebase
    ↑                                                                    ↓
    ←←←←←←←←←←←← Firebase 监听变更 ← Retrofit 网络请求 ←←←←←←←←←←←←←←←
```

## 🧪 测试策略

### 单元测试
```kotlin
// UseCase 测试示例
@Test
fun `should calculate teaching efficiency correctly`() = runTest {
    // Given
    val mockStats = TeachingStats(...)
    
    // When  
    val result = getTeachingStatsUseCase.execute()
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(expectedEfficiency, result.getOrNull()?.teachingEfficiencyIndex)
}
```

### UI 测试
```kotlin
@Test
fun testTeacherDashboardDisplaysCorrectStats() {
    composeTestRule.setContent {
        TeacherDashboardScreen(...)
    }
    
    composeTestRule.onNodeWithText("教学效率指数").assertIsDisplayed()
}
```

## 📊 性能优化

### Baseline Profiles
项目配置了 Baseline Profiles 来优化启动性能：

```kotlin
// BaselineProfilesGenerator.kt
@ExperimentalBaselineProfilesApi
class BaselineProfilesGenerator {
    @Test
    fun generate() {
        generateBaselineProfile(profilePackageName = "com.example.education") {
            startActivityAndWait()
            
            // 关键用户路径
            device.findObject(By.text("仪表盘")).click()
            device.waitForIdle()
            
            device.findObject(By.text("AI辅导")).click()
            device.waitForIdle()
        }
    }
}
```

### 内存优化
- 使用 `remember` 缓存 Compose 状态
- LazyColumn 虚拟化长列表
- Coil 图片缓存和内存管理

## 🚀 CI/CD 配置

### GitHub Actions 工作流

```yaml
# .github/workflows/ci.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'adopt'
      
      - name: Run Tests
        run: ./gradlew test
      
      - name: Run UI Tests
        run: ./gradlew connectedAndroidTest
```

## 🔒 安全特性

- **证书锁定** - 防止中间人攻击
- **数据加密** - 本地数据 AES-GCM 加密
- **网络安全** - HTTPS 强制使用
- **权限最小化** - 仅申请必要权限

## 📱 兼容性

- **最低版本**: Android 7.0 (API 24)
- **目标版本**: Android 15 (API 35)
- **架构支持**: arm64-v8a, armeabi-v7a, x86_64
- **屏幕支持**: 手机、平板、折叠屏

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 开源协议

本项目基于 MIT 协议开源。详见 [LICENSE](LICENSE) 文件。

## 👥 团队

- **项目架构师**: [@your-name]
- **Android 开发**: [@your-name]
- **UI/UX 设计**: [@your-name]
- **后端集成**: [@your-name]

## 📞 联系我们

- **项目主页**: https://github.com/your-repo/education-platform
- **问题反馈**: https://github.com/your-repo/education-platform/issues
- **邮箱**: contact@yourcompany.com

---

**🎓 让每个学习者都能享受智能化的教育体验！** 