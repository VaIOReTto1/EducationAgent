# 智能教学平台 Android 应用

一个基于 Offline-First 架构的智能教学平台，支持教师和学生角色切换，集成了 AI 工作流编排对话型应用 API。

## 🚀 功能特性

### 教师端功能
- 📊 **教师仪表盘** - 课程概览、学生统计、教学分析
- 📚 **课程管理** - 创建、编辑、删除课程
- 📝 **课程内容管理** - 章节管理、评估创建、内容导入导出
- 👥 **学生分析** - 学习进度跟踪、成绩分析
- ✅ **作业批改** - 在线批改、评分管理

### 学生端功能
- 📖 **课程学习** - 浏览已注册课程
- 📄 **章节阅读** - 沉浸式阅读体验
- 🎯 **随堂练习** - 互动式学习评估
- 📈 **学习进度** - 个人学习数据追踪

### 技术特性
- 🔄 **Offline-First** - 本地优先，支持离线使用
- 🎭 **角色切换** - 教师/学生身份无缝切换
- 🤖 **AI 集成** - 基于 Dify API 的智能对话系统
- ⚡ **实时同步** - Firebase + SharedFlow 秒级数据同步
- 🎨 **Material 3** - 现代化 UI 设计，支持深色模式
- 🔒 **安全保障** - 证书锁定、AES-GCM 加密

## 🏗️ 技术架构

### 架构模式
- **Clean Architecture** - 分层架构，职责分离
- **MVI (Model-View-Intent)** - 单向数据流，状态管理
- **Offline-First** - 本地缓存优先，增量同步

### 技术栈
- **语言**: Kotlin 100%
- **UI**: Jetpack Compose + Material 3
- **依赖注入**: Hilt
- **数据库**: Room + FTS5 全文搜索
- **网络**: Retrofit + OkHttp5
- **异步**: Coroutines + Flow
- **导航**: Navigation Compose
- **分页**: Paging 3
- **后台任务**: WorkManager
- **数据存储**: DataStore
- **实时同步**: Firebase Realtime Database

### 模块结构
```
📦 com.example.education
├─ app (应用入口, 导航)
├─ feature_teacher (教师端功能模块)
│   ├─ dashboard (仪表盘)
│   ├─ course_management (课程管理)
│   └─ course_content (课程内容)
├─ feature_student (学生端功能模块)
│   ├─ courses (课程列表)
│   ├─ reader (阅读器)
│   └─ chapter_reader (章节阅读)
├─ core (核心模块)
│   ├─ network (网络层)
│   ├─ database (数据库层)
│   ├─ sync (同步层)
│   ├─ common (通用组件)
│   └─ ui (UI 组件)
└─ agent (AI 代理模块)
    ├─ curriculum (课程代理)
    ├─ tutoring (辅导代理)
    ├─ assessment (评估代理)
    ├─ kb (知识库代理)
    └─ dialogue (对话代理)
```

## 🛠️ 开发环境设置

### 系统要求
- **Android Studio**: Hedgehog (2023.1.1) 或更高版本
- **JDK**: 17 或更高版本
- **Android SDK**: API 24+ (Android 7.0)
- **Kotlin**: 2.0.21
- **Gradle**: 8.10.0

### 克隆项目
```bash
git clone https://github.com/your-username/education-platform.git
cd education-platform
```

### API Key 配置

1. 在项目根目录创建 `local.properties` 文件
2. 添加 Dify API Key：
```properties
DIFY_API_KEY=app-zfuqOwt7yPevhnLoPx1yAtoQ
```

### Firebase 配置

