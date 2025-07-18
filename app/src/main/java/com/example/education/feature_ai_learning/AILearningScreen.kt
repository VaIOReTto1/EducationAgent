package com.example.education.feature_ai_learning

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
import com.example.education.core.user.UserRole

data class TeachingTool(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val category: String
)

data class LearningFeature(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val type: String
)

/**
 * AI学习助手页面
 * 为教师和学生提供不同的AI辅助功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AILearningScreen(
    userRole: UserRole,
    onStartChat: (String) -> Unit, // assistantType
    onNavigateToFeature: (String) -> Unit, // featureId
    viewModel: AILearningViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(userRole) {
        viewModel.loadDataForRole(userRole)
    }
    
    // 教师端备课工具
    val teachingTools = remember {
        listOf(
            TeachingTool("lesson_plan", "智能备课", "自动生成教学方案和课件", Icons.Default.School, "备课"),
            TeachingTool("question_bank", "题库生成", "AI生成各类型题目", Icons.Default.Quiz, "评估"),
            TeachingTool("content_analysis", "内容分析", "课程内容智能分析", Icons.Default.Analytics, "分析"),
            TeachingTool("teaching_strategy", "教学策略", "个性化教学建议", Icons.Default.Psychology, "指导"),
            TeachingTool("student_progress", "学情分析", "学生学习情况追踪", Icons.Default.TrendingUp, "监控"),
            TeachingTool("resource_recommend", "资源推荐", "智能推荐教学资源", Icons.Default.Recommend, "资源")
        )
    }
    
    // 学生端学习功能
    val learningFeatures = remember {
        listOf(
            LearningFeature("ai_tutor", "AI导师", "24/7个人学习助手", Icons.Default.Person, "辅导"),
            LearningFeature("practice_mode", "练习模式", "智能推荐练习题", Icons.Default.FitnessCenter, "练习"),
            LearningFeature("concept_explain", "概念解释", "深入浅出解释概念", Icons.Default.Lightbulb, "理解"),
            LearningFeature("study_plan", "学习计划", "个性化学习路径", Icons.Default.Route, "规划"),
            LearningFeature("peer_discuss", "同学讨论", "AI辅助小组讨论", Icons.Default.Groups, "协作"),
            LearningFeature("progress_track", "进度跟踪", "学习进度可视化", Icons.Default.Timeline, "监控")
        )
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部应用栏
        TopAppBar(
            title = { 
                Column {
                    Text("AI学习助手")
                    Text(
                        text = if (userRole == UserRole.TEACHER) "智能教学辅助" else "个性化学习伙伴",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* 设置 */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "设置")
                }
            }
        )
        
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 角色统计卡片
            item {
                RoleStatsCard(userRole)
            }
            
            if (userRole == UserRole.TEACHER) {
                // 教师端 - 智能备课工具
                item {
                    Text(
                        text = "智能备课工具",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height(500.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(teachingTools) { tool ->
                            TeachingToolCard(
                                tool = tool,
                                onClick = { onStartChat("teacher") }
                            )
                        }
                    }
                }
                
                // 快速操作
                item {
                    QuickActionsCard(
                        onQuickAction = { action ->
                            when (action) {
                                "knowledge_base" -> onNavigateToFeature("knowledge_base")
                                "assessment" -> onNavigateToFeature("assessment")
                                else -> onStartChat("teacher")
                            }
                        }
                    )
                }
                
            } else {
                // 学生端 - 学习功能
                item {
                    Text(
                        text = "个性化学习",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height(400.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(learningFeatures) { feature ->
                            LearningFeatureCard(
                                feature = feature,
                                onClick = { onStartChat("student") }
                            )
                        }
                    }
                }
                
                // 学习建议
                item {
                    StudyRecommendationCard(
                        onStartStudy = { onStartChat("student") }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleStatsCard(userRole: UserRole) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (userRole == UserRole.TEACHER) {
                StatItem("12", "备课次数", Icons.Default.School)
                StatItem("45", "学生人数", Icons.Default.People)
                StatItem("98%", "满意度", Icons.Default.ThumbUp)
            } else {
                StatItem("8", "学习天数", Icons.Default.CalendarMonth)
                StatItem("156", "完成练习", Icons.Default.Assignment)
                StatItem("85%", "掌握度", Icons.Default.TrendingUp)
            }
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
private fun TeachingToolCard(
    tool: TeachingTool,
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
                imageVector = tool.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tool.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = tool.category,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun LearningFeatureCard(
    feature: LearningFeature,
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
                imageVector = feature.icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = feature.type,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.height(24.dp)
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
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onQuickAction("knowledge_base") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LibraryBooks, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("知识库", style = MaterialTheme.typography.bodySmall)
                }
                
                OutlinedButton(
                    onClick = { onQuickAction("assessment") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("评估", style = MaterialTheme.typography.bodySmall)
                }
                
                OutlinedButton(
                    onClick = { onQuickAction("ai_chat") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI对话", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun StudyRecommendationCard(
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
            Text(
                text = "今日学习建议",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "根据你的学习进度，建议今天复习线性代数的特征值和特征向量部分，并完成3道相关练习题。",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Button(
                onClick = onStartStudy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("开始学习")
            }
        }
    }
}