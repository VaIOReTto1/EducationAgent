package com.example.education.feature_student.quiz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.education.core.common_ui.theme.EducationTheme
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val allQuestionsAnswered = uiState.answeredQuestions == uiState.totalQuestions

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    EducationTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                QuizTopBar(
                    progress = uiState.progress,
                    onBack = { (context as? Activity)?.finish() }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ——— 头部：统计与操作按钮 ———
                item {
                    QuizStatsAndActions(
                        modifier = Modifier.fillMaxWidth(),
                        uiState = uiState,
                        allQuestionsAnswered = allQuestionsAnswered,
                        isCompact = true,
                        onSubmit = {
                            viewModel.submitQuiz()
                            showSnackbar("测验已提交！")
                        },
                        onReset = {
                            viewModel.resetQuiz()
                            showSnackbar("测验已重置")
                        },
                        onClear = {
                            viewModel.clearSelection()
                            showSnackbar("选项已清空")
                        },
                        onToggleExplanations = { viewModel.toggleExplanations() },
                        onShare = {
                            shareResult(context, uiState.score, uiState.totalQuestions)
                        }
                    )
                }

                // ——— 列表项：题目卡片 ———
                itemsIndexed(
                    items = uiState.questions,
                    key = { _, question -> question.id }
                ) { index, question ->
                    QuestionCard(
                        questionIndex = index,
                        question = question,
                        selectedOption = uiState.selectedAnswers[index],
                        isSubmitted = uiState.isSubmitted,
                        showExplanation = uiState.showExplanations,
                        onOptionSelected = { optionIndex ->
                            viewModel.selectAnswer(index, optionIndex)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizTopBar(progress: Float, onBack: () -> Unit) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500), label = ""
    )
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        TopAppBar(
            title = { Text("课堂测验: 嵌入式Linux") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun QuestionList(
    modifier: Modifier = Modifier,
    uiState: QuizUiState,
    onAnswerSelected: (Int, Int) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(uiState.questions, key = { _, q -> q.id }) { index, question ->
            QuestionCard(
                questionIndex = index,
                question = question,
                selectedOption = uiState.selectedAnswers[index],
                isSubmitted = uiState.isSubmitted,
                showExplanation = uiState.showExplanations,
                onOptionSelected = { optionIndex ->
                    onAnswerSelected(index, optionIndex)
                }
            )
        }
    }
}

@Composable
private fun QuizStatsAndActions(
    modifier: Modifier = Modifier,
    uiState: QuizUiState,
    allQuestionsAnswered: Boolean,
    isCompact: Boolean = false,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    onClear: () -> Unit,
    onToggleExplanations: () -> Unit,
    onShare: () -> Unit
) {
    val score = uiState.score
    val total = uiState.totalQuestions
    val correctRate = if (total > 0) score.toFloat() / total else 0f

    // 由原先的 LazyColumn 改为 Column，确保内部不再是一个独立滚动容器
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isSubmitted) {
                    Text("✨ 最终得分 ✨", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$score / $total",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text("当前进度", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${uiState.answeredQuestions} / $total",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (uiState.isSubmitted) {
            CorrectnessDonutChart(
                correctRate = correctRate,
                modifier = Modifier.size(if (isCompact) 120.dp else 180.dp)
            )
        }

        DifficultyBarChart(
            questions = uiState.questions,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCompact) 80.dp else 120.dp)
        )

        Spacer(Modifier.height(8.dp))

        if (uiState.isSubmitted) {
            ActionButton(
                "重做测验", Icons.Default.Refresh, onReset,
                containerColor = MaterialTheme.colorScheme.primary
            )
        } else {
            Button(
                onClick = onSubmit,
                enabled = allQuestionsAnswered,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "提交测验")
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("提交测验")
            }
        }

        ActionButton(
            text = if (uiState.showExplanations) "隐藏解析" else "查看解析",
            icon = if (uiState.showExplanations) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            onClick = onToggleExplanations,
            enabled = uiState.isSubmitted
        )

        ActionButton(
            "清空选择", Icons.Default.ClearAll, onClear,
            enabled = !uiState.isSubmitted && uiState.answeredQuestions > 0
        )

        ActionButton(
            "分享结果", Icons.Default.Share, onShare,
            enabled = uiState.isSubmitted
        )
    }
}


