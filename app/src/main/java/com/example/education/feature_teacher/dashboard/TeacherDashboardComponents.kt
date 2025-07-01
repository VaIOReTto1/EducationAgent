package com.example.education.feature_teacher.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.education.core.ui.components.*

/**
 * 学生进度卡片
 */
@Composable
fun StudentProgressCard(
    progressList: List<StudentProgress>,
    onNavigateToStudentAnalysis: (String) -> Unit
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
                    text = "学生进度",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(
                    onClick = { /* 查看全部学生 */ }
                ) {
                    Text("查看全部")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            progressList.take(5).forEach { progress ->
                StudentProgressItem(
                    progress = progress,
                    onClick = { onNavigateToStudentAnalysis(progress.student.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * 学生进度项目
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentProgressItem(
    progress: StudentProgress,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                name = progress.student.name,
                size = 40.dp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = progress.student.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = TeacherDashboardUtils.generateStudentSummary(
                        progress.completedChapters,
                        progress.totalTimeSpent,
                        progress.lastActiveTime
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = TeacherDashboardUtils.formatProgress(progress.averageProgress),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TeacherDashboardUtils.getProgressColor(progress.averageProgress)
                )
                
                Text(
                    text = TeacherDashboardUtils.formatRelativeTime(progress.lastActiveTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 待批改评估卡片
 */
@Composable
fun PendingAssessmentsCard(
    assessments: List<PendingAssessment>,
    onNavigateToAssessment: (String) -> Unit
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
                    text = "待批改评估",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Badge {
                    Text(assessments.size.toString())
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            assessments.take(3).forEach { assessment ->
                PendingAssessmentItem(
                    assessment = assessment,
                    onClick = { onNavigateToAssessment(assessment.assessmentResult.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (assessments.size > 3) {
                TextButton(
                    onClick = { /* 查看全部待批改 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("查看全部 ${assessments.size} 个待批改评估")
                }
            }
        }
    }
}

/**
 * 待批改评估项目
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PendingAssessmentItem(
    assessment: PendingAssessment,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = assessment.assessment.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                assessment.student?.let { student ->
                    Text(
                        text = "学生：${student.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "提交时间：${TeacherDashboardUtils.formatRelativeTime(assessment.assessmentResult.submittedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "批改",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

/**
 * 最近提问卡片
 */
@Composable
fun RecentQuestionsCard(
    questions: List<StudentQuestion>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "最近学生提问",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            questions.take(3).forEach { question ->
                RecentQuestionItem(question = question)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * 最近提问项目
 */
@Composable
private fun RecentQuestionItem(
    question: StudentQuestion
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                question.student?.let { student ->
                    UserAvatar(
                        name = student.name,
                        size = 32.dp
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = TeacherDashboardUtils.formatRelativeTime(question.message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = question.message.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}

/**
 * 创建课程对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onCreateCourse: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedDifficulty by remember { mutableStateOf(false) }
    
    val categories = listOf("数学", "科学", "语言", "历史", "艺术", "技术")
    val difficulties = listOf("初级", "中级", "高级", "专家")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "创建新课程",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("课程标题") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("课程描述") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 课程类别下拉菜单
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("课程类别") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 课程难度下拉菜单
                ExposedDropdownMenuBox(
                    expanded = expandedDifficulty,
                    onExpandedChange = { expandedDifficulty = !expandedDifficulty }
                ) {
                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("课程难度") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDifficulty)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedDifficulty,
                        onDismissRequest = { expandedDifficulty = false }
                    ) {
                        difficulties.forEach { diff ->
                            DropdownMenuItem(
                                text = { Text(diff) },
                                onClick = {
                                    difficulty = diff
                                    expandedDifficulty = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("取消")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            onCreateCourse(title, description, category, difficulty)
                        },
                        enabled = !isLoading && title.isNotBlank() && description.isNotBlank() && 
                                category.isNotBlank() && difficulty.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("创建")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 课程分析对话框
 */
@Composable
fun CourseAnalyticsDialog(
    analytics: CourseAnalytics,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "课程分析",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AnalyticsItem(
                            label = "总浏览量",
                            value = analytics.totalViews.toString(),
                            icon = Icons.Default.Visibility
                        )
                    }
                    
                    item {
                        AnalyticsItem(
                            label = "独立学生数",
                            value = analytics.uniqueStudents.toString(),
                            icon = Icons.Default.Person
                        )
                    }
                    
                    item {
                        AnalyticsItem(
                            label = "平均学习时间",
                            value = TeacherDashboardUtils.formatLearningTime(analytics.averageTimeSpent.toLong()),
                            icon = Icons.Default.Schedule
                        )
                    }
                    
                    item {
                        AnalyticsItem(
                            label = "完成率",
                            value = TeacherDashboardUtils.formatProgress(analytics.completionRate * 100),
                            icon = Icons.Default.CheckCircle
                        )
                    }
                    
                    item {
                        AnalyticsItem(
                            label = "平均分数",
                            value = "${analytics.averageScore.toInt()}分",
                            icon = Icons.Default.Grade
                        )
                    }
                    
                    item {
                        AnalyticsItem(
                            label = "提问数量",
                            value = analytics.totalQuestions.toString(),
                            icon = Icons.Default.QuestionAnswer
                        )
                    }
                    
                    item {
                        AnalyticsItem(
                            label = "参与度",
                            value = TeacherDashboardUtils.getEngagementLevel(analytics.engagementScore),
                            icon = Icons.Default.TrendingUp,
                            valueColor = TeacherDashboardUtils.getEngagementColor(analytics.engagementScore)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 分析项目
 */
@Composable
private fun AnalyticsItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}