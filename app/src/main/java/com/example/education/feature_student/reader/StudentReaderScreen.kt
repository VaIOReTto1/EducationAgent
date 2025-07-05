package com.example.education.feature_student.reader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.education.navigation.NavigationRoute
import com.example.education.navigation.navigateToStudentQuiz

/**
 * 学生端阅读界面
 * 
 * 展示课程章节内容，跟踪学习进度，提供AI辅导入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentReaderScreen(
    chapterId: String,
    navController: NavController,
    viewModel: StudentReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToQuiz -> {
                    navController.navigateToStudentQuiz()
                }
            }
        }
    }
    
    LaunchedEffect(chapterId) {
        viewModel.loadChapter(chapterId)
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部工具栏
        TopAppBar(
            title = { 
                Text(
                    text = uiState.chapter?.title ?: "加载中...",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            actions = {
                // 学习进度
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(uiState.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        progress = uiState.progress,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
                
                // AI辅导按钮
                IconButton(onClick = { viewModel.openAITutor() }) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = "AI辅导",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            uiState.chapter?.let { chapter ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 章节信息卡片
                    ChapterInfoCard(
                        chapter = chapter,
                        progress = uiState.progress,
                        timeSpent = uiState.timeSpent
                    )
                    
                    // 章节内容
                    ChapterContentCard(
                        content = chapter.content,
                        onReadingProgress = { progress ->
                            viewModel.updateReadingProgress(progress)
                        }
                    )
                    
                    // 学习工具栏
                    LearningToolsCard(
                        onTakeNotes = { viewModel.openNotes() },
                        onPracticeQuiz = { viewModel.startPracticeQuiz() },
                        onAskQuestion = { viewModel.askAIQuestion(it) }
                    )
                    
                    // AI推荐学习资源
                    if (uiState.recommendedResources.isNotEmpty()) {
                        RecommendedResourcesCard(
                            resources = uiState.recommendedResources,
                            onResourceClick = { resource ->
                                viewModel.openResource(resource)
                            }
                        )
                    }
                }
            }
        }
        
        // 错误状态
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.retry() }) {
                        Text("重试")
                    }
                }
            ) {
                Text(error)
            }
        }
    }
}

private fun NavController.navigateToStudentQuiz() {
    navigate(NavigationRoute.STUDENT_QUIZ)
}

/**
 * 章节信息卡片
 */
@Composable
fun ChapterInfoCard(
    chapter: ChapterInfo,
    progress: Float,
    timeSpent: Long
) {
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
            Text(
                text = chapter.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "预计${chapter.duration}分钟",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "已学${timeSpent / 60}分钟",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // 进度条
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "完成度: ${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * 章节内容卡片
 */
@Composable
fun ChapterContentCard(
    content: String,
    onReadingProgress: (Float) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    
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
                    text = "章节内容",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "收起" else "展开"
                    )
                }
            }
            
            if (isExpanded) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // 简化的Markdown渲染
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5f
                )
                
                // 模拟阅读进度更新
                LaunchedEffect(content) {
                    onReadingProgress(0.8f) // 假设阅读了80%
                }
            }
        }
    }
}

/**
 * 学习工具栏卡片
 */
@Composable
fun LearningToolsCard(
    onTakeNotes: () -> Unit,
    onPracticeQuiz: () -> Unit,
    onAskQuestion: (String) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var showQuestionDialog by remember { mutableStateOf(false) }
    
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
            Text(
                text = "学习工具",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 记笔记
                OutlinedButton(
                    onClick = onTakeNotes,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Note, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("笔记")
                }
                
                // 练习题
                OutlinedButton(
                    onClick = onPracticeQuiz,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("练习")
                }
                
                // AI提问
                OutlinedButton(
                    onClick = { showQuestionDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("提问")
                }
            }
        }
    }
    
    // AI提问对话框
    if (showQuestionDialog) {
        AlertDialog(
            onDismissRequest = { showQuestionDialog = false },
            title = { Text("向AI提问") },
            text = {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("请输入您的问题") },
                    placeholder = { Text("例如：什么是递归？") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (questionText.isNotBlank()) {
                            onAskQuestion(questionText)
                            questionText = ""
                            showQuestionDialog = false
                        }
                    }
                ) {
                    Text("提问")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuestionDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 推荐学习资源卡片
 */
@Composable
fun RecommendedResourcesCard(
    resources: List<LearningResource>,
    onResourceClick: (LearningResource) -> Unit
) {
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
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI推荐资源",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            resources.forEach { resource ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { onResourceClick(resource) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            when (resource.type) {
                                "video" -> Icons.Default.PlayArrow
                                "article" -> Icons.Default.Article
                                "exercise" -> Icons.Default.Quiz
                                else -> Icons.Default.Link
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = resource.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = resource.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
} 