@Composable
private fun QuestionCard(
    questionIndex: Int,
    question: QuizQuestion,
    selectedOption: Int?,
    isSubmitted: Boolean,
    showExplanation: Boolean,
    onOptionSelected: (Int) -> Unit
) {
    val cardColor = if (isSubmitted) {
        if (selectedOption == question.correctAnswerIndex) {
            Color(0xFFE8F5E9) // Light Green
        } else {
            Color(0xFFFFEBEE) // Light Red
        }
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${questionIndex + 1}. ${question.question}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                DifficultyIndicator(
                    difficulty = question.difficulty,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                question.tags.forEach { tag ->
                    Tag(
                        text = tag,
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            question.options.forEachIndexed { optionIndex, optionText ->
                val isSelected = selectedOption == optionIndex
                val isCorrect = question.correctAnswerIndex == optionIndex
                val answerColor = when {
                    isSubmitted && isCorrect -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(
                        alpha = 0.3f
                    )

                    else -> Color.Transparent
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(answerColor)
                        .clickable(enabled = !isSubmitted) { onOptionSelected(optionIndex) }
                        .padding(12.dp)
                        .semantics { role = Role.RadioButton }
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { if (!isSubmitted) onOptionSelected(optionIndex) },
                        enabled = !isSubmitted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = optionText, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            AnimatedVisibility(visible = isSubmitted && showExplanation) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "解析: ${question.explanation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor.copy(alpha = 0.3f))
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text)
    }
}


@Composable
private fun CorrectnessDonutChart(correctRate: Float, modifier: Modifier = Modifier) {
    val animatedRate by animateFloatAsState(
        targetValue = correctRate,
        animationSpec = tween(1000),
        label = ""
    )
    val correctColor = MaterialTheme.colorScheme.primary
    val incorrectColor = MaterialTheme.colorScheme.error
    val backgroundTrackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.width * 0.15f
            drawArc(
                color = backgroundTrackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = correctColor,
                startAngle = -90f,
                sweepAngle = 360 * animatedRate,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(correctRate * 100).toInt()}%",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
private fun DifficultyBarChart(questions: List<QuizQuestion>, modifier: Modifier = Modifier) {
    if (questions.isEmpty()) return

    val difficultyCounts = questions.groupingBy { it.difficulty }.eachCount()
    val maxCount = difficultyCounts.values.maxOrNull() ?: 1
    val difficulties = 1..3
    val medianDifficulty = questions.map { it.difficulty }.sorted().let {
        if (it.isEmpty()) 0 else it[it.size / 2]
    }

    val easyColor = MaterialTheme.colorScheme.tertiary
    val mediumColor = MaterialTheme.colorScheme.primary
    val hardColor = MaterialTheme.colorScheme.error

    Canvas(modifier = modifier.padding(vertical = 8.dp)) {
        val barWidth = size.width / (difficulties.count() * 2)
        difficulties.forEachIndexed { index, difficulty ->
            val count = difficultyCounts[difficulty] ?: 0
            val barHeight = (count.toFloat() / maxCount) * size.height
            val isMedian = difficulty == medianDifficulty

            drawRect(
                color = when (difficulty) {
                    1 -> easyColor
                    2 -> mediumColor
                    else -> hardColor
                },
                topLeft = Offset(x = barWidth * (index * 2 + 0.5f), y = size.height - barHeight),
                size = Size(barWidth, barHeight)
            )
            if (isMedian) {
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = Offset(x = barWidth * (index * 2 + 0.5f), y = 0f),
                    size = Size(barWidth, size.height),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun DifficultyIndicator(difficulty: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        (1..3).forEach { level ->
            Box(
                modifier = Modifier
                    .size(8.dp, 12.dp + (level * 4).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (level <= difficulty) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}


private fun shareResult(context: Context, score: Int, total: Int) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "我刚刚完成了嵌入式Linux课堂测验，得到了 $score / $total 分！你也来试试吧！"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}