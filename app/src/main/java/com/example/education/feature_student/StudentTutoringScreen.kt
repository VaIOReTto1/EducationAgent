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

data class TutoringSession(
    val id: String,
    val title: String,
    val subject: String,
    val duration: String,
    val difficulty: String,
    val status: String,
    val progress: Float
)

data class AITutor(
    val id: String,
    val name: String,
    val subject: String,
    val specialty: String,
    val rating: Float,
    val isAvailable: Boolean,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class LearningTip(
    val id: String,
    val title: String,
    val content: String,
    val category: String
)

/**
 * 学生AI辅导页面 - 丰富的UI展示
 * 参考AI学习助手的设计风格
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTutoringScreen(
    navController: NavController
) {
    // 模拟数据
    val aiTutors = remember {
        listOf(
            AITutor("math_tutor", "数学导师", "数学", "微积分专家", 4.9f, true, Icons.Default.Calculate),
            AITutor("physics_tutor", "物理导师", "物理", "力学专家", 4.8f, true, Icons.Default.Science),
            AITutor("english_tutor", "英语导师", "英语", "写作专家", 4.7f, false, Icons.Default.Language),
            AITutor("programming_tutor", "编程导师", "编程", "Python专家", 4.9f, true, Icons.Default.Code)
        )
    }
    
    val recentSessions = remember {
        listOf(
            TutoringSession("session1", "微积分极限问题", "数学", "45分钟", "中等", "已完成", 1.0f),
            TutoringSession("session2", "牛顿运动定律", "物理", "30分钟", "基础", "进行中", 0.6f),
            TutoringSession("session3", "英语语法练习", "英语", "25分钟", "基础", "计划中", 0.0f)
        )
    }
    
    val quickQuestions = remember {
        listOf(
            QuickQuestion("q1", "如何求函数的导数？", "数学"),
            QuickQuestion("q2", "什么是牛顿第二定律？", "物理"),
            QuickQuestion("q3", "Python中如何定义函数？", "编程"),
            QuickQuestion("q4", "英语中现在完成时的用法？", "英语")
        )
    }
    
    val learningTips = remember {
        listOf(
            LearningTip("tip1", "高效记忆法", "使用间隔重复和联想记忆法可以提高学习效率", "学习方法"),
            LearningTip("tip2", "解题技巧", "遇到难题时，先分析题目结构，再寻找解题思路", "解题策略"),
            LearningTip("tip3", "时间管理", "使用番茄工作法，25分钟专注学习，5分钟休息", "效率提升")
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI智能辅导",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "个性化学习指导",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 历史记录 */ }) {
                        Icon(Icons.Default.History, contentDescription = "历史记录")
                    }
                    IconButton(onClick = { /* 设置 */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(NavigationRoute.TUTORING_CHAT) },
                icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                text = { Text("开始对话") }
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
            // AI辅导统计
            item {
                TutoringStatsCard()
            }
            
            // 快速提问
            item {
                QuickQuestionCard(
                    questions = quickQuestions,
                    onQuestionClick = { question ->
                        navController.navigate("${NavigationRoute.TUTORING_CHAT}?question=${question.question}")
                    }
                )
            }
            
            // AI导师团队
            item {
                Text(
                    text = "AI导师团队",
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
                    items(aiTutors) { tutor ->
                        AITutorCard(
                            tutor = tutor,
                            onClick = {
                                navController.navigate("${NavigationRoute.TUTORING_CHAT}?tutor=${tutor.id}")
                            }
                        )
                    }
                }
            }
            
            // 最近辅导记录
            item {
                RecentTutoringCard(
                    sessions = recentSessions,
                    onViewAll = { /* 查看全部 */ },
                    onContinueSession = { session ->
                        navController.navigate("${NavigationRoute.TUTORING_CHAT}?session=${session.id}")
                    }
                )
            }
            
            // 学习小贴士
            item {
                LearningTipsCard(
                    tips = learningTips,
                    onViewAll = { /* 查看全部 */ }
                )
            }
            
            // 智能推荐
            item {
                SmartRecommendationCard(
                    onAcceptRecommendation = {
                        navController.navigate(NavigationRoute.TUTORING_CHAT)
                    }
                )
            }
        }
    }
}

@Composable
private fun TutoringStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
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
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "辅导统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsItem("24", "辅导次数", Icons.Default.School)
                StatsItem("18", "解决问题", Icons.Default.CheckCircle)
                StatsItem("4.8", "满意度", Icons.Default.Star)
                StatsItem("12h", "总时长", Icons.Default.AccessTime)
            }
        }
    }
}

@Composable
private fun StatsItem(
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
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
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
private fun QuickQuestionCard(
    questions: List<QuickQuestion>,
    onQuestionClick: (QuickQuestion) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "快速提问",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(questions) { question ->
                    QuestionChip(
                        question = question,
                        onClick = { onQuestionClick(question) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionChip(
    question: QuickQuestion,
    onClick: () -> Unit
) {
    SuggestionChip(
        onClick = onClick,
        label = {
            Column {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
                Text(
                    text = question.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = Modifier.width(200.dp)
    )
}

@Composable
private fun AITutorCard(
    tutor: AITutor,
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
                    imageVector = tutor.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (tutor.isAvailable) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            shape = CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tutor.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = tutor.specialty,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tutor.rating.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = if (tutor.isAvailable) "在线" else "离线",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (tutor.isAvailable) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
    }
}

@Composable
private fun RecentTutoringCard(
    sessions: List<TutoringSession>,
    onViewAll: () -> Unit,
    onContinueSession: (TutoringSession) -> Unit
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
                    text = "最近辅导",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("查看全部")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            sessions.take(3).forEach { session ->
                TutoringSessionItem(
                    session = session,
                    onContinue = { onContinueSession(session) }
                )
                if (session != sessions.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TutoringSessionItem(
    session: TutoringSession,
    onContinue: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${session.subject} · ${session.difficulty} · ${session.duration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (session.status == "进行中") {
                TextButton(onClick = onContinue) {
                    Text("继续")
                }
            } else {
                Text(
                    text = session.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (session.status) {
                        "已完成" -> MaterialTheme.colorScheme.primary
                        "进行中" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
        
        if (session.progress > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = session.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LearningTipsCard(
    tips: List<LearningTip>,
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
                    text = "学习小贴士",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("查看全部")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tips) { tip ->
                    TipCard(tip)
                }
            }
        }
    }
}

@Composable
private fun TipCard(tip: LearningTip) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = tip.category,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.height(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = tip.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun SmartRecommendationCard(
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
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "智能推荐",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "基于你的学习进度和薄弱环节，AI推荐你重点学习《微积分极限理论》，数学导师已为你准备了专项辅导方案。",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* 稍后 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("稍后")
                }
                
                Button(
                    onClick = onAcceptRecommendation,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("开始辅导")
                }
            }
        }
    }
}