package com.example.education.feature_teacher.course_management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.education.core.database.entity.Course
import com.example.education.core.database.entity.Chapter

/**
 * 创建课程对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseDialog(
    onDismiss: () -> Unit,
    onCreateCourse: (Course) -> Unit,
    isCreating: Boolean,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("数学") }
    var difficulty by remember { mutableStateOf("初级") }
    var expanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }
    
    val categories = listOf("数学", "科学", "语言", "历史", "艺术", "技术")
    val difficulties = listOf("初级", "中级", "高级", "专家")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "创建新课程",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // 课程标题
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("课程标题") },
                    placeholder = { Text("输入课程标题") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 课程描述
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("课程描述") },
                    placeholder = { Text("输入课程描述") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 课程类别
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("课程类别") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { categoryOption ->
                            DropdownMenuItem(
                                text = { Text(categoryOption) },
                                onClick = {
                                    category = categoryOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // 课程难度
                ExposedDropdownMenuBox(
                    expanded = difficultyExpanded,
                    onExpandedChange = { difficultyExpanded = !difficultyExpanded }
                ) {
                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("课程难度") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = difficultyExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = difficultyExpanded,
                        onDismissRequest = { difficultyExpanded = false }
                    ) {
                        difficulties.forEach { difficultyOption ->
                            DropdownMenuItem(
                                text = { Text(difficultyOption) },
                                onClick = {
                                    difficulty = difficultyOption
                                    difficultyExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isCreating
                    ) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = {
                            val course = Course(
                                id = "", // 将由数据库生成
                                title = title.trim(),
                                description = description.trim(),
                                category = category,
                                difficulty = difficulty,
                                teacherId = "", // 将由UseCase设置
                                isPublished = false,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                            onCreateCourse(course)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isCreating && title.isNotBlank() && description.isNotBlank()
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("创建")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 编辑课程对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCourseDialog(
    course: Course,
    onDismiss: () -> Unit,
    onUpdateCourse: (Course) -> Unit,
    isUpdating: Boolean,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(course.title) }
    var description by remember { mutableStateOf(course.description) }
    var category by remember { mutableStateOf(course.category) }
    var difficulty by remember { mutableStateOf(course.difficulty) }
    var expanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }
    
    val categories = listOf("数学", "科学", "语言", "历史", "艺术", "技术")
    val difficulties = listOf("初级", "中级", "高级", "专家")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "编辑课程",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // 课程标题
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("课程标题") },
                    placeholder = { Text("输入课程标题") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 课程描述
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("课程描述") },
                    placeholder = { Text("输入课程描述") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 课程类别
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("课程类别") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { categoryOption ->
                            DropdownMenuItem(
                                text = { Text(categoryOption) },
                                onClick = {
                                    category = categoryOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // 课程难度
                ExposedDropdownMenuBox(
                    expanded = difficultyExpanded,
                    onExpandedChange = { difficultyExpanded = !difficultyExpanded }
                ) {
                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("课程难度") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = difficultyExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = difficultyExpanded,
                        onDismissRequest = { difficultyExpanded = false }
                    ) {
                        difficulties.forEach { difficultyOption ->
                            DropdownMenuItem(
                                text = { Text(difficultyOption) },
                                onClick = {
                                    difficulty = difficultyOption
                                    difficultyExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating
                    ) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = {
                            val updatedCourse = course.copy(
                                title = title.trim(),
                                description = description.trim(),
                                category = category,
                                difficulty = difficulty,
                                updatedAt = System.currentTimeMillis()
                            )
                            onUpdateCourse(updatedCourse)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating && title.isNotBlank() && description.isNotBlank()
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("保存")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 课程详情对话框
 */
