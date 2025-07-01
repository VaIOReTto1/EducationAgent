package com.example.education.feature_student.reader

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.education.core.common_ui.CommonComponents
import com.example.education.core.network.model.StreamChatMessageResponse
import com.example.education.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 阅读器主屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    chapterId: String,
    courseId: String,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val aiResponses by viewModel.aiResponseFlow.collectAsStateWithLifecycle(initialValue = null)
    val context = LocalContext.current
    
    // 记录阅读时间
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var lastProgressUpdate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    
    // 滚动状态
    val scrollState = rememberLazyListState()
    val isScrolling by remember {
        derivedStateOf {
            scrollState.isScrollInProgress
        }
    }
    
    // 加载章节
    LaunchedEffect(chapterId, courseId) {
        viewModel.handleIntent(ReaderIntent.LoadChapter(chapterId, courseId))
        startTime = System.currentTimeMillis()
    }
    
    // 监听滚动进度并更新学习进度
    LaunchedEffect(scrollState.firstVisibleItemIndex, scrollState.firstVisibleItemScrollOffset) {
        if (!isScrolling) return@LaunchedEffect
        
        val now = System.currentTimeMillis()
        if (now - lastProgressUpdate > 10000) { // 每10秒更新一次
            val timeSpent = ((now - startTime) / 60000).toInt() // 转换为分钟
            val progress = ReaderUtils.calculateScrollProgress(
                scrollState.firstVisibleItemIndex.toFloat() + 
                (scrollState.firstVisibleItemScrollOffset / 1000f),
                uiState.tableOfContents.size.toFloat()
            )
            
            viewModel.handleIntent(ReaderIntent.UpdateProgress(progress, timeSpent))
            lastProgressUpdate = now
        }
    }
    
    // 处理跳转到指定章节
    LaunchedEffect(uiState.jumpToSectionId) {
        uiState.jumpToSectionId?.let { sectionId ->
            val index = uiState.tableOfContents.indexOfFirst { it.id == sectionId }
            if (index >= 0) {
                scrollState.animateScrollToItem(index)
            }
            viewModel.clearJumpToSection()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部应用栏
            ReaderTopBar(
                title = uiState.currentChapter?.title ?: "加载中...",
                progress = uiState.learningProgress?.progressPercentage ?: 0f,
                onNavigateBack = onNavigateBack,
                onToggleTableOfContents = {
                    viewModel.handleIntent(ReaderIntent.ToggleTableOfContents)
                },
                onToggleAiAssistant = {
                    viewModel.handleIntent(ReaderIntent.ToggleAiAssistant)
                }
            )
            
            // 主要内容区域
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        CommonComponents.LoadingIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    uiState.error != null -> {
                        CommonComponents.ErrorState(
                            message = uiState.error,
                            onRetry = {
                                viewModel.handleIntent(ReaderIntent.RetryLoad)
                            },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    uiState.currentChapter != null -> {
                        ReaderContent(
                            chapter = uiState.currentChapter,
                            progress = uiState.learningProgress,
                            estimatedTime = uiState.estimatedReadingTime,
                            scrollState = scrollState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // 目录侧边栏
                AnimatedVisibility(
                    visible = uiState.showTableOfContents,
                    enter = slideInHorizontally { it },
                    exit = slideOutHorizontally { it },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    TableOfContentsPanel(
                        items = uiState.tableOfContents,
                        onItemClick = { item ->
                            viewModel.handleIntent(ReaderIntent.JumpToSection(item.id))
                        },
                        onDismiss = {
                            viewModel.handleIntent(ReaderIntent.ToggleTableOfContents)
                        }
                    )
                }
                
                // AI助手面板
                AnimatedVisibility(
                    visible = uiState.showAiAssistant,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it },
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    AiAssistantPanel(
                        isThinking = uiState.isAiThinking,
                        error = uiState.aiError,
                        onAskQuestion = { question ->
                            viewModel.handleIntent(ReaderIntent.AskQuestion(question))
                        },
                        onRequestAdvice = {
                            viewModel.handleIntent(ReaderIntent.RequestAdvice)
                        },
                        onRequestPractice = {
                            viewModel.handleIntent(ReaderIntent.RequestPractice)
                        },
                        onDismiss = {
                            viewModel.handleIntent(ReaderIntent.ToggleAiAssistant)
                        }
                    )
                }
            }
            
            // 底部导航栏
            ReaderBottomBar(
                hasNext = uiState.hasNextChapter,
                hasPrevious = uiState.hasPreviousChapter,
                isCompleted = uiState.learningProgress?.isCompleted == true,
                onPrevious = {
                    viewModel.handleIntent(ReaderIntent.NavigateToPrevious)
                },
                onNext = {
                    viewModel.handleIntent(ReaderIntent.NavigateToNext)
                },
                onMarkCompleted = {
                    viewModel.handleIntent(ReaderIntent.MarkAsCompleted)
                }
            )
        }
        
        // AI回复显示
        aiResponses?.let { response ->
            AiResponseOverlay(
                response = response,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * 顶部应用栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderTopBar(
    title: String,
    progress: Float,
    onNavigateBack: () -> Unit,
    onToggleTableOfContents: () -> Unit,
    onToggleAiAssistant: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // 进度条
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = ReaderUtils.getProgressColor(progress),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                )
            }
        },
        actions = {
            IconButton(onClick = onToggleTableOfContents) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "目录"
                )
            }
            
            IconButton(onClick = onToggleAiAssistant) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI助手"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * 阅读器内容
 */
@Composable
private fun ReaderContent(
    chapter: com.example.education.core.database.entity.ChapterEntity,
    progress: com.example.education.core.database.entity.LearningProgressEntity?,
    estimatedTime: Int,
    scrollState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = scrollState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 章节信息卡片
        item {
            ChapterInfoCard(
                chapter = chapter,
                progress = progress,
                estimatedTime = estimatedTime
            )
        }
        
        // 章节内容
        item {
            SelectionContainer {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 清理并显示HTML内容
                        val cleanContent = ReaderUtils.cleanHtmlContent(chapter.content)
                        Text(
                            text = cleanContent,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.6f
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        
        // 媒体文件
        val mediaFiles = ReaderUtils.extractMediaUrls(chapter.content)
        if (mediaFiles.isNotEmpty()) {
            item {
                MediaFilesSection(mediaFiles = mediaFiles)
            }
        }
        
        // 底部间距
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * 章节信息卡片
 */
@Composable
private fun ChapterInfoCard(
    chapter: com.example.education.core.database.entity.ChapterEntity,
    progress: com.example.education.core.database.entity.LearningProgressEntity?,
    estimatedTime: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 章节标题
            Text(
                text = chapter.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            // 章节描述
            if (chapter.description.isNotBlank()) {
                Text(
                    text = chapter.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            // 进度和时间信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 学习进度
                if (progress != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ReaderUtils.getProgressColor(progress.progressPercentage),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = ReaderUtils.formatProgress(progress.progressPercentage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                // 预计阅读时间
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "约${ReaderUtils.formatLearningTime(estimatedTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * 媒体文件区域
 */
@Composable
private fun MediaFilesSection(mediaFiles: List<MediaFile>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "相关资源",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            mediaFiles.forEach { mediaFile ->
                MediaFileItem(
                    mediaFile = mediaFile,
                    onClick = {
                        // TODO: 实现媒体文件播放/查看
                    }
                )
            }
        }
    }
}

/**
 * 媒体文件项
 */
@Composable
private fun MediaFileItem(
    mediaFile: MediaFile,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when (mediaFile.type) {
            MediaType.IMAGE -> Icons.Default.Image
            MediaType.VIDEO -> Icons.Default.PlayCircle
            MediaType.AUDIO -> Icons.Default.AudioFile
        }
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = mediaFile.url.substringAfterLast('/'),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = "打开",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * 底部导航栏
 */
@Composable
private fun ReaderBottomBar(
    hasNext: Boolean,
    hasPrevious: Boolean,
    isCompleted: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 上一章按钮
            OutlinedButton(
                onClick = onPrevious,
                enabled = hasPrevious,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.NavigateBefore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("上一章")
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 完成按钮
            if (!isCompleted) {
                Button(
                    onClick = onMarkCompleted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProgressComplete
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("完成")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
            }
            
            // 下一章按钮
            Button(
                onClick = onNext,
                enabled = hasNext,
                modifier = Modifier.weight(1f)
            ) {
                Text("下一章")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * AI回复覆盖层
 */
@Composable
private fun AiResponseOverlay(
    response: StreamChatMessageResponse,
    modifier: Modifier = Modifier
) {
    // 这里可以实现AI回复的显示逻辑
    // 例如显示一个临时的卡片或通知
    LaunchedEffect(response) {
        // 自动消失逻辑
        delay(3000)
    }
}