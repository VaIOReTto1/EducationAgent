package com.example.education.feature_teacher.course_management

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * 教师课程管理主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCourseManagementScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToChapterEditor: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherCourseManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 顶部应用栏
        CourseManagementTopBar(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { viewModel.handleIntent(TeacherCourseManagementIntent.SearchCourses(it)) },
            onCreateCourse = { viewModel.handleIntent(TeacherCourseManagementIntent.ShowCreateCourseDialog) }
        )
        
        // 过滤和排序控制
        FilterAndSortControls(
            selectedCategory = uiState.selectedCategory,
            selectedDifficulty = uiState.selectedDifficulty,
            selectedStatus = uiState.selectedStatus,
            sortOption = uiState.sortOption,
            onCategoryChange = { viewModel.handleIntent(TeacherCourseManagementIntent.FilterByCategory(it)) },
            onDifficultyChange = { viewModel.handleIntent(TeacherCourseManagementIntent.FilterByDifficulty(it)) },
            onStatusChange = { viewModel.handleIntent(TeacherCourseManagementIntent.FilterByStatus(it)) },
            onSortChange = { viewModel.handleIntent(TeacherCourseManagementIntent.SortCourses(it)) }
        )
        
        // 课程列表
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.handleIntent(TeacherCourseManagementIntent.RefreshCourses) },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.error != null -> {
                    ErrorStateCard(
                        error = uiState.error,
                        onRetry = { viewModel.handleIntent(TeacherCourseManagementIntent.RetryLastAction) },
                        onDismiss = { viewModel.handleIntent(TeacherCourseManagementIntent.ClearError) }
                    )
                }
                
                uiState.filteredCourses.isEmpty() -> {
                    EmptyStateCard(
                        searchQuery = uiState.searchQuery,
                        onCreateCourse = { viewModel.handleIntent(TeacherCourseManagementIntent.ShowCreateCourseDialog) }
                    )
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.filteredCourses,
                            key = { it.course.id }
                        ) { courseDetail ->
                            CourseManagementCard(
                                courseDetail = courseDetail,
                                onCourseClick = { 
                                    viewModel.handleIntent(TeacherCourseManagementIntent.SelectCourse(it.course.id))
                                    onNavigateToDetail(it.course.id)
                                },
                                onEditClick = { 
                                    viewModel.handleIntent(TeacherCourseManagementIntent.ShowEditCourseDialog(it.course))
                                },
                                onPublishClick = { 
                                    viewModel.handleIntent(TeacherCourseManagementIntent.PublishCourse(it.course.id))
                                },
                                onDeleteClick = { 
                                    viewModel.handleIntent(TeacherCourseManagementIntent.DeleteCourse(it.course.id))
                                },
                                onViewStudentsClick = {
                                    viewModel.handleIntent(TeacherCourseManagementIntent.ShowStudentList(it.course.id))
                                },
                                onAddChapterClick = {
                                    onNavigateToChapterEditor(it.course.id)
                                },
                                isPublishing = uiState.isPublishingCourse,
                                isDeleting = uiState.isDeletingCourse
                            )
                        }
                    }
                }
            }
        }
    }
    
    // 创建课程对话框
    if (uiState.showCreateCourseDialog) {
        CreateCourseDialog(
            onDismiss = { viewModel.handleIntent(TeacherCourseManagementIntent.HideCreateCourseDialog) },
            onCreateCourse = { course ->
                viewModel.handleIntent(TeacherCourseManagementIntent.CreateCourse(course))
            },
            isCreating = uiState.isCreatingCourse
        )
    }
    
    // 编辑课程对话框
    if (uiState.showEditCourseDialog && uiState.editingCourse != null) {
        EditCourseDialog(
            course = uiState.editingCourse,
            onDismiss = { viewModel.handleIntent(TeacherCourseManagementIntent.HideEditCourseDialog) },
            onUpdateCourse = { course ->
                viewModel.handleIntent(TeacherCourseManagementIntent.UpdateCourse(course))
            },
            isUpdating = uiState.isUpdatingCourse
        )
    }
    
    // 课程详情对话框
    if (uiState.showCourseDetail && uiState.selectedCourseDetail != null) {
        CourseDetailDialog(
            courseDetail = uiState.selectedCourseDetail,
            onDismiss = { viewModel.handleIntent(TeacherCourseManagementIntent.HideCourseDetail) },
            onEditCourse = { course ->
                viewModel.handleIntent(TeacherCourseManagementIntent.HideCourseDetail)
                viewModel.handleIntent(TeacherCourseManagementIntent.ShowEditCourseDialog(course))
            },
            onAddChapter = { courseId ->
                viewModel.handleIntent(TeacherCourseManagementIntent.HideCourseDetail)
                onNavigateToChapterEditor(courseId)
            },
            isLoading = uiState.isLoadingCourseDetail
        )
    }
    
    // 学生列表对话框
    if (uiState.showStudentList) {
        StudentListDialog(
            students = uiState.courseStudents,
            onDismiss = { viewModel.handleIntent(TeacherCourseManagementIntent.HideStudentList) },
            onRemoveStudent = { courseId, studentId ->
                viewModel.handleIntent(TeacherCourseManagementIntent.RemoveStudent(courseId, studentId))
            },
            isLoading = uiState.isLoadingStudents,
            isRemoving = uiState.isRemovingStudent
        )
    }
}

