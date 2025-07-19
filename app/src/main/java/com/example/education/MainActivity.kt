package com.example.education

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.education.core.database.DataInitializer
import com.example.education.core.user.RoleManager
import com.example.education.core.user.UserRole
import com.example.education.navigation.EducationNavigation
import com.example.education.navigation.NavigationRoute
import com.example.education.ui.theme.EducationTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主界面Activity
 * 
 * 智能教学平台的主入口，支持教师/学生端切换
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var dataInitializer: DataInitializer
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "MainActivity启动")
        
        // 初始化应用基础数据
        lifecycleScope.launch {
            dataInitializer.initializeData()
        }
        
        setContent {
            EducationTheme {
                MainScreen()
            }
        }
    }
}

/**
 * 主界面UI状态
 */
data class MainUiState(
    val currentRole: UserRole = UserRole.STUDENT,
    val currentUserId: String? = null,
    val currentUserName: String? = null,
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

/**
 * 主界面组合函数
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    
    LaunchedEffect(Unit) {
        mainViewModel.checkLoginStatus()
    }
    
    // 根据登录状态决定显示内容
    when {
        uiState.isLoading -> {
            LoadingScreen()
        }
        !uiState.isLoggedIn -> {
            // 显示登录页面作为独立页面
            EducationNavigation(
                navController = navController,
                currentRole = uiState.currentRole.toString(),
                startDestination = NavigationRoute.LOGIN,
                onLoginSuccess = {
                    mainViewModel.onLoginSuccess()
                },
                onLogout = {
                    mainViewModel.logout()
                }
            )
        }
        uiState.error != null -> {
            ErrorScreen(
                error = uiState.error!!,
                onRetry = { mainViewModel.checkLoginStatus() }
            )
        }
        else -> {
            // 显示主界面，但设置页面作为独立页面
            EducationNavigation(
                navController = navController,
                currentRole = uiState.currentRole.toString(),
                startDestination = getStartDestination(uiState.currentRole.toString()),
                onLoginSuccess = {
                    mainViewModel.onLoginSuccess()
                },
                onLogout = {
                    mainViewModel.logout()
                }
            )
        }
    }
}

/**
 * 根据角色获取起始目标
 */
private fun getStartDestination(role: String): String {
    return when (role) {
        "TEACHER" -> NavigationRoute.TEACHER_DASHBOARD
        "STUDENT" -> NavigationRoute.STUDENT_MAIN
        else -> NavigationRoute.STUDENT_MAIN
    }
}

// 移除MainContent函数，因为现在所有页面都通过导航处理

/**
 * 加载屏幕
 */
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "正在初始化...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * 错误屏幕
 */
@Composable
fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            
            Text(
                text = "出现错误",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(onClick = onRetry) {
                Text("重试")
            }
        }
    }
}

/**
 * 主顶部应用栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    currentRole: UserRole,
    currentUserName: String?,
    onRoleSwitch: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "智能教学平台",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (currentUserName != null) {
    Text(
                        text = "$currentUserName · ${currentRole.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            // 角色切换按钮
            IconButton(onClick = onRoleSwitch) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "切换角色",
                    tint = MaterialTheme.colorScheme.primary
    )
}

            // 设置按钮
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

/**
 * 主底部导航栏
 */
@Composable
fun MainBottomBar(
    currentRole: UserRole,
    navController: NavHostController
) {
    NavigationBar {
        when (currentRole) {
            UserRole.TEACHER -> {
                // 教师端导航
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("仪表盘") },
                    selected = false, // TODO: 根据当前路由判断
                    onClick = { navController.navigate(NavigationRoute.TEACHER_DASHBOARD) }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Create, contentDescription = null) },
                    label = { Text("AI备课") },
                    selected = false,
                    onClick = { navController.navigate(NavigationRoute.TEACHER_CHAT) }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Storage, contentDescription = null) },
                    label = { Text("知识库") },
                    selected = false,
                    onClick = { navController.navigate(NavigationRoute.KNOWLEDGE_BASE) }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null) },
                    label = { Text("智能评估") },
                    selected = false,
                    onClick = { navController.navigate(NavigationRoute.ASSESSMENT) }
                )
            }
            
            UserRole.STUDENT -> {
                // 学生端导航
                NavigationBarItem(
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text("学习") },
                    selected = false,
                    onClick = { navController.navigate(NavigationRoute.STUDENT_CHAT) }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Help, contentDescription = null) },
                    label = { Text("AI辅导") },
                    selected = false,
                    onClick = { navController.navigate(NavigationRoute.TUTORING_CHAT) }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Psychology, contentDescription = null) },
                    label = { Text("AI学习") },
                    selected = false,
                    onClick = { navController.navigate(NavigationRoute.AI_LEARNING) }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("设置") },
                    selected = false,
                    onClick = { navController.navigate(NavigationRoute.SETTINGS) }
                )
            }
        }
    }
}

/**
 * 主界面ViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val roleManager: RoleManager
) : ViewModel() {
    
    companion object {
        private const val TAG = "MainViewModel"
    }
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        Log.d(TAG, "MainViewModel初始化")
        observeUserData()
    }
    
    /**
     * 观察用户数据变化
     */
    private fun observeUserData() {
        viewModelScope.launch {
            combine(
                roleManager.currentRole,
                roleManager.currentUserId,
                roleManager.currentUserName
            ) { role, userId, userName ->
                Triple(role, userId, userName)
            }.collect { (role, userId, userName) ->
                _uiState.value = _uiState.value.copy(
                    currentRole = role,
                    currentUserId = userId,
                    currentUserName = userName,
                    isLoading = false
                )
                Log.d(TAG, "用户数据更新: $userName, 角色: ${role.displayName}")
            }
        }
    }
    
    /**
     * 检查登录状态
     */
    fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "检查登录状态")
                
                val currentUserId = roleManager.getCurrentUserId()
                val isLoggedIn = currentUserId != null
                
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = isLoggedIn,
                    isLoading = false
                )
                
                Log.d(TAG, "登录状态: $isLoggedIn, 用户ID: $currentUserId")
                
            } catch (e: Exception) {
                Log.e(TAG, "检查登录状态失败", e)
                _uiState.value = _uiState.value.copy(
                    error = "检查登录状态失败: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * 用户登录成功后调用
     */
    fun onLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoggedIn = true)
    }
    
    /**
     * 切换角色
     */
    fun switchRole() {
        viewModelScope.launch {
            try {
                val currentRole = _uiState.value.currentRole
                val newRole = if (currentRole == UserRole.TEACHER) {
                    UserRole.STUDENT
                } else {
                    UserRole.TEACHER
                }
                
                roleManager.setUserRole(newRole)
                Log.d(TAG, "角色切换: ${currentRole.displayName} -> ${newRole.displayName}")
                
            } catch (e: Exception) {
                Log.e(TAG, "角色切换失败", e)
                _uiState.value = _uiState.value.copy(
                    error = "角色切换失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 退出登录
     */
    fun logout() {
        viewModelScope.launch {
            try {
                roleManager.clearUserData()
                _uiState.value = _uiState.value.copy(isLoggedIn = false)
                Log.d(TAG, "用户已退出登录")
            } catch (e: Exception) {
                Log.e(TAG, "退出登录失败", e)
                _uiState.value = _uiState.value.copy(
                    error = "退出登录失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除错误状态
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}