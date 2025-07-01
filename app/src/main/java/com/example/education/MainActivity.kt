package com.example.education

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.education.core.common.RoleManager
import com.example.education.navigation.EducationNavigation
import com.example.education.ui.theme.EducationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 主Activity - 应用入口点
 * 负责初始化导航和角色管理
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var roleManager: RoleManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EducationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EducationApp()
                }
            }
        }
    }
}

/**
 * 应用主界面
 * 根据用户角色显示不同的导航结构
 */
@Composable
fun EducationApp() {
    val viewModel: MainViewModel = hiltViewModel()
    val currentRole by viewModel.currentRole.collectAsState()
    
    EducationNavigation(
        currentRole = currentRole,
        onRoleChanged = viewModel::switchRole
    )
}