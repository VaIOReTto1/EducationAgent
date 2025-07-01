package com.example.education.feature_student.chapter_reader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.collections.isNotEmpty

/**
 * 章节阅读主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterReaderScreen(
    chapterId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAssessment: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChapterReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val utils = ChapterReaderUtils
    
    // 加载章节
    LaunchedEffect(chapterId) {
        viewModel.handleIntent(ChapterReaderIntent.LoadChapter(chapterId))
    }
    
    // 处理错误
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // 可以显示Snackbar或其他错误提示
        }
    }
    
    Scaffold(
        topBar = {
            ChapterReaderTopBar(
                chapterDetail = uiState.chapterDetail,
                isOutlineVisible = uiState.isOutlineVisible,
                isNotesVisible = uiState.isNotesVisible,
                isSettingsVisible = uiState.isSettingsVisible,
                onNavigateBack = onNavigateBack,
                onToggleOutline = { viewModel.handleIntent(ChapterReaderIntent.ToggleOutline) },
                onToggleNotes = { viewModel.handleIntent(ChapterReaderIntent.ToggleNotes) },
                onToggleSettings = { viewModel.handleIntent(ChapterReaderIntent.ToggleSettings) },
                onSearch = { query -> viewModel.handleIntent(ChapterReaderIntent.SearchInChapter(query)) }
            )
        },
        bottomBar = {
            ChapterReaderBottomBar(
                chapterDetail = uiState.chapterDetail,
                readingProgress = uiState.readingProgress,
                isCompleted = uiState.isCompleted,
                isReading = uiState.isReading,
                onNavigatePrevious = { viewModel.handleIntent(ChapterReaderIntent.NavigateToPrevious) },
                onNavigateNext = { viewModel.handleIntent(ChapterReaderIntent.NavigateToNext) },
                onMarkCompleted = { viewModel.handleIntent(ChapterReaderIntent.MarkChapterCompleted) },
                onToggleReading = {
                    if (uiState.isReading) {
                        viewModel.handleIntent(ChapterReaderIntent.PauseReading)
                    } else {
                        viewModel.handleIntent(ChapterReaderIntent.ResumeReading)
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = { viewModel.handleIntent(ChapterReaderIntent.RetryLastAction) },
                        onDismiss = { viewModel.handleIntent(ChapterReaderIntent.ClearError) }
                    )
                }
                uiState.chapterDetail != null -> {
                    ChapterContent(
                        chapterDetail = uiState.chapterDetail,
                        readingProgress = uiState.readingProgress,
                        fontSize = uiState.fontSize,
                        isDarkTheme = uiState.isDarkTheme,
                        searchQuery = uiState.searchQuery,
                        searchResults = uiState.searchResults,
                        onProgressUpdate = { progress ->
                            viewModel.handleIntent(ChapterReaderIntent.UpdateReadingProgress(progress))
                        },
                        onNavigateToAssessment = onNavigateToAssessment
                    )
                }
            }
            
            // 侧边栏
            if (uiState.isOutlineVisible) {
                OutlineSidebar(
                    outline = uiState.outline,
                    onDismiss = { viewModel.handleIntent(ChapterReaderIntent.ToggleOutline) },
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            
            if (uiState.isNotesVisible) {
                NotesSidebar(
                    notes = uiState.notes,
                    isAddingNote = uiState.isAddingNote,
                    onAddNote = { content, position ->
                        viewModel.handleIntent(ChapterReaderIntent.AddNote(content, position))
                    },
                    onDismiss = { viewModel.handleIntent(ChapterReaderIntent.ToggleNotes) },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            
            if (uiState.isSettingsVisible) {
                SettingsDialog(
                    fontSize = uiState.fontSize,
                    isDarkTheme = uiState.isDarkTheme,
                    onFontSizeChange = { fontSize ->
                        viewModel.handleIntent(ChapterReaderIntent.UpdateFontSize(fontSize))
                    },
                    onThemeChange = { isDark ->
                        viewModel.handleIntent(ChapterReaderIntent.UpdateTheme(isDark))
                    },
                    onDismiss = { viewModel.handleIntent(ChapterReaderIntent.ToggleSettings) }
                )
            }
        }
    }
}

/**
 * 顶部应用栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapterReaderTopBar(
    chapterDetail: ChapterDetail?,
    isOutlineVisible: Boolean,
    isNotesVisible: Boolean,
    isSettingsVisible: Boolean,
    onNavigateBack: () -> Unit,
    onToggleOutline: () -> Unit,
    onToggleNotes: () -> Unit,
    onToggleSettings: () -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    TopAppBar(
        title = {
            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("搜索章节内容...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    onSearch(searchQuery)
                                }
                                isSearchActive = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "搜索"
                            )
                        }
                    }
                )
            } else {
                Column {
                    Text(
                        text = chapterDetail?.chapter?.title ?: "加载中...",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (chapterDetail != null) {
                        Text(
                            text = "第${chapterDetail.currentChapterIndex}章 / 共${chapterDetail.totalChapters}章",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
            if (!isSearchActive) {
                // 搜索按钮
                IconButton(
                    onClick = { isSearchActive = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                }
                
                // 大纲按钮
                IconButton(
                    onClick = onToggleOutline
                ) {
                    Icon(
                        imageVector = if (isOutlineVisible) Icons.Default.MenuOpen else Icons.Default.Menu,
                        contentDescription = "大纲",
                        tint = if (isOutlineVisible) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
                
                // 笔记按钮
                IconButton(
                    onClick = onToggleNotes
                ) {
                    Icon(
                        imageVector = if (isNotesVisible) Icons.Default.EditNote else Icons.Default.Note,
                        contentDescription = "笔记",
                        tint = if (isNotesVisible) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
                
                // 设置按钮
                IconButton(
                    onClick = onToggleSettings
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "设置",
                        tint = if (isSettingsVisible) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
            } else {
                // 取消搜索
                IconButton(
                    onClick = {
                        isSearchActive = false
                        searchQuery = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "取消搜索"
                    )
                }
            }
        },
        modifier = modifier
    )
}

/**
 * 底部导航栏
 */
