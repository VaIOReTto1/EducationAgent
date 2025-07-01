package com.example.education.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.education.core.common.UserRole
import com.example.education.feature_student.chapter_reader.ChapterReaderScreen
import com.example.education.feature_student.courses.StudentCoursesScreen
import com.example.education.feature_student.reader.ReaderScreen
import com.example.education.feature_teacher.course_content.CourseContentScreen
import com.example.education.feature_teacher.course_management.TeacherCourseManagementScreen
import com.example.education.feature_teacher.dashboard.TeacherDashboardScreen

/**
 * 应用主导航组件
 * 根据用户角色显示不同的导航结构
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationNavigation(
    currentRole: UserRole,
    onRoleChanged: (UserRole) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // 根据角色获取导航项
    val navigationItems = when (currentRole) {
        UserRole.TEACHER -> teacherNavigationItems
        UserRole.STUDENT -> studentNavigationItems
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                navigationItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // 避免重复导航到同一目的地
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRole) {
                            UserRole.TEACHER -> "教师端"
                            UserRole.STUDENT -> "学生端"
                        }
                    )
                },
                actions = {
                    // 角色切换按钮
                    TextButton(
                        onClick = {
                            val newRole = when (currentRole) {
                                UserRole.TEACHER -> UserRole.STUDENT
                                UserRole.STUDENT -> UserRole.TEACHER
                            }
                            onRoleChanged(newRole)
                        }
                    ) {
                        Text(
                            text = when (currentRole) {
                                UserRole.TEACHER -> "切换到学生端"
                                UserRole.STUDENT -> "切换到教师端"
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = when (currentRole) {
                UserRole.TEACHER -> TeacherRoutes.Dashboard.route
                UserRole.STUDENT -> StudentRoutes.Courses.route
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
            }
        ) {
            // 教师端路由
            composable(TeacherRoutes.Dashboard.route) {
                TeacherDashboardScreen(
                    onNavigateToCourseManagement = {
                        navController.navigate(TeacherRoutes.CourseManagement.route)
                    },
                    onNavigateToCourseContent = { courseId ->
                        navController.navigate("${TeacherRoutes.CourseContent.route}/$courseId")
                    }
                )
            }
            
            composable(TeacherRoutes.CourseManagement.route) {
                TeacherCourseManagementScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToCourseContent = { courseId ->
                        navController.navigate("${TeacherRoutes.CourseContent.route}/$courseId")
                    }
                )
            }
            
            composable("${TeacherRoutes.CourseContent.route}/{courseId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                CourseContentScreen(
                    courseId = courseId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            // 学生端路由
            composable(StudentRoutes.Courses.route) {
                StudentCoursesScreen(
                    onNavigateToReader = { courseId ->
                        navController.navigate("${StudentRoutes.Reader.route}/$courseId")
                    },
                    onNavigateToChapterReader = { courseId, chapterId ->
                        navController.navigate("${StudentRoutes.ChapterReader.route}/$courseId/$chapterId")
                    }
                )
            }
            
            composable("${StudentRoutes.Reader.route}/{courseId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                ReaderScreen(
                    courseId = courseId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToChapter = { chapterId ->
                        navController.navigate("${StudentRoutes.ChapterReader.route}/$courseId/$chapterId")
                    }
                )
            }
            
            composable("${StudentRoutes.ChapterReader.route}/{courseId}/{chapterId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                ChapterReaderScreen(
                    courseId = courseId,
                    chapterId = chapterId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * 导航项数据类
 */
data class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

/**
 * 教师端导航项
 */
val teacherNavigationItems = listOf(
    NavigationItem(
        route = TeacherRoutes.Dashboard.route,
        title = "仪表盘",
        icon = Icons.Default.Dashboard
    ),
    NavigationItem(
        route = TeacherRoutes.CourseManagement.route,
        title = "课程管理",
        icon = Icons.Default.School
    )
)

/**
 * 学生端导航项
 */
val studentNavigationItems = listOf(
    NavigationItem(
        route = StudentRoutes.Courses.route,
        title = "我的课程",
        icon = Icons.Default.MenuBook
    )
)