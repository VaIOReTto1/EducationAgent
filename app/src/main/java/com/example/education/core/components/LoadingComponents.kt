package com.example.education.core.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.education.core.common_ui.theme.EducationTheme

/**
 * 通用加载组件
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "加载中...",
    showMessage: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        
        if (showMessage) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 全屏加载组件
 */
@Composable
fun FullScreenLoading(
    message: String = "加载中...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(message = message)
    }
}

/**
 * 卡片加载骨架屏
 */
@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    val animationAlpha = if (showAnimation) alpha else 0.3f
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题骨架
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = animationAlpha))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 内容骨架
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 2) 0.5f else 1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = animationAlpha))
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 按钮骨架
            Row {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray.copy(alpha = animationAlpha))
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray.copy(alpha = animationAlpha))
                )
            }
        }
    }
}

/**
 * 带动画的加载点
 */
@Composable
fun AnimatedLoadingDots(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    dotSize: Float = 8f,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    animationDuration: Int = 1200
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDuration,
                        delayMillis = index * (animationDuration / dotCount)
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(dotSize.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

/**
 * 智能体思考加载
 */
@Composable
fun AIThinkingLoader(
    modifier: Modifier = Modifier,
    message: String = "AI正在思考...",
    agentName: String = ""
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedLoadingDots(
                dotColor = MaterialTheme.colorScheme.primary,
                dotSize = 6f
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                if (agentName.isNotEmpty()) {
                    Text(
                        text = agentName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 波浪加载动画
 */
@Composable
fun WaveLoadingIndicator(
    modifier: Modifier = Modifier,
    waveColor: Color = MaterialTheme.colorScheme.primary,
    waveCount: Int = 4
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(waveCount) { index ->
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 4f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 800,
                        delayMillis = index * 100
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "height_$index"
            )
            
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(animatedHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(waveColor)
            )
        }
    }
}

/**
 * 内容加载状态包装器
 */
@Composable
fun <T> LoadingStateWrapper(
    loadingState: LoadingState<T>,
    onRetry: () -> Unit = {},
    loadingContent: @Composable () -> Unit = { 
        LoadingIndicator(message = "加载中...") 
    },
    errorContent: @Composable (String) -> Unit = { error ->
        ErrorState(
            message = error,
            onRetry = onRetry
        )
    },
    content: @Composable (T) -> Unit
) {
    when (loadingState) {
        is LoadingState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                loadingContent()
            }
        }
        is LoadingState.Success -> {
            content(loadingState.data)
        }
        is LoadingState.Error -> {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                errorContent(loadingState.message)
            }
        }
    }
}

/**
 * 错误状态组件
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😅",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "出现了一些问题",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("重试")
        }
    }
}

/**
 * 加载状态密封类
 */
sealed class LoadingState<out T> {
    object Loading : LoadingState<Nothing>()
    data class Success<T>(val data: T) : LoadingState<T>()
    data class Error(val message: String) : LoadingState<Nothing>()
}

@Preview(showBackground = true)
@Composable
fun LoadingComponentsPreview() {
    EducationTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoadingIndicator()
            LoadingSkeleton()
            AIThinkingLoader(agentName = "课程规划助手")
            WaveLoadingIndicator()
            AnimatedLoadingDots()
        }
    }
} 