package com.example.education.feature_teacher.course_content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.education.R
import com.example.education.core.database.entity.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * 课程内容管理主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseContentScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    onNavigateToChapterEditor: (String) -> Unit,
    onNavigateToAssessmentEditor: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseContentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 初始化加载
    LaunchedEffect(courseId) {
        viewModel.handleIntent(CourseContentIntent.LoadCourseContent(courseId))
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 顶部应用栏
        CourseContentTopBar(
            title = uiState.courseContentDetail?.course?.title ?: "课程内容",
            onNavigateBack = onNavigateBack,
            onShowStatistics = {
                viewModel.handleIntent(CourseContentIntent.ShowStatisticsDialog)
            },
            onImportContent = {
                viewModel.handleIntent(CourseContentIntent.ShowImportDialog)
            },
            onExportContent = {
                viewModel.handleIntent(CourseContentIntent.ExportContent)
            }
        )
        
        // 内容区域
        when {
            uiState.isLoading && uiState.courseContentDetail == null -> {
                LoadingContent()
            }
            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error,
                    onRetry = {
                        viewModel.handleIntent(CourseContentIntent.RetryLastAction)
                    },
                    onDismiss = {
                        viewModel.handleIntent(CourseContentIntent.ClearError)
                    }
                )
            }
            uiState.courseContentDetail != null -> {
                CourseContentList(
                    courseContentDetail = uiState.courseContentDetail,
                    statistics = uiState.statistics,
                    isRefreshing = uiState.isLoading,
                    onRefresh = {
                        viewModel.handleIntent(CourseContentIntent.RefreshContent)
                    },
                    onCreateChapter = {
                        viewModel.handleIntent(CourseContentIntent.ShowCreateChapterDialog)
                    },
                    onEditChapter = { chapter ->
                        viewModel.handleIntent(CourseContentIntent.ShowEditChapterDialog(chapter))
                    },
                    onDeleteChapter = { chapter ->
                        viewModel.handleIntent(
                            CourseContentIntent.ShowDeleteConfirmDialog(
                                itemType = "章节",
                                itemId = chapter.id,
                                itemTitle = chapter.title
                            )
                        )
                    },
                    onDuplicateChapter = { chapter ->
                        viewModel.handleIntent(
                            CourseContentIntent.DuplicateChapter(
                                chapterId = chapter.id,
                                newTitle = "${chapter.title} (副本)"
                            )
                        )
                    },
                    onReorderChapters = { chapterOrders ->
                        viewModel.handleIntent(CourseContentIntent.ReorderChapters(chapterOrders))
                    },
                    onCreateAssessment = { chapterId ->
                        viewModel.handleIntent(CourseContentIntent.ShowCreateAssessmentDialog(chapterId))
                    },
                    onEditAssessment = { assessment ->
                        viewModel.handleIntent(CourseContentIntent.ShowEditAssessmentDialog(assessment))
                    },
                    onDeleteAssessment = { assessment ->
                        viewModel.handleIntent(
                            CourseContentIntent.ShowDeleteConfirmDialog(
                                itemType = "评估",
                                itemId = assessment.id,
                                itemTitle = assessment.title
                            )
                        )
                    },
                    onNavigateToChapterEditor = onNavigateToChapterEditor,
                    onNavigateToAssessmentEditor = onNavigateToAssessmentEditor
                )
            }
        }
    }
    
    // 对话框
    CourseContentDialogs(
        uiState = uiState,
        onIntent = viewModel::handleIntent
    )
}

/**
 * 顶部应用栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseContentTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    onShowStatistics: () -> Unit,
    onImportContent: () -> Unit,
    onExportContent: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
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
            // 统计信息
            IconButton(onClick = onShowStatistics) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "统计信息"
                )
            }
            
            // 更多操作
            var showMenu by remember { mutableStateOf(false) }
            
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多"
                )
            }
            
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("导入内容") },
                    onClick = {
                        showMenu = false
                        onImportContent()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null
                        )
                    }
                )
                
                DropdownMenuItem(
                    text = { Text("导出内容") },
                    onClick = {
                        showMenu = false
                        onExportContent()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    )
}

/**
 * 课程内容列表
 */
@Composable
private fun CourseContentList(
    courseContentDetail: CourseContentDetail,
    statistics: ContentStatistics?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onCreateChapter: () -> Unit,
    onEditChapter: (Chapter) -> Unit,
    onDeleteChapter: (Chapter) -> Unit,
    onDuplicateChapter: (Chapter) -> Unit,
    onReorderChapters: (List<Pair<String, Int>>) -> Unit,
    onCreateAssessment: (String?) -> Unit,
    onEditAssessment: (Assessment) -> Unit,
    onDeleteAssessment: (Assessment) -> Unit,
    onNavigateToChapterEditor: (String) -> Unit,
    onNavigateToAssessmentEditor: (String) -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 课程信息卡片
            item {
                CourseInfoCard(
                    course = courseContentDetail.course,
                    statistics = statistics
                )
            }
            
            // 快速操作卡片
            item {
                QuickActionsCard(
                    onCreateChapter = onCreateChapter,
                    onCreateAssessment = { onCreateAssessment(null) }
                )
            }
            
            // 章节列表
            if (courseContentDetail.chapters.isNotEmpty()) {
                item {
                    Text(
                        text = "章节列表 (${courseContentDetail.chapters.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(
                    items = courseContentDetail.chapters,
                    key = { it.id }
                ) { chapter ->
                    val chapterAssessments = courseContentDetail.assessments.filter { 
                        it.chapterId == chapter.id 
                    }
                    
                    ChapterCard(
                        chapter = chapter,
                        assessments = chapterAssessments,
                        onEdit = { onEditChapter(chapter) },
                        onDelete = { onDeleteChapter(chapter) },
                        onDuplicate = { onDuplicateChapter(chapter) },
                        onCreateAssessment = { onCreateAssessment(chapter.id) },
                        onEditAssessment = onEditAssessment,
                        onDeleteAssessment = onDeleteAssessment,
                        onNavigateToEditor = { onNavigateToChapterEditor(chapter.id) },
                        onNavigateToAssessmentEditor = onNavigateToAssessmentEditor
                    )
                }
            } else {
                item {
                    EmptyChapterCard(
                        onCreateChapter = onCreateChapter
                    )
                }
            }
            
            // 课程级评估
            val courseAssessments = courseContentDetail.assessments.filter { it.chapterId == null }
            if (courseAssessments.isNotEmpty()) {
                item {
                    Text(
                        text = "课程评估 (${courseAssessments.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(
                    items = courseAssessments,
                    key = { it.id }
                ) { assessment ->
                    AssessmentCard(
                        assessment = assessment,
                        onEdit = { onEditAssessment(assessment) },
                        onDelete = { onDeleteAssessment(assessment) },
                        onNavigateToEditor = { onNavigateToAssessmentEditor(assessment.id) }
                    )
                }
            }
        }
    }
}

