package com.example.education.feature_teacher.course_content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.education.core.database.entity.*

/**
 * 课程内容管理对话框集合
 */
@Composable
fun CourseContentDialogs(
    uiState: CourseContentUiState,
    onIntent: (CourseContentIntent) -> Unit
) {
    // 创建章节对话框
    if (uiState.showCreateChapterDialog) {
        CreateChapterDialog(
            onDismiss = {
                onIntent(CourseContentIntent.HideCreateChapterDialog)
            },
            onConfirm = { title, content, description ->
                onIntent(
                    CourseContentIntent.CreateChapter(
                        title = title,
                        content = content,
                        description = description
                    )
                )
            }
        )
    }
    
    // 编辑章节对话框
    if (uiState.showEditChapterDialog && uiState.selectedChapter != null) {
        EditChapterDialog(
            chapter = uiState.selectedChapter,
            onDismiss = {
                onIntent(CourseContentIntent.HideEditChapterDialog)
            },
            onConfirm = { title, content, description ->
                onIntent(
                    CourseContentIntent.UpdateChapter(
                        chapterId = uiState.selectedChapter.id,
                        title = title,
                        content = content,
                        description = description
                    )
                )
            }
        )
    }
    
    // 创建评估对话框
    if (uiState.showCreateAssessmentDialog) {
        CreateAssessmentDialog(
            chapterId = uiState.selectedChapterId,
            onDismiss = {
                onIntent(CourseContentIntent.HideCreateAssessmentDialog)
            },
            onConfirm = { title, description, type, questions, timeLimit, passingScore ->
                onIntent(
                    CourseContentIntent.CreateAssessment(
                        chapterId = uiState.selectedChapterId,
                        title = title,
                        description = description,
                        type = type,
                        questions = questions,
                        timeLimit = timeLimit,
                        passingScore = passingScore
                    )
                )
            }
        )
    }
    
    // 编辑评估对话框
    if (uiState.showEditAssessmentDialog && uiState.selectedAssessment != null) {
        EditAssessmentDialog(
            assessment = uiState.selectedAssessment,
            onDismiss = {
                onIntent(CourseContentIntent.HideEditAssessmentDialog)
            },
            onConfirm = { title, description, questions, timeLimit, passingScore ->
                onIntent(
                    CourseContentIntent.UpdateAssessment(
                        assessmentId = uiState.selectedAssessment.id,
                        title = title,
                        description = description,
                        questions = questions,
                        timeLimit = timeLimit,
                        passingScore = passingScore
                    )
                )
            }
        )
    }
    
    // 删除确认对话框
    if (uiState.showDeleteConfirmDialog) {
        DeleteConfirmDialog(
            itemType = uiState.deleteItemType ?: "",
            itemTitle = uiState.deleteItemTitle ?: "",
            onDismiss = {
                onIntent(CourseContentIntent.HideDeleteConfirmDialog)
            },
            onConfirm = {
                val itemId = uiState.deleteItemId ?: return@DeleteConfirmDialog
                val itemType = uiState.deleteItemType ?: return@DeleteConfirmDialog
                
                when (itemType) {
                    "章节" -> onIntent(CourseContentIntent.DeleteChapter(itemId))
                    "评估" -> onIntent(CourseContentIntent.DeleteAssessment(itemId))
                }
            }
        )
    }
    
    // 导入对话框
    if (uiState.showImportDialog) {
        ImportContentDialog(
            onDismiss = {
                onIntent(CourseContentIntent.HideImportDialog)
            },
            onConfirm = { importData ->
                onIntent(CourseContentIntent.ImportContent(importData))
            }
        )
    }
    
    // 导出对话框
    if (uiState.showExportDialog) {
        ExportContentDialog(
            exportData = uiState.exportData,
            onDismiss = {
                onIntent(CourseContentIntent.HideExportDialog)
            }
        )
    }
    
    // 统计信息对话框
    if (uiState.showStatisticsDialog) {
        StatisticsDialog(
            statistics = uiState.statistics,
            onDismiss = {
                onIntent(CourseContentIntent.HideStatisticsDialog)
            }
        )
    }
}

