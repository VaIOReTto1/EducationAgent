package com.example.education.feature_teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.education.navigation.NavigationRoute
import com.example.education.feature_teacher.dashboard.TeacherDashboardScreen
import com.example.education.feature_teacher.TeacherAILessonPlanScreen
import com.example.education.feature_knowledge.KnowledgeBaseScreen
import com.example.education.feature_assessment.AssessmentScreen
import com.example.education.ui.chat.ChatScreen

/**
 * 教师端主布局 - 包含顶部应用栏和底部导航栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherMainLayout(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // 使用内部状态管理选中的标签页，默认显示仪表盘
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "智能教学平台",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "教师工作台",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(NavigationRoute.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("仪表盘") },
                    selected = selectedTab == 0,
                    onClick = { 
                        selectedTab = 0
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Create, contentDescription = null) },
                    label = { Text("AI备课") },
                    selected = selectedTab == 1,
                    onClick = { 
                        selectedTab = 1
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Storage, contentDescription = null) },
                    label = { Text("知识库") },
                    selected = selectedTab == 2,
                    onClick = { 
                        selectedTab = 2
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null) },
                    label = { Text("智能评估") },
                    selected = selectedTab == 3,
                    onClick = { 
                        selectedTab = 3
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> {
                    TeacherDashboardScreen(
                        onNavigateToAssessment = { courseId, assessmentType ->
                            navController.navigate("${NavigationRoute.ASSESSMENT_CHAT}/$courseId/$assessmentType")
                        }
                    )
                }
                1 -> {
                     TeacherAILessonPlanScreen(
                         navController = navController
                     )
                 }
                 2 -> {
                     KnowledgeBaseScreen(
                         onCourseClick = { courseId: String ->
                             navController.navigate("${NavigationRoute.KNOWLEDGE_BASE_CHAT}/$courseId")
                         }
                     )
                 }
                 3 -> {
                     AssessmentScreen(
                         onStartAssessment = { courseId: String, assessmentType: String ->
                             navController.navigate("${NavigationRoute.ASSESSMENT_CHAT}/$courseId/$assessmentType")
                         }
                     )
                 }
                else -> {
                    // 默认显示仪表盘
                    TeacherDashboardScreen(
                        onNavigateToAssessment = { courseId, assessmentType ->
                            navController.navigate("${NavigationRoute.ASSESSMENT_CHAT}/$courseId/$assessmentType")
                        }
                    )
                }
            }
        }
    }
}