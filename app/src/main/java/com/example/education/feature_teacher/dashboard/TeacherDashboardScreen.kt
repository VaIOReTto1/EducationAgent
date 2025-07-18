package com.example.education.feature_teacher.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

data class QuickAssessment(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val difficulty: String
)

/**
 * 教师端仪表盘页面
 * 
 * 显示教学效率指数、学生学习效果等关键指标，集成智能评估功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    onNavigateToAssessment: ((String, String) -> Unit)? = null, // courseId, assessmentType
    viewModel: TeacherDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 预设快速评估类型
    val quickAssessments = remember {
        listOf(
            QuickAssessment("quiz", "选择题测试", "自动生成单选/多选题", Icons.Default.Quiz, "简单"),
            QuickAssessment("essay", "问答题评估", "深度理解能力测试", Icons.Default.Edit, "中等"),
            QuickAssessment("coding", "编程能力", "算法与编程实战", Icons.Default.Code, "困难"),
            QuickAssessment("comprehensive", "综合评估", "多维度能力测试", Icons.Default.Assessment, "困难")
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 页面标题
        Text(
            text = "教学仪表盘",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        // 教学效率指数卡片
        TeachingEfficiencyCard(uiState.teachingEfficiency)
        
        // 智能评估快速入口
        IntelligentAssessmentSection(
            assessments = quickAssessments,
            onAssessmentClick = { assessmentType ->
                onNavigateToAssessment?.invoke("default", assessmentType)
            }
        )
        
        // 学生学习效果卡片
        StudentLearningCard(uiState.studentLearning)
        
        // 使用统计卡片
        UsageStatsCard(uiState.usageStats)
        
        // 课程优化建议卡片
        OptimizationSuggestionsCard(uiState.suggestions)
        
        // 快速操作区域
        QuickActionsSection(
            onCreateCourse = { viewModel.navigateToCreateCourse() },
            onViewAssignments = { viewModel.navigateToAssignments() },
            onAnalyzeStudents = { viewModel.navigateToStudentAnalysis() }
        )
    }
}

/**
 * 教学效率指数卡片
 */
@Composable
fun TeachingEfficiencyCard(efficiency: TeachingEfficiency) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "教学效率指数",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // 效率分数
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${efficiency.score}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "/100",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 详细指标
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EfficiencyMetric(
                    label = "备课耗时",
                    value = "${efficiency.preparationTime}分钟",
                    change = efficiency.preparationTimeChange
                )
                EfficiencyMetric(
                    label = "批改效率",
                    value = "${efficiency.gradingEfficiency}%",
                    change = efficiency.gradingEfficiencyChange
                )
                EfficiencyMetric(
                    label = "课程优化",
                    value = "${efficiency.optimizationCount}次",
                    change = efficiency.optimizationChange
                )
            }
        }
    }
}

/**
 * 智能评估快速入口
 */
@Composable
fun IntelligentAssessmentSection(
    assessments: List<QuickAssessment>,
    onAssessmentClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "智能评估",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(assessments) { assessment ->
                    QuickAssessmentButton(
                        assessment = assessment,
                        onClick = { onAssessmentClick(assessment.id) }
                    )
                }
            }
        }
    }
}

/**
 * 学生学习效果卡片
 */
@Composable
fun StudentLearningCard(learning: StudentLearning) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "学生学习效果",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            
            // 平均正确率
            LinearProgressIndicator(
                progress = learning.averageAccuracy / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "平均正确率: ${learning.averageAccuracy}%",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // 高频错误知识点
            if (learning.commonMistakes.isNotEmpty()) {
                Text(
                    text = "高频错误知识点:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                learning.commonMistakes.take(3).forEach { mistake ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = mistake,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * 使用统计卡片
 */
@Composable
fun UsageStatsCard(stats: UsageStats) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "使用统计 (本周)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Person,
                    label = "活跃学生",
                    value = "${stats.activeStudents}人"
                )
                StatItem(
                    icon = Icons.Default.Book,
                    label = "课程访问",
                    value = "${stats.courseViews}次"
                )
                StatItem(
                    icon = Icons.Default.Assignment,
                    label = "作业提交",
                    value = "${stats.assignmentSubmissions}份"
                )
            }
        }
    }
}

/**
 * 课程优化建议卡片
 */
@Composable
fun OptimizationSuggestionsCard(suggestions: List<String>) {
    if (suggestions.isEmpty()) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI建议",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            suggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * 快速操作区域
 */
@Composable
fun QuickActionsSection(
    onCreateCourse: () -> Unit,
    onViewAssignments: () -> Unit,
    onAnalyzeStudents: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "快速操作",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "新建课程",
                    onClick = onCreateCourse,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Assignment,
                    label = "查看作业",
                    onClick = onViewAssignments,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Analytics,
                    label = "学生分析",
                    onClick = onAnalyzeStudents,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 智能评估快速入口按钮
 */
@Composable
fun QuickAssessmentButton(
    assessment: QuickAssessment,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = assessment.icon,
                contentDescription = null
            )
            Text(
                text = assessment.title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = assessment.description,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 效率指标组件
 */
@Composable
fun EfficiencyMetric(
    label: String,
    value: String,
    change: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (change != 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (change > 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (change > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = "${if (change > 0) "+" else ""}$change%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (change > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * 统计项组件
 */
@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 快速操作按钮
 */
@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} 