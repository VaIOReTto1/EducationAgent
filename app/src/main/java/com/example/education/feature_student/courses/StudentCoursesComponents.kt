package com.example.education.feature_student.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * 学习统计卡片
 * 显示学生的学习统计信息
 */
@Composable
fun LearningStatisticsCard(
    statistics: LearningStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "学习统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.School,
                    value = statistics.enrolledCourses.toString(),
                    label = "已注册课程",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    value = statistics.completedCourses.toString(),
                    label = "已完成课程",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                StatisticItem(
                    icon = Icons.Default.Schedule,
                    value = StudentCoursesUtils.formatLearningTime(statistics.totalTimeSpent),
                    label = "学习时长",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                StatisticItem(
                    icon = Icons.Default.Star,
                    value = String.format("%.1f", statistics.averageScore),
                    label = "平均分数",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}

/**
 * 继续学习卡片
 * 显示可以继续学习的课程
 */
@Composable
fun ContinueLearningCard(
    courseWithProgress: CourseWithProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 课程标题
            Text(
                text = courseWithProgress.course.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            // 进度条
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "学习进度",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    
                    Text(
                        text = StudentCoursesUtils.formatProgress(courseWithProgress.averageProgress),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                LinearProgressIndicator(
                    progress = { courseWithProgress.averageProgress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = StudentCoursesUtils.getProgressColor(courseWithProgress.averageProgress),
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
            
            // 最后学习时间
            Text(
                text = "最后学习: ${StudentCoursesUtils.formatRelativeTime(courseWithProgress.lastAccessTime)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            
            // 继续学习按钮
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("继续学习")
            }
        }
    }
}

/**
 * 推荐课程卡片
 * 显示推荐给学生的课程
 */
@Composable
fun RecommendedCourseCard(
    courseWithProgress: CourseWithProgress,
    onClick: () -> Unit,
    onEnrollClick: () -> Unit,
    isEnrolling: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 推荐标签
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Recommend,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "推荐",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // 课程标题
            Text(
                text = courseWithProgress.course.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // 课程描述
            Text(
                text = courseWithProgress.course.description,
                style = MaterialTheme.typography.bodySmall,
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
                    label = {
                        Text(
                            text = StudentCoursesUtils.getDifficultyText(courseWithProgress.course.difficulty),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = StudentCoursesUtils.getDifficultyColor(courseWithProgress.course.difficulty).copy(alpha = 0.2f),
                        labelColor = StudentCoursesUtils.getDifficultyColor(courseWithProgress.course.difficulty)
                    )
                )
                
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = courseWithProgress.course.category,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = StudentCoursesUtils.getCategoryColor(courseWithProgress.course.category).copy(alpha = 0.2f),
                        labelColor = StudentCoursesUtils.getCategoryColor(courseWithProgress.course.category)
                    )
                )
            }
            
            // 推荐理由
            Text(
                text = StudentCoursesUtils.getRecommendationReason(courseWithProgress.course),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            
            // 注册按钮
            if (!courseWithProgress.isEnrolled) {
                Button(
                    onClick = onEnrollClick,
                    enabled = !isEnrolling,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isEnrolling) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("注册中...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("注册课程")
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("查看详情")
                }
            }
        }
    }
}

/**
 * 课程列表项
 * 显示课程列表中的单个课程
 */
@Composable
fun CourseListItem(
    courseWithProgress: CourseWithProgress,
    onClick: () -> Unit,
    onEnrollClick: () -> Unit,
    onContinueClick: (String) -> Unit,
    isEnrolling: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 课程标题和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = courseWithProgress.course.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = courseWithProgress.course.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // 学习状态标签
                Box(
                    modifier = Modifier
                        .background(
                            color = StudentCoursesUtils.getLearningStatusColor(courseWithProgress).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = StudentCoursesUtils.getLearningStatusText(courseWithProgress),
                        style = MaterialTheme.typography.labelSmall,
                        color = StudentCoursesUtils.getLearningStatusColor(courseWithProgress),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 课程标签
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudentCoursesUtils.getCourseTags(courseWithProgress.course).take(3).forEach { tag ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
            
            // 进度信息（仅已注册课程显示）
            if (courseWithProgress.isEnrolled) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${courseWithProgress.completedChapters}/${courseWithProgress.totalChapters} 章节",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = StudentCoursesUtils.formatProgress(courseWithProgress.averageProgress),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = { courseWithProgress.averageProgress / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = StudentCoursesUtils.getProgressColor(courseWithProgress.averageProgress),
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    
                    Text(
                        text = "学习时长: ${StudentCoursesUtils.formatLearningTime(courseWithProgress.totalTimeSpent)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!courseWithProgress.isEnrolled) {
                    Button(
                        onClick = onEnrollClick,
                        enabled = !isEnrolling,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isEnrolling) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("注册中...")
                        } else {
                            Text("注册课程")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("查看详情")
                    }
                } else {
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("课程详情")
                    }
                    
                    if (courseWithProgress.averageProgress > 0f && !courseWithProgress.isCompleted()) {
                        Button(
                            onClick = { 
                                // TODO: 获取下一个要学习的章节ID
                                onContinueClick("next_chapter_id")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("继续学习")
                        }
                    } else if (courseWithProgress.averageProgress == 0f) {
                        Button(
                            onClick = {
                                // TODO: 获取第一个章节ID
                                onContinueClick("first_chapter_id")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("开始学习")
                        }
                    }
                }
            }
        }
    }
}