/**
 * 创建章节对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChapterDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    val isValid = title.isNotBlank() && content.isNotBlank()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "创建章节",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("章节标题") },
                    placeholder = { Text("请输入章节标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("章节描述") },
                    placeholder = { Text("请输入章节描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("章节内容") },
                    placeholder = { Text("请输入章节内容") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
                
                // 内容统计
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "字数: ${content.length}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "预计阅读: ${CourseContentUtils.formatLearningTime(content.length / 200)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(title, content, description)
                },
                enabled = isValid
            ) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 编辑章节对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChapterDialog(
    chapter: Chapter,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(chapter.title) }
    var content by remember { mutableStateOf(chapter.content) }
    var description by remember { mutableStateOf(chapter.description) }
    
    val isValid = title.isNotBlank() && content.isNotBlank()
    val hasChanges = title != chapter.title || 
                    content != chapter.content || 
                    description != chapter.description
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "编辑章节",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("章节标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("章节描述") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("章节内容") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
                
                // 内容统计
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "字数: ${content.length}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "预计阅读: ${CourseContentUtils.formatLearningTime(content.length / 200)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(title, content, description)
                },
                enabled = isValid && hasChanges
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 创建评估对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAssessmentDialog(
    chapterId: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, AssessmentType, List<AssessmentQuestion>, Int?, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(AssessmentType.QUIZ) }
    var questions by remember { mutableStateOf(listOf<AssessmentQuestion>()) }
    var timeLimit by remember { mutableStateOf("") }
    var passingScore by remember { mutableStateOf("60") }
    
    val isValid = title.isNotBlank() && questions.isNotEmpty() && 
                 passingScore.toIntOrNull()?.let { it in 0..100 } == true
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 标题
                Text(
                    text = if (chapterId != null) "创建章节评估" else "创建课程评估",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 内容区域
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("评估标题") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("评估描述") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                    
                    item {
                        // 评估类型选择
                        Column {
                            Text(
                                text = "评估类型",
                                style = MaterialTheme.typography.labelMedium
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AssessmentType.values().forEach { assessmentType ->
                                    FilterChip(
                                        selected = type == assessmentType,
                                        onClick = { type = assessmentType },
                                        label = {
                                            Text(CourseContentUtils.getAssessmentTypeText(assessmentType))
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = timeLimit,
                                onValueChange = { timeLimit = it },
                                label = { Text("时间限制（分钟）") },
                                placeholder = { Text("不限制") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = passingScore,
                                onValueChange = { passingScore = it },
                                label = { Text("及格分数（%）") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                    
                    item {
                        // 题目列表
                        QuestionListEditor(
                            questions = questions,
                            onQuestionsChange = { questions = it }
                        )
                    }
                }
                
                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = {
                            onConfirm(
                                title,
                                description,
                                type,
                                questions,
                                timeLimit.toIntOrNull(),
                                passingScore.toIntOrNull() ?: 60
                            )
                        },
                        enabled = isValid
                    ) {
                        Text("创建")
                    }
                }
            }
        }
    }
}

/**
 * 编辑评估对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAssessmentDialog(
    assessment: Assessment,
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<AssessmentQuestion>, Int?, Int) -> Unit
) {
    var title by remember { mutableStateOf(assessment.title) }
    var description by remember { mutableStateOf(assessment.description) }
    var questions by remember { mutableStateOf(assessment.questions) }
    var timeLimit by remember { mutableStateOf(assessment.timeLimit?.toString() ?: "") }
    var passingScore by remember { mutableStateOf(assessment.passingScore.toString()) }
    
    val isValid = title.isNotBlank() && questions.isNotEmpty() && 
                 passingScore.toIntOrNull()?.let { it in 0..100 } == true
    
    val hasChanges = title != assessment.title || 
                    description != assessment.description ||
                    questions != assessment.questions ||
                    timeLimit.toIntOrNull() != assessment.timeLimit ||
                    passingScore.toIntOrNull() != assessment.passingScore
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 标题
                Text(
                    text = "编辑评估",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 内容区域
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("评估标题") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("评估描述") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                    
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = timeLimit,
                                onValueChange = { timeLimit = it },
                                label = { Text("时间限制（分钟）") },
                                placeholder = { Text("不限制") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = passingScore,
                                onValueChange = { passingScore = it },
                                label = { Text("及格分数（%）") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                    
                    item {
                        // 题目列表
                        QuestionListEditor(
                            questions = questions,
                            onQuestionsChange = { questions = it }
                        )
                    }
                }
                
                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = {
                            onConfirm(
                                title,
                                description,
                                questions,
                                timeLimit.toIntOrNull(),
                                passingScore.toIntOrNull() ?: 60
                            )
                        },
                        enabled = isValid && hasChanges
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

/**
 * 题目列表编辑器
 */
