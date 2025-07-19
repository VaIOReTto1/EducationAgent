package com.example.education.feature_knowledge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.hilt.navigation.compose.hiltViewModel

data class Course(
    val id: String,
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val knowledgePoints: Int
)

/**
 * 知识库页面
 * 显示课程列表，点击进入对应的AI聊天
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen(
    onCourseClick: (String) -> Unit,
    viewModel: KnowledgeBaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 预设课程数据
    val courses = remember {
        listOf(
            Course("linux", "Linux系统", "操作系统原理、命令行操作、系统管理", Icons.Default.Computer, 156),
            Course("ml", "机器学习", "算法原理、模型训练、深度学习", Icons.Default.Psychology, 203),
            Course("ds", "数据结构", "算法设计、数据存储、复杂度分析", Icons.Default.DataArray, 128),
            Course("db", "数据库系统", "SQL语法、数据库设计、事务处理", Icons.Default.Storage, 145),
            Course("network", "计算机网络", "网络协议、网络安全、网络编程", Icons.Default.NetworkCheck, 167),
            Course("java", "Java编程", "面向对象、JVM原理、Spring框架", Icons.Default.Code, 198),
            Course("python", "Python编程", "语法基础、数据分析、Web开发", Icons.Default.Terminal, 175),
            Course("algorithm", "算法设计", "排序算法、图论、动态规划", Icons.Default.AutoAwesome, 234)
        )
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部应用栏
        TopAppBar(
            title = { 
                Column {
                    Text("知识库管理")
                    Text(
                        text = "选择课程开始AI辅助学习",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* 搜索功能 */ }) {
                    Icon(Icons.Default.Search, contentDescription = "搜索课程")
                }
                IconButton(onClick = { viewModel.refreshCourses() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "刷新")
                }
            }
        )
        
        // 统计信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = "${courses.size}",
                    label = "开设课程",
                    icon = Icons.Default.MenuBook
                )
                StatisticItem(
                    value = "${courses.sumOf { it.knowledgePoints }}",
                    label = "知识点",
                    icon = Icons.Default.Psychology
                )
                StatisticItem(
                    value = "100%",
                    label = "AI覆盖率",
                    icon = Icons.Default.AutoAwesome
                )
            }
        }
        
        // 课程网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(courses) { course ->
                CourseCard(
                    course = course,
                    onClick = { onCourseClick(course.id) }
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
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
            style = MaterialTheme.typography.headlineSmall,
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
private fun CourseCard(
    course: Course,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = course.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${course.knowledgePoints}个知识点",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}