@Composable
private fun ChapterReaderBottomBar(
    chapterDetail: ChapterDetail?,
    readingProgress: Float,
    isCompleted: Boolean,
    isReading: Boolean,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit,
    onMarkCompleted: () -> Unit,
    onToggleReading: () -> Unit,
    modifier: Modifier = Modifier
) {
    val utils = ChapterReaderUtils
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 进度条
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = utils.formatProgress(readingProgress),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(50.dp)
                )
                
                LinearProgressIndicator(
                    progress = readingProgress,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    color = utils.getProgressColor(readingProgress)
                )
                
                Text(
                    text = if (isCompleted) "已完成" else "进行中",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCompleted) utils.getProgressColor(1f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 上一章节
                OutlinedButton(
                    onClick = onNavigatePrevious,
                    enabled = chapterDetail?.previousChapter != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.NavigateBefore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("上一章")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 阅读控制
                FilledTonalButton(
                    onClick = onToggleReading,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isReading) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isReading) "暂停" else "继续")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 标记完成或下一章节
                if (!isCompleted) {
                    Button(
                        onClick = onMarkCompleted,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("完成")
                    }
                } else {
                    Button(
                        onClick = onNavigateNext,
                        enabled = chapterDetail?.nextChapter != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("下一章")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.NavigateNext,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 章节内容
 */
@Composable
private fun ChapterContent(
    chapterDetail: ChapterDetail,
    readingProgress: Float,
    fontSize: Float,
    isDarkTheme: Boolean,
    searchQuery: String,
    searchResults: List<SearchResult>,
    onProgressUpdate: (Float) -> Unit,
    onNavigateToAssessment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    
    // 监听滚动位置更新进度
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        // 简单的进度计算逻辑
        val totalItems = listState.layoutInfo.totalItemsCount
        if (totalItems > 0) {
            val visibleItem = listState.firstVisibleItemIndex
            val progress = visibleItem.toFloat() / totalItems
            onProgressUpdate(progress)
        }
    }
    
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 章节标题
        item {
            ChapterHeader(
                chapterDetail = chapterDetail,
                fontSize = fontSize
            )
        }
        
        // 章节内容
        item {
            ChapterContentText(
                content = chapterDetail.chapter.content,
                fontSize = fontSize,
                searchQuery = searchQuery,
                searchResults = searchResults
            )
        }
        
        // 评估列表
        if (chapterDetail.assessments.isNotEmpty()) {
            item {
                ChapterAssessments(
                    assessments = chapterDetail.assessments,
                    onNavigateToAssessment = onNavigateToAssessment
                )
            }
        }
        
        // 学习提示
        item {
            LearningTips(
                chapterDetail = chapterDetail
            )
        }
    }
}

/**
 * 章节标题
 */
@Composable
private fun ChapterHeader(
    chapterDetail: ChapterDetail,
    fontSize: Float,
    modifier: Modifier = Modifier
) {
    val utils = ChapterReaderUtils
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = chapterDetail.chapter.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = (fontSize + 8).sp
                ),
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = chapterDetail.course.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // 章节信息
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                utils.getChapterTags(chapterDetail).forEach { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text(tag) }
                    )
                }
            }
            
            // 章节统计
            Text(
                text = utils.formatChapterStats(chapterDetail),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 章节内容文本
 */
@Composable
private fun ChapterContentText(
    content: String,
    fontSize: Float,
    searchQuery: String,
    searchResults: List<SearchResult>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 搜索结果提示
            if (searchQuery.isNotEmpty() && searchResults.isNotEmpty()) {
                Text(
                    text = "找到 ${searchResults.size} 个搜索结果",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // 内容文本
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.5).sp
                ),
                textAlign = TextAlign.Justify
            )
        }
    }
}

/**
 * 章节评估
 */
@Composable
private fun ChapterAssessments(
    assessments: List<com.example.education.core.database.entity.Assessment>,
    onNavigateToAssessment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "章节评估",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            assessments.forEach { assessment ->
                OutlinedCard(
                    onClick = { onNavigateToAssessment(assessment.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = assessment.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Text(
                                text = assessment.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "开始评估"
                        )
                    }
                }
            }
        }
    }
}

/**
 * 学习提示
 */
@Composable
private fun LearningTips(
    chapterDetail: ChapterDetail,
    modifier: Modifier = Modifier
) {
    val utils = ChapterReaderUtils
    val tips = utils.getLearningTips(chapterDetail)
    
    if (tips.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
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
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "学习提示",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                tips.forEach { tip ->
                    Text(
                        text = "• $tip",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/**
 * 加载内容
 */
@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "正在加载章节...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 错误内容
 */
@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = "加载失败",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("关闭")
                    }
                    
                    Button(onClick = onRetry) {
                        Text("重试")
                    }
                }
            }
        }
    }
}