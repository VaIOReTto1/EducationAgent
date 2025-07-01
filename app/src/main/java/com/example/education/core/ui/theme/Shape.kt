package com.example.education.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 形状定义
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * 教育应用专用形状定义
 */
object EducationShapes {
    // 卡片形状
    val cardShape = RoundedCornerShape(12.dp)
    
    // 按钮形状
    val buttonShape = RoundedCornerShape(8.dp)
    
    // 输入框形状
    val textFieldShape = RoundedCornerShape(8.dp)
    
    // 对话气泡形状
    val chatBubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 4.dp,
        bottomEnd = 16.dp
    )
    
    // AI回复气泡形状
    val agentBubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 16.dp,
        bottomEnd = 4.dp
    )
    
    // 进度条形状
    val progressShape = RoundedCornerShape(4.dp)
    
    // 标签形状
    val chipShape = RoundedCornerShape(16.dp)
    
    // 底部导航形状
    val bottomNavShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp
    )
    
    // 顶部应用栏形状
    val topAppBarShape = RoundedCornerShape(
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    
    // 模态底部表单形状
    val bottomSheetShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp
    )
    
    // 对话框形状
    val dialogShape = RoundedCornerShape(16.dp)
    
    // 课程封面形状
    val courseCoverShape = RoundedCornerShape(12.dp)
    
    // 章节项目形状
    val chapterItemShape = RoundedCornerShape(8.dp)
    
    // 评估卡片形状
    val assessmentCardShape = RoundedCornerShape(12.dp)
    
    // 成就徽章形状
    val badgeShape = RoundedCornerShape(50) // 圆形
    
    // 搜索栏形状
    val searchBarShape = RoundedCornerShape(24.dp)
    
    // 浮动操作按钮形状
    val fabShape = RoundedCornerShape(16.dp)
    
    // 小型浮动操作按钮形状
    val smallFabShape = RoundedCornerShape(12.dp)
    
    // 扩展浮动操作按钮形状
    val extendedFabShape = RoundedCornerShape(16.dp)
    
    // 选项卡形状
    val tabShape = RoundedCornerShape(8.dp)
    
    // 开关形状
    val switchShape = RoundedCornerShape(16.dp)
    
    // 滑块轨道形状
    val sliderTrackShape = RoundedCornerShape(2.dp)
    
    // 滑块拇指形状
    val sliderThumbShape = RoundedCornerShape(50) // 圆形
}