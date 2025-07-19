package com.example.education.feature_teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class LessonPlan(
    val id: String,
    val title: String,
    val subject: String,
    val grade: String,
    val duration: String,
    val status: String,
    val progress: Float,
    val lastModified: String
)

data class TeachingResource(
    val id: String,
    val title: String,
    val type: String,
    val subject: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isRecommended: Boolean
)

data class LessonTemplate(
    val id: String,
    val name: String,
    val description: String,
    val subject: String,
    val difficulty: String,
    val estimatedTime: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class AITool(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isActive: Boolean
)

/**
 * 教师AI备课页面 - 丰富的UI展示
 * 参考知识库和智能评估页面的设计风格
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAILessonPlanScreen(
    navController: NavController
) {
    // 模拟数据
    val recentLessonPlans = remember {
        listOf(
            LessonPlan("plan1", "小学数学-分数运算", "数学", "四年级", "45分钟", "进行中", 0.75f, "2小时前"),
            LessonPlan("plan2", "初中物理-光的反射", "物理", "八年级", "40分钟", "已完成", 1.0f, "昨天"),
            LessonPlan("plan3", "高中化学-有机化合物", "化学", "高二", "50分钟", "草稿", 0.3f, "3天前")
        )
    }
    
    val teachingResources = remember {
        listOf(
            TeachingResource("res1", "互动式数学课件", "课件", "数学", "包含动画演示和练习题", Icons.Default.Slideshow, true),
            TeachingResource("res2", "物理实验视频库", "视频", "物理", "高清实验演示视频", Icons.Default.VideoLibrary, true),
            TeachingResource("res3", "化学分子模型", "模型", "化学", "3D分子结构展示", Icons.Default.ViewInAr, false),
            TeachingResource("res4", "英语听力材料", "音频", "英语", "标准发音听力练习", Icons.Default.AudioFile, true),
            TeachingResource("res5", "历史时间轴", "图表", "历史", "重要历史事件时间线", Icons.Default.Timeline, false),
            TeachingResource("res6", "地理地图集", "图片", "地理", "高分辨率地理图片", Icons.Default.Map, false)
        )
    }
    
    val lessonTemplates = remember {
        listOf(
            LessonTemplate("temp1", "概念导入型", "适合新概念教学", "通用", "基础", "40分钟", Icons.Default.Lightbulb),
            LessonTemplate("temp2", "实验探究型", "适合科学实验课", "理科", "中等", "45分钟", Icons.Default.Science),
            LessonTemplate("temp3", "讨论互动型", "适合小组讨论", "文科", "中等", "35分钟", Icons.Default.Groups),
            LessonTemplate("temp4", "练习巩固型", "适合复习课", "通用", "基础", "30分钟", Icons.Default.Assignment),
            LessonTemplate("temp5", "项目制学习", "适合综合实践", "通用", "高级", "90分钟", Icons.Default.Engineering),
            LessonTemplate("temp6", "翻转课堂型", "适合自主学习", "通用", "高级", "50分钟", Icons.Default.FlipToFront)
        )
    }
    
    val aiTools = remember {
        listOf(
            AITool("tool1", "智能课件生成", "自动生成PPT课件", "内容创作", Icons.Default.AutoAwesome, true),
            AITool("tool2", "题目自动生成", "根据知识点生成练习题", "评估工具", Icons.Default.Quiz, true),
            AITool("tool3", "教学策略建议", "个性化教学方法推荐", "教学指导", Icons.Default.Psychology, false),
            AITool("tool4", "学情数据分析", "学生学习情况分析", "数据分析", Icons.Default.Analytics, true)
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI智能备课",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "让AI助力您的教学设计",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 搜索 */ }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = { /* 帮助 */ }) {
                        Icon(Icons.Default.Help, contentDescription = "帮助")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* 新建课程计划 */ },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("新建课程") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 备课统计概览
            item {
                LessonPlanStatsCard()
            }
            
            // AI工具箱
            item {
                AIToolboxCard(
                    tools = aiTools,
                    onToolClick = { tool ->
                        // 处理AI工具点击
                    }
                )
            }
            
            // 快速开始
            item {
                QuickStartCard(
                    onCreateFromTemplate = { /* 从模板创建 */ },
                    onCreateFromScratch = { /* 从头创建 */ },
                    onImportContent = { /* 导入内容 */ }
                )
            }
            
            // 课程模板
            item {
                Text(
                    text = "课程模板",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(600.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lessonTemplates) { template ->
                        LessonTemplateCard(
                            template = template,
                            onClick = {
                                // 使用模板创建课程
                            }
                        )
                    }
                }
            }
            
            // 最近的课程计划
            item {
                RecentLessonPlansCard(
                    lessonPlans = recentLessonPlans,
                    onViewAll = { /* 查看全部 */ },
                    onEditPlan = { plan ->
                        // 编辑课程计划
                    }
                )
            }
            
            // 推荐教学资源
            item {
                RecommendedResourcesCard(
                    resources = teachingResources.filter { it.isRecommended },
                    onViewAll = { /* 查看全部资源 */ },
                    onUseResource = { resource ->
                        // 使用教学资源
                    }
                )
            }
            
            // AI备课建议
            item {
                AIRecommendationCard(
                    onAcceptSuggestion = {
                        // 接受AI建议
                    }
                )
            }
        }
    }
}