1. 在 [Firebase Console](https://console.firebase.google.com/) 创建新项目
2. 添加 Android 应用，包名：`com.example.education`
3. 下载 `google-services.json` 文件到 `app/` 目录
4. 启用以下服务：
   - Authentication
   - Realtime Database
   - Cloud Firestore

### 构建和运行

```bash
# 清理项目
./gradlew clean

# 构建 Debug 版本
./gradlew assembleDebug

# 运行单元测试
./gradlew test

# 运行 UI 测试
./gradlew connectedAndroidTest

# 安装到设备
./gradlew installDebug
```

## 🧪 测试

### 测试策略
- **单元测试**: JUnit5 + Turbine (Flow 测试)
- **集成测试**: Hilt 测试 + Room 测试
- **UI 测试**: Compose UI Test
- **端到端测试**: Gradle Managed Devices

### 运行测试
```bash
# 运行所有单元测试
./gradlew test

# 运行特定模块测试
./gradlew :feature_teacher:test

# 运行 UI 测试
./gradlew connectedAndroidTest

# 生成测试报告
./gradlew jacocoTestReport
```

## 📊 性能优化

### Baseline Profiles
项目集成了 Baseline Profiles 用于启动性能优化：

```bash
# 生成 Baseline Profile
./gradlew generateBaselineProfile

# 运行性能基准测试
./gradlew benchmarkRelease
```

### Macrobenchmark
性能基准测试配置在 `benchmark/` 模块：

```bash
# 运行启动基准测试
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

## 🔧 调试工具

### Debug 构建集成工具
- **Flipper**: 网络请求、数据库检查
- **LeakCanary**: 内存泄漏检测
- **Compose Layout Inspector**: UI 调试

### 启用调试工具
在 `local.properties` 中添加：
```properties
ENABLE_FLIPPER=true
ENABLE_LEAKCANARY=true
```

## 🚀 部署

### Release 构建
```bash
# 构建 Release APK
./gradlew assembleRelease

# 构建 AAB (推荐用于 Play Store)
./gradlew bundleRelease
```

### 签名配置
在 `local.properties` 中配置签名信息：
```properties
KEYSTORE_FILE=path/to/keystore.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

## 📱 使用指南

### 角色切换
1. 点击顶部导航栏的角色切换按钮
2. 选择「教师端」或「学生端」
3. 应用会自动重建导航结构

### 教师端使用
1. **仪表盘**: 查看课程概览和学生统计
2. **课程管理**: 创建新课程或编辑现有课程
3. **内容管理**: 添加章节、创建评估、导入导出内容

### 学生端使用
1. **我的课程**: 浏览已注册的课程
2. **课程阅读**: 选择课程进入阅读模式
3. **章节学习**: 逐章节学习，完成练习

## 🤖 AI 功能

### 对话系统
基于 Dify API 的五个智能代理：
- **Curriculum**: 课程规划和设计
- **Tutoring**: 个性化辅导
- **Assessment**: 智能评估
- **KB**: 知识库检索
- **Dialogue**: 自然语言对话

### API 集成
所有 AI 功能通过 `/chat-messages` 端点进行 SSE 流式交互。

## 🔄 数据同步

### 同步策略
- **本地优先**: 所有操作先保存到本地数据库
- **增量同步**: 仅同步变更的数据
- **冲突解决**: 基于时间戳的冲突解决机制
- **离线支持**: 离线时数据缓存，联网后自动同步

### 同步触发
- 应用启动时自动同步
- 网络状态变化时同步
- 用户手动下拉刷新
- 后台定时同步（WorkManager）

## 🐛 故障排除

### 常见问题

**Q: 编译失败，提示找不到 Dify API Key**
A: 确保在 `local.properties` 中正确配置了 `DIFY_API_KEY`

**Q: Firebase 初始化失败**
A: 检查 `google-services.json` 文件是否正确放置在 `app/` 目录

**Q: 数据库迁移错误**
A: 清除应用数据或卸载重装应用

**Q: 网络请求失败**
A: 检查网络连接和 API 端点配置

### 日志查看
```bash
# 查看应用日志
adb logcat | grep "EducationApp"

# 查看网络请求日志
adb logcat | grep "OkHttp"

# 查看数据库日志
adb logcat | grep "Room"
```

## 🤝 贡献指南

### 开发流程
1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

### 代码规范
- 遵循 [Kotlin 编码规范](https://kotlinlang.org/docs/coding-conventions.html)
- 使用中文注释和日志
- 保持代码覆盖率 > 80%
- 所有 Compose 组件必须有 Preview

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系我们

- **项目维护者**: [Your Name](mailto:your.email@example.com)
- **问题反馈**: [GitHub Issues](https://github.com/your-username/education-platform/issues)
- **功能建议**: [GitHub Discussions](https://github.com/your-username/education-platform/discussions)

---

**智能教学平台** - 让教育更智能，让学习更高效 🎓✨