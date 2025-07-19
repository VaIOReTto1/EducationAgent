package com.example.education.feature_student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.education.navigation.NavigationRoute

data class AITutoringSubject(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val difficulty: String,
    val sessionCount: Int,
    val averageRating: Float,
    val isAvailable: Boolean
)

data class TutoringFeature(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color
)

data class LearningProgress(
    val subject: String,
    val completedSessions: Int,
    val totalSessions: Int,
    val masteryLevel: Float,
    val lastActivity: String
)

/**
 * AI辅导页面 - 提供智能化的学习辅导服务
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITutoringScreen(
    navController: NavController
) {
    // 模拟数据
    val tutoringSubjects = remember {
        listOf(
            AITutoringSubject("math", "数学辅导", "个性化数学学习指导", Icons.Default.Calculate, "全难度", 156, 4.8f, true),
            AITutoringSubject("physics", "物理辅导", "物理概念深度解析", Icons.Default.Science, "中高级", 89, 4.7f, true),
            AITutoringSubject("chemistry", "化学辅导", "化学实验与理论结合", Icons.Default.Biotech, "中级", 67, 4.6f, true),
            AITutoringSubject("english", "英语辅导", "英语听说读写全面提升", Icons.Default.Language, "全难度", 234, 4.9f, true),
            AITutoringSubject("programming", "编程辅导", "编程思维与实践指导", Icons.Default.Code, "初中级", 123, 4.8f, true),
            AITutoringSubject("biology", "生物辅导", "生物知识系统学习", Icons.Default.Eco, "中级", 45, 4.5f, false)
        )
    }
    
    val colorScheme = MaterialTheme.colorScheme
    val tutoringFeatures = remember {
        listOf(
            TutoringFeature("instant_help", "即时答疑", "24/7在线解答学习疑问", Icons.Default.Help, colorScheme.primary),
            TutoringFeature("practice_mode", "练习模式", "智能生成练习题目", Icons.Default.Quiz, colorScheme.secondary),
            TutoringFeature("concept_explain", "概念解释", "深入浅出解释复杂概念", Icons.Default.Lightbulb, colorScheme.tertiary),
            TutoringFeature("study_plan", "学习计划", "个性化学习路径规划", Icons.Default.Schedule, colorScheme.error)
        )
    }
    
    val learningProgress = remember {
        listOf(
            LearningProgress("数学", 12, 20, 0.75f, "2小时前"),
            LearningProgress("物理", 8, 15, 0.60f, "昨天"),
            LearningProgress("英语", 15, 18, 0.85f, "3小时前")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 页面标题
        item {
            Column {
                Text(
                    text = "AI智能辅导",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "个性化学习，智能化辅导",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // 快速功能入口
        item {
            Text(
                text = "快速功能",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tutoringFeatures) { feature ->
                    TutoringFeatureCard(
                        feature = feature,
                        onClick = {
                            navController.navigate("${NavigationRoute.TUTORING_DETAIL}/${feature.id}")
                        }
                    )
                }
            }
        }
        
        // 学习进度概览
        item {
            Text(
                text = "学习进度",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(learningProgress) { progress ->
                    LearningProgressCard(progress = progress)
                }
            }
        }
        
        // 学科辅导
        item {
            Text(
                text = "学科辅导",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(tutoringSubjects) { subject ->
            AITutoringSubjectCard(
                subject = subject,
                onClick = {
                    if (subject.isAvailable) {
                        navController.navigate("${NavigationRoute.TUTORING_DETAIL}/${subject.id}")
                    }
                }
            )
        }
        
        // 底部间距
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TutoringFeatureCard(
    feature: TutoringFeature,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = feature.color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LearningProgressCard(
    progress: LearningProgress
) {
    Card(
        modifier = Modifier.width(160.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = progress.subject,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = progress.masteryLevel,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${progress.completedSessions}/${progress.totalSessions}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${(progress.masteryLevel * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = progress.lastActivity,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AITutoringSubjectCard(
    subject: AITutoringSubject,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (subject.isAvailable) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 学科图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = subject.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 学科信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!subject.isAvailable) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "即将开放",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = subject.averageRating.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${subject.sessionCount}次辅导",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = subject.difficulty,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            // 箭头图标
            if (subject.isAvailable) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}