@Composable
private fun LessonPlanStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "备课统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsItem("28", "课程计划", Icons.Default.School)
                StatsItem("156", "教学资源", Icons.Default.LibraryBooks)
                StatsItem("12", "本周备课", Icons.Default.CalendarToday)
                StatsItem("4.9", "学生评分", Icons.Default.Star)
            }
        }
    }
}

@Composable
private fun StatsItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AIToolboxCard(
    tools: List<AITool>,
    onToolClick: (AITool) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI工具箱",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tools) { tool ->
                    AIToolCard(
                        tool = tool,
                        onClick = { onToolClick(tool) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AIToolCard(
    tool: AITool,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tool.isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (tool.isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tool.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = tool.category,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun QuickStartCard(
    onCreateFromTemplate: () -> Unit,
    onCreateFromScratch: () -> Unit,
    onImportContent: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快速开始",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCreateFromTemplate,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("使用模板", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                OutlinedButton(
                    onClick = onCreateFromScratch,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Create, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("从头创建", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                OutlinedButton(
                    onClick = onImportContent,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("导入内容", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonTemplateCard(
    template: LessonTemplate,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = template.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = template.difficulty,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.height(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = template.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = template.subject,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = template.estimatedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecentLessonPlansCard(
    lessonPlans: List<LessonPlan>,
    onViewAll: () -> Unit,
    onEditPlan: (LessonPlan) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "最近的课程计划",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("查看全部")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            lessonPlans.forEach { plan ->
                LessonPlanItem(
                    plan = plan,
                    onEdit = { onEditPlan(plan) }
                )
                if (plan != lessonPlans.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun LessonPlanItem(
    plan: LessonPlan,
    onEdit: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${plan.subject} · ${plan.grade} · ${plan.duration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "最后修改: ${plan.lastModified}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = plan.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (plan.status) {
                        "已完成" -> MaterialTheme.colorScheme.primary
                        "进行中" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.outline
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                TextButton(onClick = onEdit) {
                    Text("编辑")
                }
            }
        }
        
        if (plan.progress > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = plan.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RecommendedResourcesCard(
    resources: List<TeachingResource>,
    onViewAll: () -> Unit,
    onUseResource: (TeachingResource) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "推荐教学资源",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("查看全部")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(resources) { resource ->
                    ResourceCard(
                        resource = resource,
                        onClick = { onUseResource(resource) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResourceCard(
    resource: TeachingResource,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(180.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = resource.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                if (resource.isRecommended) {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = "推荐",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.height(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
            
            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = resource.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Text(
                    text = resource.subject,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AIRecommendationCard(
    onAcceptSuggestion: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI备课建议",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "基于您的教学历史和学生反馈，AI建议您在下节数学课中增加更多互动环节，并使用可视化教具来解释分数概念。系统已为您准备了相关的教学资源和活动设计。",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* 稍后查看 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("稍后查看")
                }
                
                Button(
                    onClick = onAcceptSuggestion,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("应用建议")
                }
            }
        }
    }
}