package com.example.education.feature_teacher.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.education.core.ui.components.*

/**
 * 教师仪表盘主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToStudentAnalysis: (String) -> Unit,
    onNavigateToAssessment: (String) -> Unit,
    viewModel: TeacherDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 处理错误显示
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // 这里可以显示Snackbar或其他错误提示
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部标题栏
        DashboardTopBar(
            onCreateCourse = {
                viewModel.handleIntent(TeacherDashboardIntent.ShowCreateCourseDialog(true))
            },
            onRefresh = {
                viewModel.handleIntent(TeacherDashboardIntent.RefreshDashboard)
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (uiState.isLoading) {
            // 加载状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 课程概览卡片
                item {
                    CoursesOverviewCard(
                        courses = uiState.courses,
                        selectedCourseId = uiState.selectedCourseId,
                        onCourseSelected = { courseId ->
                            viewModel.handleIntent(TeacherDashboardIntent.SelectCourse(courseId))
                        },
                        onNavigateToCourse = onNavigateToCourse
                    )
                }
                
                // 当前课程统计
                uiState.courseStatistics?.let { statistics ->
                    item {
                        CourseStatisticsCard(
                            statistics = statistics,
                            isLoading = uiState.isLoadingCourseData,
                            onShowAnalytics = {
                                viewModel.handleIntent(TeacherDashboardIntent.ShowCourseAnalytics(true))
                            }
                        )
                    }
                }
                
                // 学生进度概览
                if (uiState.studentProgressList.isNotEmpty()) {
                    item {
                        StudentProgressCard(
                            progressList = uiState.studentProgressList,
                            onNavigateToStudentAnalysis = onNavigateToStudentAnalysis
                        )
                    }
                }
                
                // 待批改评估
                if (uiState.pendingAssessments.isNotEmpty()) {
                    item {
                        PendingAssessmentsCard(
                            assessments = uiState.pendingAssessments,
                            onNavigateToAssessment = onNavigateToAssessment
                        )
                    }
                }
                
                // 最近学生提问
                if (uiState.recentQuestions.isNotEmpty()) {
                    item {
                        RecentQuestionsCard(
                            questions = uiState.recentQuestions
                        )
                    }
                }
            }
        }
    }
    
    // 创建课程对话框
    if (uiState.showCreateCourseDialog) {
        CreateCourseDialog(
            isLoading = uiState.isCreatingCourse,
            onDismiss = {
                viewModel.handleIntent(TeacherDashboardIntent.ShowCreateCourseDialog(false))
            },
            onCreateCourse = { title, description, category, difficulty ->
                viewModel.handleIntent(
                    TeacherDashboardIntent.CreateCourse(title, description, category, difficulty)
                )
            }
        )
    }
    
    // 课程分析对话框
    if (uiState.showCourseAnalytics) {
        uiState.courseAnalytics?.let { analytics ->
            CourseAnalyticsDialog(
                analytics = analytics,
                onDismiss = {
                    viewModel.handleIntent(TeacherDashboardIntent.ShowCourseAnalytics(false))
                }
            )
        }
    }
}

/**
 * 仪表盘顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    onCreateCourse: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "教师仪表盘",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Row {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "刷新"
                )
            }
            
            FilledTonalButton(
                onClick = onCreateCourse,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("创建课程")
            }
        }
    }
}

/**
 * 课程概览卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoursesOverviewCard(
    courses: List<com.example.education.core.database.entity.CourseEntity>,
    selectedCourseId: String?,
    onCourseSelected: (String) -> Unit,
    onNavigateToCourse: (String) -> Unit
) {
    Card(
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
                Text(
                    text = "我的课程",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = TeacherDashboardUtils.formatChapterCount(courses.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (courses.isEmpty()) {
                EmptyState(
                    message = "还没有创建任何课程",
                    actionText = "创建第一个课程"
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(courses) { course ->
                        CourseCard(
                            course = course,
                            isSelected = course.id == selectedCourseId,
                            onClick = { onCourseSelected(course.id) },
                            onNavigate = { onNavigateToCourse(course.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 课程卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseCard(
    course: com.example.education.core.database.entity.CourseEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryLabel(
                    category = course.category,
                    color = TeacherDashboardUtils.getCategoryColor(course.category)
                )
                
                DifficultyLabel(
                    difficulty = TeacherDashboardUtils.getDifficultyText(course.difficulty),
                    color = TeacherDashboardUtils.getDifficultyColor(course.difficulty)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (course.isPublished) "已发布" else "草稿",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (course.isPublished) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                IconButton(
                    onClick = onNavigate,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "查看课程",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 课程统计卡片
 */
@Composable
private fun CourseStatisticsCard(
    statistics: CourseStatistics,
    isLoading: Boolean,
    onShowAnalytics: () -> Unit
) {
    Card(
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
                Text(
                    text = "课程统计",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(onClick = onShowAnalytics) {
                    Text("详细分析")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                LoadingIndicator()
            } else {
                statistics.course?.let { course ->
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            label = "学生数量",
                            value = statistics.totalStudents.toString(),
                            icon = Icons.Default.Person
                        )
                        
                        StatisticItem(
                            label = "章节数量",
                            value = statistics.totalChapters.toString(),
                            icon = Icons.Default.MenuBook
                        )
                        
                        StatisticItem(
                            label = "平均进度",
                            value = TeacherDashboardUtils.formatProgress(statistics.averageProgress),
                            icon = Icons.Default.TrendingUp
                        )
                        
                        StatisticItem(
                            label = "评估数量",
                            value = statistics.totalAssessments.toString(),
                            icon = Icons.Default.Assignment
                        )
                    }
                }
            }
        }
    }
}

/**
 * 统计项目
 */
@Composable
private fun StatisticItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}