/**
 * 课程信息卡片
 */
@Composable
private fun CourseInfoCard(
    course: Course,
    statistics: ContentStatistics?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            if (course.description.isNotBlank()) {
                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (statistics != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(
                        icon = Icons.Default.MenuBook,
                        label = "章节",
                        value = statistics.totalChapters.toString()
                    )
                    
                    StatisticItem(
                        icon = Icons.Default.Quiz,
                        label = "评估",
                        value = statistics.totalAssessments.toString()
                    )
                    
                    StatisticItem(
                        icon = Icons.Default.TextFields,
                        label = "字数",
                        value = CourseContentUtils.formatWordCount(statistics.totalWords)
                    )
                    
                    StatisticItem(
                        icon = Icons.Default.Schedule,
                        label = "时长",
                        value = CourseContentUtils.formatLearningTime(statistics.estimatedDuration)
                    )
                }
            }
        }
    }
}

/**
 * 快速操作卡片
 */
@Composable
private fun QuickActionsCard(
    onCreateChapter: () -> Unit,
    onCreateAssessment: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "快速操作",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCreateChapter,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("新建章节")
                }
                
                OutlinedButton(
                    onClick = onCreateAssessment,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("新建评估")
                }
            }
        }
    }
}

/**
 * 章节卡片
 */
@Composable
private fun ChapterCard(
    chapter: Chapter,
    assessments: List<Assessment>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onCreateAssessment: () -> Unit,
    onEditAssessment: (Assessment) -> Unit,
    onDeleteAssessment: (Assessment) -> Unit,
    onNavigateToEditor: () -> Unit,
    onNavigateToAssessmentEditor: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 章节标题和操作
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterVertically,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "第${chapter.order}章 ${chapter.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (chapter.description.isNotBlank()) {
                        Text(
                            text = chapter.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // 章节统计
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "${CourseContentUtils.formatWordCount(chapter.content.length)} 字",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "${assessments.size} 个评估",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // 操作菜单
                var showMenu by remember { mutableStateOf(false) }
                
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多操作"
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("编辑内容") },
                        onClick = {
                            showMenu = false
                            onNavigateToEditor()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("编辑信息") },
                        onClick = {
                            showMenu = false
                            onEdit()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("复制章节") },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("删除章节") },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
            
            // 评估列表
            if (assessments.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "章节评估",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    assessments.forEach { assessment ->
                        AssessmentItem(
                            assessment = assessment,
                            onEdit = { onEditAssessment(assessment) },
                            onDelete = { onDeleteAssessment(assessment) },
                            onNavigateToEditor = { onNavigateToAssessmentEditor(assessment.id) }
                        )
                    }
                }
            }
            
            // 添加评估按钮
            TextButton(
                onClick = onCreateAssessment,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("添加评估")
            }
        }
    }
}

/**
 * 评估项
 */
@Composable
private fun AssessmentItem(
    assessment: Assessment,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onNavigateToEditor: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = assessment.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = CourseContentUtils.getAssessmentTypeText(assessment.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = CourseContentUtils.getAssessmentTypeColor(assessment.type)
                    )
                    
                    Text(
                        text = "${assessment.questions.size} 题",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (assessment.timeLimit != null) {
                        Text(
                            text = "${assessment.timeLimit}分钟",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Row {
                IconButton(
                    onClick = onNavigateToEditor,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "设置",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 评估卡片
 */
@Composable
private fun AssessmentCard(
    assessment: Assessment,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onNavigateToEditor: () -> Unit
) {
    Card(
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (assessment.description.isNotBlank()) {
                    Text(
                        text = assessment.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = CourseContentUtils.getAssessmentTypeText(assessment.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = CourseContentUtils.getAssessmentTypeColor(assessment.type)
                    )
                    
                    Text(
                        text = "${assessment.questions.size} 题",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (assessment.timeLimit != null) {
                        Text(
                            text = "${assessment.timeLimit}分钟",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = "及格分: ${assessment.passingScore}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row {
                IconButton(onClick = onNavigateToEditor) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑内容"
                    )
                }
                
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "编辑设置"
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * 空章节卡片
 */
@Composable
private fun EmptyChapterCard(
    onCreateChapter: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "还没有章节",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "创建第一个章节来开始构建课程内容",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(
                onClick = onCreateChapter
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("创建章节")
            }
        }
    }
}

/**
 * 统计项
 */
@Composable
private fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 加载状态
 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "加载课程内容中...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 错误状态
 */
@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "加载失败",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onRetry,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("重试")
                }
                
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("关闭")
                }
            }
        }
    }
}