/**
 * 顶部应用栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseManagementTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCreateCourse: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = {
            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("搜索课程...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "课程管理",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    isSearchActive = !isSearchActive
                    if (!isSearchActive) {
                        onSearchQueryChange("")
                    }
                }
            ) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (isSearchActive) "关闭搜索" else "搜索"
                )
            }
            
            IconButton(onClick = onCreateCourse) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "创建课程"
                )
            }
        },
        modifier = modifier
    )
}

/**
 * 过滤和排序控制
 */
@Composable
private fun FilterAndSortControls(
    selectedCategory: String,
    selectedDifficulty: String,
    selectedStatus: String,
    sortOption: CourseSortOption,
    onCategoryChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onSortChange: (CourseSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "筛选和排序",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 类别过滤
                FilterChip(
                    selected = selectedCategory != "全部",
                    onClick = { /* 显示类别选择器 */ },
                    label = { Text("类别: $selectedCategory") },
                    modifier = Modifier.weight(1f)
                )
                
                // 难度过滤
                FilterChip(
                    selected = selectedDifficulty != "全部",
                    onClick = { /* 显示难度选择器 */ },
                    label = { Text("难度: $selectedDifficulty") },
                    modifier = Modifier.weight(1f)
                )
                
                // 状态过滤
                FilterChip(
                    selected = selectedStatus != "全部",
                    onClick = { /* 显示状态选择器 */ },
                    label = { Text("状态: $selectedStatus") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 排序选择
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "排序:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilterChip(
                    selected = true,
                    onClick = { /* 显示排序选择器 */ },
                    label = { Text(sortOption.displayName) }
                )
            }
        }
    }
}

/**
 * 课程管理卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseManagementCard(
    courseDetail: TeacherCourseDetail,
    onCourseClick: (TeacherCourseDetail) -> Unit,
    onEditClick: (TeacherCourseDetail) -> Unit,
    onPublishClick: (TeacherCourseDetail) -> Unit,
    onDeleteClick: (TeacherCourseDetail) -> Unit,
    onViewStudentsClick: (TeacherCourseDetail) -> Unit,
    onAddChapterClick: (TeacherCourseDetail) -> Unit,
    isPublishing: Boolean,
    isDeleting: Boolean,
    modifier: Modifier = Modifier
) {
    val course = courseDetail.course
    val utils = TeacherCourseManagementUtils
    
    Card(
        onClick = { onCourseClick(courseDetail) },
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 课程标题和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                AssistChip(
                    onClick = { },
                    label = { Text(utils.getCourseStatusText(courseDetail)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = utils.getCourseStatusColor(courseDetail).copy(alpha = 0.1f),
                        labelColor = utils.getCourseStatusColor(courseDetail)
                    )
                )
            }
            
            // 课程描述
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // 课程标签
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(course.category) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = utils.getCategoryColor(course.category).copy(alpha = 0.1f),
                        labelColor = utils.getCategoryColor(course.category)
                    )
                )
                
                AssistChip(
                    onClick = { },
                    label = { Text(utils.getDifficultyText(course.difficulty)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = utils.getDifficultyColor(course.difficulty).copy(alpha = 0.1f),
                        labelColor = utils.getDifficultyColor(course.difficulty)
                    )
                )
            }
            
            // 统计信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    icon = Icons.Default.People,
                    label = "学生",
                    value = courseDetail.totalStudents.toString()
                )
                
                StatisticItem(
                    icon = Icons.Default.MenuBook,
                    label = "章节",
                    value = courseDetail.chapters.size.toString()
                )
                
                StatisticItem(
                    icon = Icons.Default.TrendingUp,
                    label = "平均进度",
                    value = utils.formatProgress(courseDetail.averageProgress)
                )
                
                StatisticItem(
                    icon = Icons.Default.Schedule,
                    label = "学习时长",
                    value = utils.formatLearningTime(courseDetail.averageLearningTime)
                )
            }
            
            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 编辑按钮
                OutlinedButton(
                    onClick = { onEditClick(courseDetail) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("编辑")
                }
                
                // 发布/取消发布按钮
                if (!course.isPublished) {
                    Button(
                        onClick = { onPublishClick(courseDetail) },
                        enabled = !isPublishing,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isPublishing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Publish,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("发布")
                    }
                } else {
                    OutlinedButton(
                        onClick = { onViewStudentsClick(courseDetail) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("学生")
                    }
                }
                
                // 添加章节按钮
                OutlinedButton(
                    onClick = { onAddChapterClick(courseDetail) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("章节")
                }
            }
        }
    }
}

/**
 * 统计项组件
 */
@Composable
private fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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
 * 错误状态卡片
 */
@Composable
private fun ErrorStateCard(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            
            Text(
                text = "出现错误",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("关闭")
                }
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("重试")
                }
            }
        }
    }
}

/**
 * 空状态卡片
 */
@Composable
private fun EmptyStateCard(
    searchQuery: String,
    onCreateCourse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.School,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = if (searchQuery.isNotEmpty()) "未找到相关课程" else "还没有课程",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = if (searchQuery.isNotEmpty()) {
                    "尝试调整搜索条件或筛选器"
                } else {
                    "创建您的第一门课程，开始教学之旅"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (searchQuery.isEmpty()) {
                Button(
                    onClick = onCreateCourse
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("创建课程")
                }
            }
        }
    }
}