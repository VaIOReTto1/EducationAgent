package com.example.education.feature_student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.education.navigation.NavigationRoute
import com.example.education.feature_ai_learning.AILearningScreen
import com.example.education.feature_student.StudentTutoringScreen
import com.example.education.feature_student.AITutoringScreen
import com.example.education.feature_student.StudentChatScreen
import com.example.education.core.user.UserRole

data class CourseProgress(
    val courseId: String,
    val courseName: String,
    val progress: Float,
    val totalChapters: Int,
    val completedChapters: Int,
    val lastStudyTime: String
)

data class StudyStats(
    val studyDays: Int,
    val completedExercises: Int,
    val masteryRate: Float,
    val weeklyGoal: Int,
    val weeklyProgress: Int
)

/**
 * 学生端主布局 - 包含底部导航栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentMainLayout(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val selectedTab = when (currentRoute) {
        NavigationRoute.STUDENT_MAIN -> 0
        NavigationRoute.STUDENT_CHAT -> 1
        NavigationRoute.TUTORING_CHAT -> 2
        NavigationRoute.AI_LEARNING -> 3
        else -> 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "我的学习",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "继续你的学习之旅",
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
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text("课程情况") },
                    selected = selectedTab == 0,
                    onClick = {
                        if (currentRoute != NavigationRoute.STUDENT_MAIN) {
                            navController.navigate(NavigationRoute.STUDENT_MAIN) {
                                popUpTo(NavigationRoute.STUDENT_MAIN) { inclusive = true }
                            }
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    label = { Text("学习助手") },
                    selected = selectedTab == 1,
                    onClick = {
                        if (currentRoute != NavigationRoute.STUDENT_CHAT) {
                            navController.navigate(NavigationRoute.STUDENT_CHAT) {
                                popUpTo(NavigationRoute.STUDENT_MAIN)
                            }
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Help, contentDescription = null) },
                    label = { Text("AI辅导") },
                    selected = selectedTab == 2,
                    onClick = {
                        if (currentRoute != NavigationRoute.TUTORING_CHAT) {
                            navController.navigate(NavigationRoute.TUTORING_CHAT) {
                                popUpTo(NavigationRoute.STUDENT_MAIN)
                            }
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Psychology, contentDescription = null) },
                    label = { Text("AI学习") },
                    selected = selectedTab == 3,
                    onClick = {
                        if (currentRoute != NavigationRoute.AI_LEARNING) {
                            navController.navigate(NavigationRoute.AI_LEARNING) {
                                popUpTo(NavigationRoute.STUDENT_MAIN)
                            }
                        }
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
            when (currentRoute) {
                NavigationRoute.STUDENT_MAIN -> {
                    StudentMainScreen(navController)
                }
                NavigationRoute.STUDENT_CHAT -> {
                    StudentChatScreen(navController = navController)
                }
                NavigationRoute.TUTORING_CHAT -> {
                     // AI辅导页面 - 使用专门的AI辅导页面
                     AITutoringScreen(navController = navController)
                 }
                NavigationRoute.AI_LEARNING -> {
                    AILearningScreen(
                        userRole = UserRole.STUDENT,
                        onStartChat = { assistantType ->
                            navController.navigate("${NavigationRoute.STUDENT_AI_CHAT}/$assistantType")
                        },
                        onNavigateToFeature = { featureId ->
                            when (featureId) {
                                "knowledge_base" -> navController.navigate(NavigationRoute.KNOWLEDGE_BASE)
                                "assessment" -> navController.navigate(NavigationRoute.ASSESSMENT)
                                else -> {
                                    navController.navigate("${NavigationRoute.STUDENT_AI_CHAT}/$featureId")
                                }
                            }
                        }
                    )
                }
                else -> {
                    // 默认显示主页面
                    StudentMainScreen(navController)
                }
            }
        }
    }
}

/**
 * 学生主页面
 * 展示学习进度、课程情况和快速操作
 */
@Composable
fun StudentMainScreen(
    navController: NavController
) {
    // 模拟数据
    val studyStats = remember {
        StudyStats(
            studyDays = 15,
            completedExercises = 89,
            masteryRate = 0.85f,
            weeklyGoal = 5,
            weeklyProgress = 3
        )
    }

    val courseProgress = remember {
        listOf(
            CourseProgress("math001", "高等数学", 0.75f, 12, 9, "2小时前"),
            CourseProgress("physics001", "大学物理", 0.60f, 10, 6, "昨天"),
            CourseProgress("cs001", "计算机基础", 0.90f, 8, 7, "3小时前"),
            CourseProgress("english001", "大学英语", 0.45f, 15, 7, "2天前")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 学习统计卡片
        item {
            StudyStatsCard(studyStats)
        }

        // 今日学习建议
        item {
            TodayRecommendationCard(
                onStartStudy = { navController.navigate(NavigationRoute.STUDENT_CHAT) }
            )
        }

        // 课程进度
        item {
            Text(
                text = "我的课程",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(courseProgress) { course ->
            CourseProgressCard(
                course = course,
                onClick = {
                    navController.navigate("${NavigationRoute.STUDENT_READER}/${course.courseId}")
                }
            )
        }

        // 快速操作
        item {
            QuickActionsCard(
                onQuickAction = { action ->
                    when (action) {
                        "quiz" -> navController.navigate(NavigationRoute.STUDENT_QUIZ)
                        "tutoring" -> navController.navigate(NavigationRoute.TUTORING)
                        "ai_learning" -> navController.navigate(NavigationRoute.AI_LEARNING)
                        else -> navController.navigate(NavigationRoute.STUDENT_CHAT)
                    }
                }
            )
        }
    }
}

@Composable
private fun StudyStatsCard(stats: StudyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "学习统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "${stats.studyDays}",
                    label = "学习天数",
                    icon = Icons.Default.CalendarMonth
                )
                StatItem(
                    value = "${stats.completedExercises}",
                    label = "完成练习",
                    icon = Icons.Default.Assignment
                )
                StatItem(
                    value = "${(stats.masteryRate * 100).toInt()}%",
                    label = "掌握度",
                    icon = Icons.Default.TrendingUp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 本周目标进度
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "本周目标",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stats.weeklyProgress}/${stats.weeklyGoal}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            LinearProgressIndicator(
                progress = stats.weeklyProgress.toFloat() / stats.weeklyGoal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TodayRecommendationCard(
    onStartStudy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "今日学习建议",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "根据你的学习进度，建议今天复习高等数学的极限与连续性，并完成3道相关练习题。",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Button(
                onClick = onStartStudy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("开始学习")
            }
        }
    }
}

@Composable
private fun CourseProgressCard(
    course: CourseProgress,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.courseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${course.completedChapters}/${course.totalChapters} 章节",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${(course.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = course.lastStudyTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = course.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuickActionsCard(
    onQuickAction: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快速操作",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    QuickActionItem(
                        icon = Icons.Default.Quiz,
                        title = "练习测试",
                        onClick = { onQuickAction("quiz") }
                    )
                }
                item {
                    QuickActionItem(
                        icon = Icons.Default.Help,
                        title = "AI辅导",
                        onClick = { onQuickAction("tutoring") }
                    )
                }
                item {
                    QuickActionItem(
                        icon = Icons.Default.Psychology,
                        title = "AI学习",
                        onClick = { onQuickAction("ai_learning") }
                    )
                }
                item {
                    QuickActionItem(
                        icon = Icons.Default.Chat,
                        title = "学习助手",
                        onClick = { onQuickAction("chat") }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}