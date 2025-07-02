package com.example.education.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.education.feature_teacher.dashboard.TeacherDashboardScreen
import com.example.education.feature_student.reader.StudentReaderScreen
import com.example.education.ui.chat.ChatScreen
import com.example.education.core.user.RoleManager

/**
 * 智能教学平台导航配置
 * 
 * 统一配置所有路由，运行时根据角色控制访问权限
 */
@Composable
fun EducationNavigation(
    navController: NavHostController = rememberNavController(),
    currentRole: String = RoleManager.ROLE_STUDENT
) {
    NavHost(
        navController = navController,
        startDestination = getStartDestination(currentRole)
    ) {
        // 教师端页面
        composable(NavigationRoute.TEACHER_DASHBOARD) {
            TeacherDashboardScreen()
        }
        
        composable(NavigationRoute.TEACHER_CHAT) {
            ChatScreen(
                agentType = "teacher",
                title = "智能备课助手"
            )
        }
        
        // 学生端页面
        composable("${NavigationRoute.STUDENT_READER}/{chapterId}") { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            StudentReaderScreen(chapterId = chapterId)
        }
        
        composable(NavigationRoute.STUDENT_CHAT) {
            ChatScreen(
                agentType = "student",
                title = "AI学习助手"
            )
        }
        
        // 共享的AI智能体页面
        composable(NavigationRoute.TUTORING) {
            ChatScreen(
                agentType = "tutoring",
                title = "智能辅导"
            )
        }
        
        composable(NavigationRoute.KNOWLEDGE_BASE) {
            ChatScreen(
                agentType = "knowledge_base",
                title = "知识库管理"
            )
        }
        
        composable(NavigationRoute.ASSESSMENT) {
            ChatScreen(
                agentType = "assessment", 
                title = "智能评估"
            )
        }
        
        // 通用页面
        composable(NavigationRoute.SETTINGS) {
            // TODO: 实现设置页面
            ChatScreen(
                agentType = "student",
                title = "设置"
            )
        }
        
        composable(NavigationRoute.ROLE_SWITCH) {
            // TODO: 实现角色切换页面
            ChatScreen(
                agentType = "student",
                title = "角色切换"
            )
        }
    }
}

/**
 * 导航路由常量
 */
object NavigationRoute {
    // 教师端路由
    const val TEACHER_DASHBOARD = "teacher_dashboard"
    const val TEACHER_CHAT = "teacher_chat"
    const val KNOWLEDGE_BASE = "knowledge_base"
    const val ASSESSMENT = "assessment"
    
    // 学生端路由
    const val STUDENT_READER = "student_reader"
    const val STUDENT_CHAT = "student_chat"
    const val TUTORING = "tutoring"
    
    // 通用路由
    const val SETTINGS = "settings"
    const val ROLE_SWITCH = "role_switch"
}

/**
 * 根据角色获取起始目标
 */
private fun getStartDestination(role: String): String {
    return when (role) {
        RoleManager.ROLE_TEACHER -> NavigationRoute.TEACHER_DASHBOARD
        RoleManager.ROLE_STUDENT -> NavigationRoute.STUDENT_CHAT
        else -> NavigationRoute.STUDENT_CHAT
    }
}

/**
 * 导航扩展函数
 */
fun NavHostController.navigateToTeacherDashboard() {
    navigate(NavigationRoute.TEACHER_DASHBOARD) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateToStudentReader(chapterId: String) {
    navigate("${NavigationRoute.STUDENT_READER}/$chapterId")
}

fun NavHostController.navigateToChat(agentType: String) {
    val route = when (agentType) {
        "teacher" -> NavigationRoute.TEACHER_CHAT
        "student" -> NavigationRoute.STUDENT_CHAT
        "tutoring" -> NavigationRoute.TUTORING
        "knowledge_base" -> NavigationRoute.KNOWLEDGE_BASE
        "assessment" -> NavigationRoute.ASSESSMENT
        else -> NavigationRoute.STUDENT_CHAT
    }
    navigate(route)
}

fun NavHostController.navigateToSettings() {
    navigate(NavigationRoute.SETTINGS)
}

fun NavHostController.navigateToRoleSwitch() {
    navigate(NavigationRoute.ROLE_SWITCH)
} 