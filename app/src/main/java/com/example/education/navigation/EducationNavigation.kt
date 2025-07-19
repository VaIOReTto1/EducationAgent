package com.example.education.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.education.feature_teacher.dashboard.TeacherDashboardScreen
import com.example.education.feature_teacher.TeacherMainLayout
import com.example.education.feature_student.StudentMainScreen
import com.example.education.feature_student.reader.StudentReaderScreen
import com.example.education.feature_student.quiz.QuizScreen
import com.example.education.feature_student.StudentLearningScreen
import com.example.education.feature_student.StudentTutoringScreen
import com.example.education.feature_teacher.TeacherAILessonPlanScreen
import com.example.education.ui.chat.ChatScreen
import com.example.education.core.user.RoleManager
import com.example.education.core.user.UserRole
import com.example.education.feature_auth.LoginScreen
import com.example.education.feature_settings.SettingsScreen
import com.example.education.feature_knowledge.KnowledgeBaseScreen
import com.example.education.feature_assessment.AssessmentScreen
import com.example.education.feature_ai_learning.AILearningScreen
import com.example.education.feature_student.StudentMainLayout

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
    onLoginSuccess: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    NavHost(
        navController = navController,
        startDestination = startDestination ?: getStartDestination(currentRole)
    ) {
        // 教师端页面 - 带顶部应用栏和底部导航的主布局
        composable(NavigationRoute.TEACHER_DASHBOARD) {
            TeacherMainLayout(
                navController = navController
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
        
        // 学生端页面 - 带底部导航的主布局
    composable(NavigationRoute.STUDENT_MAIN) {
        StudentMainLayout(
            navController = navController
        )
    }
    
    composable("${NavigationRoute.STUDENT_READER}/{chapterId}") { backStackEntry ->
        val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
        StudentLearningScreen(
            navController = navController
        )
    }
    
    composable(NavigationRoute.STUDENT_CHAT) {
        StudentMainLayout(
            navController = navController
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
            // 学生端 - 在主布局内显示AI辅导页面
            StudentMainLayout(
                navController = navController
            )
        }
        
        // 知识库页面 - 在教师端主布局内显示
        composable(NavigationRoute.KNOWLEDGE_BASE) {
            TeacherMainLayout(
                navController = navController
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
        
        // 评估页面 - 在教师端主布局内显示
        composable(NavigationRoute.ASSESSMENT) {
            TeacherMainLayout(
                navController = navController
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
            if (currentRole == RoleManager.ROLE_TEACHER) {
                // 教师端 - 在主布局内显示AI备课页面
                TeacherMainLayout(
                    navController = navController
                )
            } else {
                // 学生端 - 在主布局内显示AI学习页面
                StudentMainLayout(
                    navController = navController
                )
            }
        }
        
        // 认证页面
        composable(NavigationRoute.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role: UserRole ->
                    onLoginSuccess?.invoke()
                }
            )
        }
        
        // 通用页面 - 设置页面作为独立页面
        composable(NavigationRoute.SETTINGS) {
            SettingsScreen(
                onLogout = {
                    onLogout?.invoke()
                },
                onBack = {
                    navController.popBackStack()
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
    const val STUDENT_MAIN = "student_main"
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
        RoleManager.ROLE_STUDENT -> NavigationRoute.STUDENT_MAIN
        else -> NavigationRoute.STUDENT_MAIN
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