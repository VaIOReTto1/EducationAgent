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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.education.navigation.NavigationRoute

data class LearningModule(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val progress: Float,
    val difficulty: String
)

data class StudySession(
    val id: String,
    val title: String,
    val duration: String,
    val type: String,
    val status: String
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isUnlocked: Boolean
)

/**
 * 学生学习页面 - 丰富的UI展示
 * 参考AI学习助手的设计风格
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentLearningScreen(
    navController: NavController
) {
    // 模拟数据
    val learningModules = remember {
        listOf(
            LearningModule("math_basic", "基础数学", "掌握基本数学概念", Icons.Default.Calculate, 0.85f, "基础"),
            LearningModule("math_advanced", "高等数学", "微积分与线性代数", Icons.Default.Functions, 0.60f, "进阶"),
            LearningModule("physics", "物理学", "力学与电磁学基础", Icons.Default.Science, 0.40f, "中等"),
            LearningModule("programming", "编程基础", "Python编程入门", Icons.Default.Code, 0.75f, "基础"),
            LearningModule("english", "英语学习", "学术英语写作", Icons.Default.Language, 0.55f, "中等"),
            LearningModule("chemistry", "化学", "有机化学基础", Icons.Default.Biotech, 0.30f, "进阶")
        )
    }
    
    val recentSessions = remember {
        listOf(
            StudySession("session1", "微积分练习", "45分钟", "练习", "已完成"),
            StudySession("session2", "物理实验", "1小时30分钟", "实验", "进行中"),
            StudySession("session3", "英语阅读", "30分钟", "阅读", "计划中")
        )
    }
    
    val achievements = remember {
        listOf(
            Achievement("first_week", "学习新手", "完成第一周学习", Icons.Default.EmojiEvents, true),
            Achievement("math_master", "数学达人", "数学模块达到80%", Icons.Default.School, true),
            Achievement("streak_7", "坚持不懈", "连续学习7天", Icons.Default.Whatshot, false),
            Achievement("perfect_score", "完美表现", "获得满分成绩", Icons.Default.Star, false)
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "我的学习中心",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "个性化学习体验",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 搜索 */ }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = { /* 通知 */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "通知")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 学习统计概览
            item {
                LearningOverviewCard()
            }
            
            // 今日学习计划
            item {
                TodayPlanCard(
                    onStartLearning = { navController.navigate(NavigationRoute.STUDENT_CHAT) }
                )
            }
            
            // 学习模块
            item {
                Text(
                    text = "学习模块",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(600.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(learningModules) { module ->
                        LearningModuleCard(
                            module = module,
                            onClick = {
                                navController.navigate("${NavigationRoute.STUDENT_READER}/${module.id}")
                            }
                        )
                    }
                }
            }
            
            // 最近学习记录
            item {
                RecentSessionsCard(
                    sessions = recentSessions,
                    onViewAll = { /* 查看全部 */ }
                )
            }
            
            // 成就系统
            item {
                AchievementsCard(
                    achievements = achievements,
                    onViewAll = { /* 查看全部成就 */ }
                )
            }
            
            // AI学习建议
            item {
                AIRecommendationCard(
                    onAcceptRecommendation = { navController.navigate(NavigationRoute.TUTORING_CHAT) }
                )
            }
        }
    }
}

@Composable
private fun LearningOverviewCard() {
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
                text = "学习概览",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewItem("15", "学习天数", Icons.Default.CalendarToday)
                OverviewItem("6", "完成模块", Icons.Default.CheckCircle)
                OverviewItem("89", "练习题", Icons.Default.Assignment)
                OverviewItem("85%", "平均分", Icons.Default.TrendingUp)
            }
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
private fun TodayPlanCard(
    onStartLearning: () -> Unit
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
                    imageVector = Icons.Default.Today,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "今日学习计划",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "高等数学 - 极限理论",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "预计用时: 45分钟",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = onStartLearning,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("开始学习")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = 0.3f,
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "今日进度: 30%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun LearningModuleCard(
    module: LearningModule,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = module.difficulty,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.height(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = module.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(module.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                LinearProgressIndicator(
                    progress = module.progress,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RecentSessionsCard(
    sessions: List<StudySession>,
    onViewAll: () -> Unit
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
                    text = "最近学习",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("查看全部")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            sessions.take(3).forEach { session ->
                SessionItem(session)
                if (session != sessions.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SessionItem(session: StudySession) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val statusColor = when (session.status) {
            "已完成" -> MaterialTheme.colorScheme.primary
            "进行中" -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.outline
        }
        
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = statusColor,
                    shape = CircleShape
                )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${session.type} · ${session.duration}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = session.status,
            style = MaterialTheme.typography.bodySmall,
            color = statusColor
        )
    }
}

@Composable
private fun AchievementsCard(
    achievements: List<Achievement>,
    onViewAll: () -> Unit
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
                    text = "成就徽章",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("查看全部")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementItem(achievement)
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(achievement: Achievement) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (achievement.isUnlocked) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = achievement.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (achievement.isUnlocked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = achievement.title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = if (achievement.isUnlocked) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun AIRecommendationCard(
    onAcceptRecommendation: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI学习建议",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "基于你的学习数据分析，建议加强物理学模块的练习，特别是力学部分。AI导师可以为你提供个性化的学习路径。",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* 稍后提醒 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("稍后提醒")
                }
                
                Button(
                    onClick = onAcceptRecommendation,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("开始学习")
                }
            }
        }
    }
}