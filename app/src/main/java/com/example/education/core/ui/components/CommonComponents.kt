package com.example.education.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.education.core.ui.theme.*

/**
 * 加载指示器组件
 * @param modifier 修饰符
 * @param message 加载消息
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "加载中..."
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 错误状态组件
 * @param modifier 修饰符
 * @param message 错误消息
 * @param onRetry 重试回调
 */
@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    message: String = "加载失败",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "错误",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("重试")
            }
        }
    }
}

/**
 * 空状态组件
 * @param modifier 修饰符
 * @param message 空状态消息
 * @param icon 图标
 * @param actionText 操作按钮文字
 * @param onAction 操作回调
 */
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    message: String = "暂无数据",
    icon: ImageVector = Icons.Default.Inbox,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "空状态",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}

/**
 * 进度卡片组件
 * @param modifier 修饰符
 * @param title 标题
 * @param progress 进度（0.0-1.0）
 * @param progressText 进度文字
 */
@Composable
fun ProgressCard(
    modifier: Modifier = Modifier,
    title: String,
    progress: Float,
    progressText: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = EducationShapes.cardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(EducationShapes.progressShape),
                color = when {
                    progress >= 1.0f -> ProgressCompleted
                    progress > 0.0f -> ProgressInProgress
                    else -> ProgressNotStarted
                }
            )
            if (progressText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = progressText,
                    style = EducationTypography.progressText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 难度标签组件
 * @param modifier 修饰符
 * @param difficulty 难度级别
 */
@Composable
fun DifficultyChip(
    modifier: Modifier = Modifier,
    difficulty: String
) {
    val (backgroundColor, textColor) = when (difficulty.lowercase()) {
        "beginner", "初级" -> BeginnerColor to Color.White
        "intermediate", "中级" -> IntermediateColor to Color.White
        "advanced", "高级" -> AdvancedColor to Color.White
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        modifier = modifier,
        shape = EducationShapes.chipShape,
        color = backgroundColor
    ) {
        Text(
            text = difficulty,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 评分显示组件
 * @param modifier 修饰符
 * @param score 分数
 * @param totalScore 总分
 * @param showPercentage 是否显示百分比
 */
@Composable
fun ScoreDisplay(
    modifier: Modifier = Modifier,
    score: Int,
    totalScore: Int,
    showPercentage: Boolean = true
) {
    val percentage = if (totalScore > 0) (score.toFloat() / totalScore * 100).toInt() else 0
    val scoreColor = when {
        percentage >= 90 -> ScoreExcellent
        percentage >= 80 -> ScoreGood
        percentage >= 60 -> ScoreAverage
        else -> ScorePoor
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$score/$totalScore",
            style = EducationTypography.scoreText,
            color = scoreColor
        )
        if (showPercentage) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "($percentage%)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 用户头像组件
 * @param modifier 修饰符
 * @param name 用户名
 * @param avatarUrl 头像URL（可选）
 * @param size 头像大小
 * @param onClick 点击回调
 */
@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    name: String,
    avatarUrl: String? = null,
    size: Int = 40,
    onClick: (() -> Unit)? = null
) {
    val initials = name.take(2).uppercase()
    
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null) {
            // TODO: 使用图片加载库（如Coil）加载头像
            // AsyncImage(
            //     model = avatarUrl,
            //     contentDescription = "用户头像",
            //     modifier = Modifier.fillMaxSize()
            // )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 时间戳显示组件
 * @param modifier 修饰符
 * @param timestamp 时间戳（毫秒）
 */
@Composable
fun TimestampText(
    modifier: Modifier = Modifier,
    timestamp: Long
) {
    val timeText = remember(timestamp) {
        // TODO: 实现时间格式化逻辑
        // 可以使用 DateUtils.getRelativeTimeSpanString 或自定义格式化
        "刚刚" // 临时显示
    }
    
    Text(
        text = timeText,
        modifier = modifier,
        style = EducationTypography.timestamp,
        color = MessageTimestamp
    )
}

/**
 * 课程类别标签组件
 * @param modifier 修饰符
 * @param subject 学科
 */
@Composable
fun SubjectChip(
    modifier: Modifier = Modifier,
    subject: String
) {
    val backgroundColor = when (subject.lowercase()) {
        "数学", "math" -> MathColor
        "科学", "science" -> ScienceColor
        "语言", "language" -> LanguageColor
        "历史", "history" -> HistoryColor
        "艺术", "art" -> ArtColor
        "音乐", "music" -> MusicColor
        "体育", "physical" -> PhysicalColor
        "技术", "technology" -> TechnologyColor
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Surface(
        modifier = modifier,
        shape = EducationShapes.chipShape,
        color = backgroundColor
    ) {
        Text(
            text = subject,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}