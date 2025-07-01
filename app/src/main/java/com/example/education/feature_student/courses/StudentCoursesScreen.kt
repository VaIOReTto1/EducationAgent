package com.example.education.feature_student.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 学生课程页面
 * 显示课程列表、推荐课程、继续学习等功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCoursesScreen(
    onNavigateToCourseDetail: (String) -> Unit,
    onNavigateToReader: (String, String) -> Unit, // courseId, chapterId
    modifier: Modifier = Modifier,
    viewModel: StudentCoursesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 显示错误对话框
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.handleIntent(StudentCoursesIntent.ClearError) },
            title = { Text("错误") },
            text = { Text(error) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.handleIntent(StudentCoursesIntent.RetryLastAction) }
                ) {
                    Text("重试")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.handleIntent(StudentCoursesIntent.ClearError) }
                ) {
                    Text("取消")
                }
            }
        )
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 顶部应用栏
        CoursesTopBar(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { query ->
                viewModel.handleIntent(StudentCoursesIntent.SearchCourses(query))
            },
            onFilterClick = { /* TODO: 显示过滤对话框 */ }
        )
        
        // 下拉刷新
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.handleIntent(StudentCoursesIntent.RefreshCourses) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 学习统计卡片
                item {
                    uiState.learningStatistics?.let { statistics ->
                        LearningStatisticsCard(
                            statistics = statistics,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // 继续学习部分
                if (uiState.continueLearningCourses.isNotEmpty()) {
                    item {
                        Text(
                            text = "继续学习",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(
                                items = uiState.continueLearningCourses,
                                key = { it.course.id }
                            ) { courseWithProgress ->
                                ContinueLearningCard(
                                    courseWithProgress = courseWithProgress,
                                    onClick = {
                                        // 导航到课程详情或直接开始学习
                                        onNavigateToCourseDetail(courseWithProgress.course.id)
                                    },
                                    modifier = Modifier.width(280.dp)
                                )
                            }
                        }
                    }
                }
                
                // 推荐课程部分
                if (uiState.recommendedCourses.isNotEmpty()) {
                    item {
                        Text(
                            text = "为您推荐",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(
                                items = uiState.recommendedCourses,
                                key = { it.course.id }
                            ) { courseWithProgress ->
                                RecommendedCourseCard(
                                    courseWithProgress = courseWithProgress,
                                    onClick = {
                                        onNavigateToCourseDetail(courseWithProgress.course.id)
                                    },
                                    onEnrollClick = {
                                        viewModel.handleIntent(
                                            StudentCoursesIntent.EnrollCourse(courseWithProgress.course.id)
                                        )
                                    },
                                    isEnrolling = uiState.isEnrolling,
                                    modifier = Modifier.width(240.dp)
                                )
                            }
                        }
                    }
                }
                
                // 过滤和排序控制
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.isSearching() || uiState.hasFilters()) {
                                "搜索结果 (${uiState.filteredCourses.size})"
                            } else {
                                "所有课程 (${uiState.allCourses.size})"
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row {
                            // 排序按钮
                            IconButton(
                                onClick = { /* TODO: 显示排序选项 */ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "排序"
                                )
                            }
                            
                            // 只显示已注册课程切换
                            FilterChip(
                                selected = uiState.showOnlyEnrolled,
                                onClick = {
                                    viewModel.handleIntent(StudentCoursesIntent.ToggleEnrolledOnly)
                                },
                                label = { Text("已注册") },
                                leadingIcon = if (uiState.showOnlyEnrolled) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
                
                // 课程列表
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.hasSearchResults()) {
                    items(
                        items = uiState.filteredCourses,
                        key = { it.course.id }
                    ) { courseWithProgress ->
                        CourseListItem(
                            courseWithProgress = courseWithProgress,
                            onClick = {
                                onNavigateToCourseDetail(courseWithProgress.course.id)
                            },
                            onEnrollClick = {
                                viewModel.handleIntent(
                                    StudentCoursesIntent.EnrollCourse(courseWithProgress.course.id)
                                )
                            },
                            onContinueClick = { chapterId ->
                                onNavigateToReader(courseWithProgress.course.id, chapterId)
                            },
                            isEnrolling = uiState.isEnrolling,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    item {
                        EmptyStateCard(
                            title = if (uiState.isSearching()) "未找到相关课程" else "暂无课程",
                            description = if (uiState.isSearching()) {
                                "尝试调整搜索关键词或过滤条件"
                            } else {
                                "还没有可用的课程，请稍后再试"
                            },
                            actionText = if (uiState.isSearching()) "清除搜索" else "刷新",
                            onActionClick = {
                                if (uiState.isSearching()) {
                                    viewModel.handleIntent(StudentCoursesIntent.SearchCourses(""))
                                } else {
                                    viewModel.handleIntent(StudentCoursesIntent.RefreshCourses)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * 课程页面顶部应用栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoursesTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
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
                Text("我的课程")
            }
        },
        actions = {
            if (isSearchActive) {
                IconButton(
                    onClick = {
                        isSearchActive = false
                        onSearchQueryChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭搜索"
                    )
                }
            } else {
                IconButton(
                    onClick = { isSearchActive = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                }
                
                IconButton(
                    onClick = onFilterClick
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "过滤"
                    )
                }
            }
        },
        modifier = modifier
    )
}

/**
 * 空状态卡片
 */
@Composable
private fun EmptyStateCard(
    title: String,
    description: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(
                onClick = onActionClick
            ) {
                Text(actionText)
            }
        }
    }
}