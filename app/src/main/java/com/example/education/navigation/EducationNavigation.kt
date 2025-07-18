package com.example.education.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.education.feature_teacher.dashboard.TeacherDashboardScreen
import com.example.education.feature_student.reader.StudentReaderScreen
import com.example.education.feature_student.quiz.QuizScreen
import com.example.education.ui.chat.ChatScreen
import com.example.education.core.user.RoleManager
import com.example.education.core.user.UserRole
import com.example.education.feature_auth.LoginScreen
import com.example.education.feature_settings.SettingsScreen
import com.example.education.feature_knowledge.KnowledgeBaseScreen
import com.example.education.feature_assessment.AssessmentScreen
import com.example.education.feature_ai_learning.AILearningScreen

/**
 * 智能教学平台导航配置
 * 
 * 统一配置所有路由，运行时根据角色控制访问权限
 */
@Composable
fun EducationNavigation(
    navController: NavHostController = rememberNavController(),
    currentRole: String = RoleManager.ROLE_STUDENT,
    startDestination: String? = null,
    onLoginSuccess: (() -> Unit)? = null
) {
    NavHost(
        navController = navController,
        startDestination = startDestination ?: getStartDestination(currentRole)
    ) {
        // 教师端页面
        composable(NavigationRoute.TEACHER_DASHBOARD) {
            TeacherDashboardScreen(
                onNavigateToAssessment = { courseId, assessmentType ->
                    navController.navigate("${NavigationRoute.ASSESSMENT_CHAT}/$courseId/$assessmentType")
                }
            )
        }
        
        // 教师端备课助手聊天
        composable(NavigationRoute.TEACHER_CHAT) {
            ChatScreen(
                agentType = "teacher",
                title = "智能备课助手"
            )
        }
        
        // 教师端专用AI聊天（从AI学习页面跳转）
        composable("${NavigationRoute.TEACHER_AI_CHAT}/{toolType}") { backStackEntry ->
            val toolType = backStackEntry.arguments?.getString("toolType") ?: ""
            ChatScreen(
                agentType = "teacher",
                title = "智能备课 - ${getTeachingToolTitle(toolType)}"
            )
        }
        
        // 学生端页面
        composable("${NavigationRoute.STUDENT_READER}/{chapterId}") { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            StudentReaderScreen(
                chapterId = chapterId,
                navController = navController
            )
        }
        
        composable(NavigationRoute.STUDENT_CHAT) {
            ChatScreen(
                agentType = "student",
                title = "AI学习助手"
            )
        }
        
        composable(NavigationRoute.STUDENT_QUIZ) {
            QuizScreen()
        }
        
        // 共享的AI智能体页面
        composable(NavigationRoute.TUTORING) {
            ChatScreen(
                agentType = "tutoring",
                title = "智能辅导"
            )
        }
        
        // 智能辅导详细页面 - 带科目参数
        composable("${NavigationRoute.TUTORING_DETAIL}/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            ChatScreen(
                agentType = "tutoring",
                title = "智能辅导 - ${getTutoringSubjectTitle(subjectId)}"
            )
        }
        
        composable(NavigationRoute.TUTORING_CHAT) {
            ChatScreen(
                agentType = "tutoring",
                title = "AI辅导"
            )
        }
        
        composable(NavigationRoute.KNOWLEDGE_BASE) {
            KnowledgeBaseScreen(
                onCourseClick = { courseId: String ->
                    navController.navigate("${NavigationRoute.KNOWLEDGE_BASE_CHAT}/$courseId")
                }
            )
        }
        
        // 知识库聊天页面 - 带课程ID参数
        composable("${NavigationRoute.KNOWLEDGE_BASE_CHAT}/{courseId}") { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            ChatScreen(
                agentType = "knowledge_base",
                title = "知识库 - ${getCourseTitle(courseId)}"
            )
        }
        
        composable(NavigationRoute.ASSESSMENT) {
            AssessmentScreen(
                onStartAssessment = { courseId: String, assessmentType: String ->
                    navController.navigate("${NavigationRoute.ASSESSMENT_CHAT}/$courseId/$assessmentType")
                }
            )
        }
        
        // 评估聊天页面 - 带课程ID和评估类型参数
        composable("${NavigationRoute.ASSESSMENT_CHAT}/{courseId}/{assessmentType}") { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val assessmentType = backStackEntry.arguments?.getString("assessmentType") ?: ""
            ChatScreen(
                agentType = "assessment",
                title = "智能评估 - ${getAssessmentTitle(assessmentType)}"
            )
        }
        
        // 学生端AI聊天（从AI学习页面跳转）
        composable("${NavigationRoute.STUDENT_AI_CHAT}/{featureType}") { backStackEntry ->
            val featureType = backStackEntry.arguments?.getString("featureType") ?: ""
            ChatScreen(
                agentType = "student",
                title = "AI学习助手 - ${getLearningFeatureTitle(featureType)}"
            )
        }
        
        composable(NavigationRoute.AI_LEARNING) {
            AILearningScreen(
                userRole = if (currentRole == RoleManager.ROLE_TEACHER) UserRole.TEACHER else UserRole.STUDENT,
                onStartChat = { assistantType ->
                    if (currentRole == RoleManager.ROLE_TEACHER) {
                        navController.navigate("${NavigationRoute.TEACHER_AI_CHAT}/$assistantType")
                    } else {
                        navController.navigate("${NavigationRoute.STUDENT_AI_CHAT}/$assistantType")
                    }
                },
                onNavigateToFeature = { featureId ->
                    // 根据功能ID导航到相应页面
                    when (featureId) {
                        "knowledge_base" -> navController.navigate(NavigationRoute.KNOWLEDGE_BASE)
                        "assessment" -> navController.navigate(NavigationRoute.ASSESSMENT)
                        else -> {
                            if (currentRole == RoleManager.ROLE_TEACHER) {
                                navController.navigate("${NavigationRoute.TEACHER_AI_CHAT}/$featureId")
                            } else {
                                navController.navigate("${NavigationRoute.STUDENT_AI_CHAT}/$featureId")
                            }
                        }
                    }
                }
            )
        }
        
        // 认证页面
        composable(NavigationRoute.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role: UserRole ->
                    onLoginSuccess?.invoke()
                }
            )
        }
        
        // 通用页面
        composable(NavigationRoute.SETTINGS) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(NavigationRoute.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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
    // 认证路由
    const val LOGIN = "login"
    
    // 教师端路由
    const val TEACHER_DASHBOARD = "teacher_dashboard"
    const val TEACHER_CHAT = "teacher_chat"
    const val KNOWLEDGE_BASE = "knowledge_base"
    const val ASSESSMENT = "assessment"
    
    // 学生端路由
    const val STUDENT_READER = "student_reader"
    const val STUDENT_CHAT = "student_chat"
    const val STUDENT_QUIZ = "student_quiz"
    const val TUTORING = "tutoring"
    const val TUTORING_CHAT = "tutoring_chat"
    const val TUTORING_DETAIL = "tutoring_detail"
    
    // AI学习助手
    const val AI_LEARNING = "ai_learning"
    
    // 通用路由
    const val SETTINGS = "settings"
    const val ROLE_SWITCH = "role_switch"
    
    // AI聊天路由
    const val KNOWLEDGE_BASE_CHAT = "knowledge_base_chat"
    const val ASSESSMENT_CHAT = "assessment_chat"
    const val TEACHER_AI_CHAT = "teacher_ai_chat"
    const val STUDENT_AI_CHAT = "student_ai_chat"
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
 * 获取课程标题
 */
private fun getCourseTitle(courseId: String): String {
    return "课程 $courseId"
}

/**
 * 获取评估标题
 */
private fun getAssessmentTitle(assessmentType: String): String {
    return "评估 - $assessmentType"
}

/**
 * 获取教学工具标题
 */
private fun getTeachingToolTitle(toolType: String): String {
    return "工具 - $toolType"
}

/**
 * 获取学习功能标题
 */
private fun getLearningFeatureTitle(featureType: String): String {
    return "功能 - $featureType"
}

/**
 * 获取智能辅导科目标题
 */
private fun getTutoringSubjectTitle(subjectId: String): String {
    return "科目 $subjectId"
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

public fun NavHostController.navigateToStudentQuiz() {
    navigate(NavigationRoute.STUDENT_QUIZ)
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

fun NavHostController.navigateToLogin() {
    navigate(NavigationRoute.LOGIN) {
        popUpTo(0) { inclusive = true }
    }
}

fun NavHostController.navigateToKnowledgeBase() {
    navigate(NavigationRoute.KNOWLEDGE_BASE)
}

fun NavHostController.navigateToAssessment() {
    navigate(NavigationRoute.ASSESSMENT)
}

fun NavHostController.navigateToAILearning() {
    navigate(NavigationRoute.AI_LEARNING)
}