@Composable
fun CourseDetailDialog(
    courseDetail: TeacherCourseDetail,
    onDismiss: () -> Unit,
    onEditCourse: (Course) -> Unit,
    onAddChapter: (String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val utils = TeacherCourseManagementUtils
    val course = courseDetail.course
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 标题栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "课程详情",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭"
                            )
                        }
                    }
                    
                    Divider()
                    
                    // 内容
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 基本信息
                        item {
                            CourseBasicInfoCard(
                                course = course,
                                courseDetail = courseDetail,
                                utils = utils
                            )
                        }
                        
                        // 统计信息
                        item {
                            CourseStatisticsCard(
                                courseDetail = courseDetail,
                                utils = utils
                            )
                        }
                        
                        // 章节列表
                        item {
                            ChapterListCard(
                                chapters = courseDetail.chapters,
                                onAddChapter = { onAddChapter(course.id) }
                            )
                        }
                        
                        // 健康度评分
                        item {
                            CourseHealthCard(
                                courseDetail = courseDetail,
                                utils = utils
                            )
                        }
                        
                        // 改进建议
                        item {
                            ImprovementSuggestionsCard(
                                suggestions = utils.getImprovementSuggestions(courseDetail)
                            )
                        }
                    }
                    
                    Divider()
                    
                    // 底部操作按钮
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onEditCourse(course) },
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
                        
                        Button(
                            onClick = { onAddChapter(course.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("添加章节")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 课程基本信息卡片
 */
@Composable
private fun CourseBasicInfoCard(
    course: Course,
    courseDetail: TeacherCourseDetail,
    utils: TeacherCourseManagementUtils,
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
                text = "基本信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 课程标题
            Row {
                Text(
                    text = "标题：",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // 课程描述
            Row {
                Text(
                    text = "描述：",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 标签
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "标签：",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(80.dp)
                )
                
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
                
                AssistChip(
                    onClick = { },
                    label = { Text(utils.getCourseStatusText(courseDetail)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = utils.getCourseStatusColor(courseDetail).copy(alpha = 0.1f),
                        labelColor = utils.getCourseStatusColor(courseDetail)
                    )
                )
            }
            
            // 时间信息
            Row {
                Text(
                    text = "创建：",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = utils.formatDateTime(course.createdAt),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Row {
                Text(
                    text = "更新：",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = utils.formatDateTime(course.updatedAt),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * 课程统计信息卡片
 */
@Composable
private fun CourseStatisticsCard(
    courseDetail: TeacherCourseDetail,
    utils: TeacherCourseManagementUtils,
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
                text = "统计信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.People,
                    label = "总学生",
                    value = courseDetail.totalStudents.toString()
                )
                
                StatisticItem(
                    icon = Icons.Default.PersonAdd,
                    label = "活跃学生",
                    value = courseDetail.activeStudents.toString()
                )
                
                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    label = "完成学生",
                    value = courseDetail.completedStudents.toString()
                )
                
                StatisticItem(
                    icon = Icons.Default.TrendingUp,
                    label = "平均进度",
                    value = utils.formatProgress(courseDetail.averageProgress)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.Schedule,
                    label = "平均学习时长",
                    value = utils.formatLearningTime(courseDetail.averageLearningTime)
                )
                
                StatisticItem(
                    icon = Icons.Default.Assignment,
                    label = "待批改评估",
                    value = courseDetail.pendingAssessments.toString()
                )
                
                StatisticItem(
                    icon = Icons.Default.MenuBook,
                    label = "章节数",
                    value = courseDetail.chapters.size.toString()
                )
                
                StatisticItem(
                    icon = Icons.Default.Quiz,
                    label = "评估数",
                    value = courseDetail.assessments.size.toString()
                )
            }
        }
    }
}

/**
 * 章节列表卡片
 */
@Composable
private fun ChapterListCard(
    chapters: List<Chapter>,
    onAddChapter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "章节列表 (${chapters.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onAddChapter) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("添加章节")
                }
            }
            
            if (chapters.isEmpty()) {
                Text(
                    text = "还没有章节，点击上方按钮添加第一个章节",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                chapters.forEachIndexed { index, chapter ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(32.dp)
                        )
                        
                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(
                            onClick = { /* 编辑章节 */ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "编辑章节",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    if (index < chapters.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

/**
 * 课程健康度卡片
 */
@Composable
private fun CourseHealthCard(
    courseDetail: TeacherCourseDetail,
    utils: TeacherCourseManagementUtils,
    modifier: Modifier = Modifier
) {
    val healthScore = utils.getCourseHealthScore(courseDetail)
    val healthGrade = utils.getHealthGrade(healthScore)
    val healthColor = utils.getHealthColor(healthScore)
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "课程健康度",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$healthScore 分",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = healthColor
                    )
                    
                    Text(
                        text = healthGrade,
                        style = MaterialTheme.typography.bodyLarge,
                        color = healthColor
                    )
                }
                
                CircularProgressIndicator(
                    progress = healthScore / 100f,
                    modifier = Modifier.size(64.dp),
                    color = healthColor,
                    strokeWidth = 6.dp
                )
            }
            
            LinearProgressIndicator(
                progress = healthScore / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = healthColor
            )
        }
    }
}

/**
 * 改进建议卡片
 */
@Composable
private fun ImprovementSuggestionsCard(
    suggestions: List<String>,
    modifier: Modifier = Modifier
) {
    if (suggestions.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "改进建议",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                suggestions.forEach { suggestion ->
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.LightbulbOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 学生列表对话框
 */
@Composable
fun StudentListDialog(
    students: List<StudentProgress>,
    onDismiss: () -> Unit,
    onRemoveStudent: (String, String) -> Unit,
    isLoading: Boolean,
    isRemoving: Boolean,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 标题栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "学生列表 (${students.size})",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }
                
                Divider()
                
                // 学生列表
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (students.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = "暂无学生",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = students,
                            key = { it.studentId }
                        ) { student ->
                            StudentProgressItem(
                                studentProgress = student,
                                onRemoveStudent = onRemoveStudent,
                                isRemoving = isRemoving
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 学生进度项
 */
@Composable
private fun StudentProgressItem(
    studentProgress: StudentProgress,
    onRemoveStudent: (String, String) -> Unit,
    isRemoving: Boolean,
    modifier: Modifier = Modifier
) {
    val utils = TeacherCourseManagementUtils
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = studentProgress.studentName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = utils.generateStudentProgressSummary(studentProgress),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { },
                        label = { Text(utils.getStudentStatusText(studentProgress)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = utils.getStudentStatusColor(studentProgress).copy(alpha = 0.1f),
                            labelColor = utils.getStudentStatusColor(studentProgress)
                        )
                    )
                    
                    if (studentProgress.lastAccessTime != null) {
                        Text(
                            text = "最后访问: ${utils.formatRelativeTime(studentProgress.lastAccessTime)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            IconButton(
                onClick = {
                    onRemoveStudent(studentProgress.courseId, studentProgress.studentId)
                },
                enabled = !isRemoving
            ) {
                if (isRemoving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.RemoveCircle,
                        contentDescription = "移除学生",
                        tint = MaterialTheme.colorScheme.error
                    )
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