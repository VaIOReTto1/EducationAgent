package com.example.education.feature_assessment

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
import androidx.hilt.navigation.compose.hiltViewModel

data class AssessmentType(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val difficulty: String,
    val estimatedTime: String
)

data class AssessmentCourse(
    val id: String,
    val name: String,
    val studentsCount: Int,
    val lastAssessment: String
)

/**
 * 智能评估页面
 * 集成到教师仪表盘，支持课程选择和多种评估类型
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    onStartAssessment: (String, String) -> Unit, // courseId, assessmentType
    viewModel: AssessmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 预设评估类型
    val assessmentTypes = remember {
        listOf(
            AssessmentType("basic_quiz", "基础选择题", "单选/多选题自动生成", Icons.Default.Quiz, "简单", "15分钟"),
            AssessmentType("advanced_quiz", "综合选择题", "多层次理解测试", Icons.Default.QuestionAnswer, "中等", "30分钟"),
            AssessmentType("essay_questions", "问答题评估", "深度理解能力测试", Icons.Default.Edit, "中等", "45分钟"),
            AssessmentType("coding_challenge", "编程挑战", "算法与编程实战", Icons.Default.Code, "困难", "60分钟"),
            AssessmentType("project_assessment", "项目评估", "综合项目能力测试", Icons.Default.Assignment, "困难", "120分钟"),
            AssessmentType("oral_assessment", "口语评估", "表达与沟通能力", Icons.Default.RecordVoiceOver, "中等", "20分钟"),
            AssessmentType("peer_review", "同行评议", "协作与评价能力", Icons.Default.Groups, "中等", "40分钟"),
            AssessmentType("adaptive_test", "自适应测试", "AI个性化难度调整", Icons.Default.AutoAwesome, "智能", "不定时")
        )
    }
    
    // 预设课程数据
    val courses = remember {
        listOf(
            AssessmentCourse("linux", "Linux系统", 45, "3天前"),
            AssessmentCourse("ml", "机器学习", 38, "1周前"),
            AssessmentCourse("ds", "数据结构", 52, "2天前"),
            AssessmentCourse("db", "数据库系统", 41, "5天前"),
            AssessmentCourse("network", "计算机网络", 35, "1周前"),
            AssessmentCourse("java", "Java编程", 48, "昨天")
        )
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部应用栏
        TopAppBar(
            title = { 
                Column {
                    Text("智能评估")
                    Text(
                        text = "AI驱动的个性化评估系统",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* 历史记录 */ }) {
                    Icon(Icons.Default.History, contentDescription = "评估历史")
                }
                IconButton(onClick = { viewModel.refreshData() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "刷新")
                }
            }
        )
        
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 统计概览
            item {
                AssessmentOverviewCard(courses)
            }
            
            // 课程选择
            item {
                CourseSelectionSection(
                    courses = courses,
                    onCourseSelected = { courseId ->
                        // 可以在这里添加课程选择逻辑
                    }
                )
            }
            
            // 评估类型
            item {
                Text(
                    text = "评估类型",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(600.dp), // 固定高度防止嵌套滚动问题
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(assessmentTypes) { assessmentType ->
                        AssessmentTypeCard(
                            assessmentType = assessmentType,
                            onClick = { onStartAssessment("selected_course", assessmentType.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssessmentOverviewCard(courses: List<AssessmentCourse>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OverviewItem(
                value = "${courses.size}",
                label = "活跃课程",
                icon = Icons.Default.School
            )
            OverviewItem(
                value = "${courses.sumOf { it.studentsCount }}",
                label = "学生总数",
                icon = Icons.Default.People
            )
            OverviewItem(
                value = "95%",
                label = "AI准确率",
                icon = Icons.Default.TrendingUp
            )
        }
    }
}

@Composable
private fun OverviewItem(
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
            style = MaterialTheme.typography.titleLarge,
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
private fun CourseSelectionSection(
    courses: List<AssessmentCourse>,
    onCourseSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "选择课程",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(courses) { course ->
                    CourseChip(
                        course = course,
                        onClick = { onCourseSelected(course.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseChip(
    course: AssessmentCourse,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Column {
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${course.studentsCount}人 • ${course.lastAssessment}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        selected = false
    )
}

@Composable
private fun AssessmentTypeCard(
    assessmentType: AssessmentType,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = assessmentType.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = assessmentType.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = assessmentType.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = assessmentType.difficulty,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.height(24.dp)
                )
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = assessmentType.estimatedTime,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}