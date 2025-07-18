package com.example.education.feature_settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * 设置页面
 * 包含夜间模式、登出等功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部应用栏
        TopAppBar(
            title = { Text("设置") }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 用户信息卡片
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column {
                                Text(
                                    text = uiState.userName ?: "未知用户",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = uiState.userRole?.displayName ?: "未知角色",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "ID: ${uiState.userId ?: "未知"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            // 外观设置
            item {
                SettingsSection(title = "外观") {
                    SettingsItem(
                        icon = Icons.Default.DarkMode,
                        title = "夜间模式",
                        subtitle = if (uiState.isDarkMode) "已开启" else "已关闭",
                        trailing = {
                            Switch(
                                checked = uiState.isDarkMode,
                                onCheckedChange = viewModel::toggleDarkMode
                            )
                        }
                    )
                }
            }
            
            // 学习设置（仅学生可见）
            if (uiState.userRole?.value == "student") {
                item {
                    SettingsSection(title = "学习设置") {
                        SettingsItem(
                            icon = Icons.Default.Notifications,
                            title = "学习提醒",
                            subtitle = "定时提醒学习进度",
                            trailing = {
                                Switch(
                                    checked = uiState.learningReminder,
                                    onCheckedChange = viewModel::toggleLearningReminder
                                )
                            }
                        )
                        
                        SettingsItem(
                            icon = Icons.Default.Speed,
                            title = "自动播放速度",
                            subtitle = "${uiState.playbackSpeed}x",
                            onClick = { viewModel.showPlaybackSpeedDialog() }
                        )
                    }
                }
            }
            
            // 教学设置（仅教师可见）
            if (uiState.userRole?.value == "teacher") {
                item {
                    SettingsSection(title = "教学设置") {
                        SettingsItem(
                            icon = Icons.Default.AutoAwesome,
                            title = "AI助手模式",
                            subtitle = if (uiState.aiAssistantMode == "advanced") "高级模式" else "基础模式",
                            onClick = { viewModel.toggleAiAssistantMode() }
                        )
                        
                        SettingsItem(
                            icon = Icons.Default.Save,
                            title = "自动保存",
                            subtitle = "自动保存备课内容",
                            trailing = {
                                Switch(
                                    checked = uiState.autoSave,
                                    onCheckedChange = viewModel::toggleAutoSave
                                )
                            }
                        )
                    }
                }
            }
            
            // 通用设置
            item {
                SettingsSection(title = "通用") {
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "语言",
                        subtitle = "简体中文",
                        onClick = { /* TODO: 语言设置 */ }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Storage,
                        title = "缓存管理",
                        subtitle = "清理应用缓存",
                        onClick = { viewModel.clearCache() }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "关于应用",
                        subtitle = "版本 1.0.0",
                        onClick = { /* TODO: 关于页面 */ }
                    )
                }
            }
            
            // 账户操作
            item {
                SettingsSection(title = "账户") {
                    SettingsItem(
                        icon = Icons.Default.Logout,
                        title = "退出登录",
                        subtitle = "退出当前账户",
                        onClick = { showLogoutDialog = true },
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    // 登出确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出当前账户吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
    
    // 播放速度选择对话框
    if (uiState.showPlaybackSpeedDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hidePlaybackSpeedDialog() },
            title = { Text("选择播放速度") },
            text = {
                Column {
                    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                    speeds.forEach { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.playbackSpeed == speed,
                                onClick = { viewModel.setPlaybackSpeed(speed) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${speed}x")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.hidePlaybackSpeedDialog() }
                ) {
                    Text("确定")
                }
            }
        )
    }
}

/**
 * 设置分组组件
 */
@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

/**
 * 设置项组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick ?: {},
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (textColor == MaterialTheme.colorScheme.onSurface) 
                    MaterialTheme.colorScheme.onSurfaceVariant else textColor
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
                
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            trailing?.invoke()
        }
    }
}