@Composable
private fun QuestionListEditor(
    questions: List<AssessmentQuestion>,
    onQuestionsChange: (List<AssessmentQuestion>) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "题目列表 (${questions.size})",
                style = MaterialTheme.typography.labelMedium
            )
            
            TextButton(
                onClick = {
                    val newQuestion = AssessmentQuestion(
                        id = "temp_${System.currentTimeMillis()}",
                        question = "",
                        options = listOf("", "", "", ""),
                        correctAnswer = 0,
                        explanation = ""
                    )
                    onQuestionsChange(questions + newQuestion)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("添加题目")
            }
        }
        
        if (questions.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "还没有题目，点击上方按钮添加",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            questions.forEachIndexed { index, question ->
                QuestionEditor(
                    question = question,
                    index = index + 1,
                    onQuestionChange = { updatedQuestion ->
                        val updatedQuestions = questions.toMutableList()
                        updatedQuestions[index] = updatedQuestion
                        onQuestionsChange(updatedQuestions)
                    },
                    onDelete = {
                        onQuestionsChange(questions - question)
                    }
                )
            }
        }
    }
}

/**
 * 题目编辑器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionEditor(
    question: AssessmentQuestion,
    index: Int,
    onQuestionChange: (AssessmentQuestion) -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 题目标题和删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "题目 $index",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除题目",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // 题目内容
            OutlinedTextField(
                value = question.question,
                onValueChange = { 
                    onQuestionChange(question.copy(question = it))
                },
                label = { Text("题目内容") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            // 选项
            question.options.forEachIndexed { optionIndex, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = question.correctAnswer == optionIndex,
                        onClick = {
                            onQuestionChange(question.copy(correctAnswer = optionIndex))
                        }
                    )
                    
                    OutlinedTextField(
                        value = option,
                        onValueChange = { newOption ->
                            val newOptions = question.options.toMutableList()
                            newOptions[optionIndex] = newOption
                            onQuestionChange(question.copy(options = newOptions))
                        },
                        label = { Text("选项 ${('A' + optionIndex)}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
            
            // 解释
            OutlinedTextField(
                value = question.explanation,
                onValueChange = { 
                    onQuestionChange(question.copy(explanation = it))
                },
                label = { Text("答案解释（可选）") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
        }
    }
}

/**
 * 删除确认对话框
 */
@Composable
fun DeleteConfirmDialog(
    itemType: String,
    itemTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "删除$itemType",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "确定要删除$itemType \"$itemTitle\" 吗？\n\n此操作无法撤销。"
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 导入内容对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportContentDialog(
    onDismiss: () -> Unit,
    onConfirm: (CourseContentImportData) -> Unit
) {
    var importText by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "导入内容",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "请粘贴要导入的JSON格式内容：",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                OutlinedTextField(
                    value = importText,
                    onValueChange = { importText = it },
                    label = { Text("导入内容") },
                    placeholder = { Text("粘贴JSON内容...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 这里应该解析JSON并创建ImportData
                    // 简化处理，实际应该有JSON解析逻辑
                    try {
                        val importData = CourseContentImportData(
                            chapters = emptyList(),
                            assessments = emptyList()
                        )
                        onConfirm(importData)
                    } catch (e: Exception) {
                        // 处理解析错误
                    }
                },
                enabled = importText.isNotBlank()
            ) {
                Text("导入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 导出内容对话框
 */
@Composable
fun ExportContentDialog(
    exportData: CourseContentExportData?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "导出内容",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (exportData != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "导出成功！",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "导出统计：",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = "章节数：${exportData.chapters.size}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            Text(
                                text = "评估数：${exportData.assessments.size}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            Text(
                                text = "文件名：${CourseContentUtils.generateExportFileName(exportData.courseTitle)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                Text("正在准备导出数据...")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

/**
 * 统计信息对话框
 */
@Composable
fun StatisticsDialog(
    statistics: ContentStatistics?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "内容统计",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (statistics != null) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        StatisticsCard(
                            title = "基础统计",
                            items = listOf(
                                "总章节数" to statistics.totalChapters.toString(),
                                "总评估数" to statistics.totalAssessments.toString(),
                                "总字数" to CourseContentUtils.formatWordCount(statistics.totalWords),
                                "预计学习时长" to CourseContentUtils.formatLearningTime(statistics.estimatedDuration)
                            )
                        )
                    }
                    
                    item {
                        StatisticsCard(
                            title = "内容质量",
                            items = listOf(
                                "内容完整性" to "${CourseContentUtils.calculateContentCompletenessScore(statistics)}%",
                                "内容复杂度" to CourseContentUtils.getContentComplexityLevel(statistics),
                                "平均章节长度" to CourseContentUtils.formatWordCount(
                                    if (statistics.totalChapters > 0) statistics.totalWords / statistics.totalChapters else 0
                                )
                            )
                        )
                    }
                    
                    item {
                        StatisticsCard(
                            title = "改进建议",
                            items = CourseContentUtils.generateContentImprovementSuggestions(statistics)
                                .map { it to "" }
                        )
                    }
                }
            } else {
                Text("暂无统计数据")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

/**
 * 统计卡片
 */
@Composable
private fun StatisticsCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            items.forEach { (label, value) ->
                if (value.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "• $label",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}