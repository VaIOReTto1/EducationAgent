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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class ChatAssistant(
    val id: String,
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val specialty: String,
    val isOnline: Boolean,
    val rating: Float
)

data class QuickQuestion(
    val id: String,
    val question: String,
    val category: String
)

data class StudyTip(
    val id: String,
    val title: String,
    val content: String,
    val category: String
)

data class ChatHistory(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: String,
    val assistantType: String
)

/**
 * 学生端学习助手页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentChatScreen(
    navController: NavController
) {
    // 模拟数据
    val chatAssistants = remember {
        listOf(
            ChatAssistant("general", "通用学习助手", "全科目学习指导和答疑", Icons.Default.School, "全科目", true, 4.8f),
            ChatAssistant("math", "数学助手", "数学问题解答和学习指导", Icons.Default.Calculate, "数学", true, 4.9f),
            ChatAssistant("english", "英语助手", "英语学习和写作指导", Icons.Default.Language, "英语", true, 4.7f),
            ChatAssistant("science", "科学助手", "物理化学生物学习指导", Icons.Default.Science, "理科", false, 4.6f)
        )
    }
    
    val quickQuestions = remember {
        listOf(
            QuickQuestion("q1", "如何提高学习效率？", "学习方法"),
            QuickQuestion("q2", "数学公式记忆技巧", "数学"),
            QuickQuestion("q3", "英语单词背诵方法", "英语"),
            QuickQuestion("q4", "物理概念理解技巧", "物理")
        )
    }
    
    val studyTips = remember {
        listOf(
            StudyTip("tip1", "番茄工作法", "25分钟专注学习，5分钟休息", "时间管理"),
            StudyTip("tip2", "费曼学习法", "用简单语言解释复杂概念", "学习方法"),
            StudyTip("tip3", "间隔重复", "定期复习巩固记忆", "记忆技巧")
        )
    }
    
    val chatHistory = remember {
        listOf(
            ChatHistory("chat1", "数学函数问题", "谢谢老师的详细解答！", "2小时前", "数学助手"),
            ChatHistory("chat2", "英语作文修改", "语法错误已经修正", "昨天", "英语助手"),
            ChatHistory("chat3", "学习计划制定", "计划很详细，我会按照执行", "3天前", "通用学习助手")
        )
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 学习助手统计卡片
        item {
            ChatStatsCard()
        }
        
        // AI学习助手选择
        item {
            Text(
                text = "选择学习助手",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatAssistants) { assistant ->
                    ChatAssistantCard(
                        assistant = assistant,
                        onClick = {
                            // 导航到聊天界面
                            navController.navigate("chat/${assistant.id}")
                        }
                    )
                }
            }
        }
        
        // 快速提问
        item {
            QuickQuestionsCard(
                questions = quickQuestions,
                onQuestionClick = { question ->
                    // 导航到聊天界面并传递问题
                    navController.navigate("chat/general?question=${question.question}")
                }
            )
        }
        
        // 学习小贴士
        item {
            StudyTipsCard(
                tips = studyTips,
                onViewAll = {
                    // 查看所有学习贴士
                }
            )
        }
        
        // 最近聊天记录
        item {
            RecentChatsCard(
                chats = chatHistory,
                onViewAll = {
                    // 查看所有聊天记录
                },
                onChatClick = { chat ->
                    // 继续聊天
                    navController.navigate("chat/${chat.assistantType}/${chat.id}")
                }
            )
        }
    }
}

@Composable
private fun ChatStatsCard() {
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
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "学习助手统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsItem(
                    value = "24",
                    label = "今日对话",
                    icon = Icons.Default.Message
                )
                StatsItem(
                    value = "156",
                    label = "解决问题",
                    icon = Icons.Default.CheckCircle
                )
                StatsItem(
                    value = "4.8",
                    label = "满意度",
                    icon = Icons.Default.Star
                )
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

@Composable
private fun ChatAssistantCard(
    assistant: ChatAssistant,
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
            Box {
                Icon(
                    imageVector = assistant.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (assistant.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
                if (assistant.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Green, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = assistant.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = assistant.specialty,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFFFFB000)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = assistant.rating.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun QuickQuestionsCard(
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "快速提问",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
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
            Text(
                text = question.question,
                style = MaterialTheme.typography.bodySmall
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Composable
private fun StudyTipsCard(
    tips: List<StudyTip>,
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
                    fontWeight = FontWeight.Bold
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
                    TipCard(tip = tip)
                }
            }
        }
    }
}

@Composable
private fun TipCard(tip: StudyTip) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = tip.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tip.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tip.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RecentChatsCard(
    chats: List<ChatHistory>,
    onViewAll: () -> Unit,
    onChatClick: (ChatHistory) -> Unit
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
                    text = "最近聊天",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAll) {
                    Text("查看全部")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            chats.forEach { chat ->
                ChatHistoryItem(
                    chat = chat,
                    onClick = { onChatClick(chat) }
                )
                if (chat != chats.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ChatHistoryItem(
    chat: ChatHistory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
            Icon(
                imageVector = Icons.Default.ChatBubble,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "${chat.assistantType} · ${chat.timestamp}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}