package com.example.education.navigation

/**
 * 应用路由定义
 * 定义所有页面的路由常量
 */

/**
 * 教师端路由
 */
sealed class TeacherRoutes(val route: String) {
    /**
     * 教师仪表盘
     */
    object Dashboard : TeacherRoutes("teacher_dashboard")
    
    /**
     * 课程管理
     */
    object CourseManagement : TeacherRoutes("teacher_course_management")
    
    /**
     * 课程内容管理
     */
    object CourseContent : TeacherRoutes("teacher_course_content")
    
    /**
     * 学生分析
     */
    object StudentAnalytics : TeacherRoutes("teacher_student_analytics")
    
    /**
     * 作业批改
     */
    object AssignmentGrading : TeacherRoutes("teacher_assignment_grading")
    
    /**
     * 设置页面
     */
    object Settings : TeacherRoutes("teacher_settings")
}

/**
 * 学生端路由
 */
sealed class StudentRoutes(val route: String) {
    /**
     * 课程列表
     */
    object Courses : StudentRoutes("student_courses")
    
    /**
     * 课程阅读器
     */
    object Reader : StudentRoutes("student_reader")
    
    /**
     * 章节阅读器
     */
    object ChapterReader : StudentRoutes("student_chapter_reader")
    
    /**
     * 随堂练习
     */
    object Practice : StudentRoutes("student_practice")
    
    /**
     * 学习进度
     */
    object Progress : StudentRoutes("student_progress")
    
    /**
     * 设置页面
     */
    object Settings : StudentRoutes("student_settings")
}

/**
 * 通用路由
 */
sealed class CommonRoutes(val route: String) {
    /**
     * 登录页面
     */
    object Login : CommonRoutes("login")
    
    /**
     * 注册页面
     */
    object Register : CommonRoutes("register")
    
    /**
     * 角色选择页面
     */
    object RoleSelection : CommonRoutes("role_selection")
    
    /**
     * 启动页面
     */
    object Splash : CommonRoutes("splash")
}

/**
 * 路由工具类
 */
object RouteUtils {
    
    /**
     * 根据角色获取默认起始路由
     */
    fun getStartDestination(role: com.example.education.core.common.UserRole): String {
        return when (role) {
            com.example.education.core.common.UserRole.TEACHER -> TeacherRoutes.Dashboard.route
            com.example.education.core.common.UserRole.STUDENT -> StudentRoutes.Courses.route
        }
    }
    
    /**
     * 构建带参数的路由
     */
    fun buildRouteWithArgs(baseRoute: String, vararg args: String): String {
        return buildString {
            append(baseRoute)
            args.forEach { arg ->
                append("/")
                append(arg)
            }
        }
    }
    
    /**
     * 检查路由是否为教师端路由
     */
    fun isTeacherRoute(route: String?): Boolean {
        return route?.startsWith("teacher_") == true
    }
    
    /**
     * 检查路由是否为学生端路由
     */
    fun isStudentRoute(route: String?): Boolean {
        return route?.startsWith("student_") == true
    }
    
    /**
     * 获取路由显示名称
     */
    fun getRouteDisplayName(route: String?): String {
        return when (route) {
            TeacherRoutes.Dashboard.route -> "教师仪表盘"
            TeacherRoutes.CourseManagement.route -> "课程管理"
            TeacherRoutes.CourseContent.route -> "课程内容"
            TeacherRoutes.StudentAnalytics.route -> "学生分析"
            TeacherRoutes.AssignmentGrading.route -> "作业批改"
            TeacherRoutes.Settings.route -> "设置"
            
            StudentRoutes.Courses.route -> "我的课程"
            StudentRoutes.Reader.route -> "课程阅读"
            StudentRoutes.ChapterReader.route -> "章节阅读"
            StudentRoutes.Practice.route -> "随堂练习"
            StudentRoutes.Progress.route -> "学习进度"
            StudentRoutes.Settings.route -> "设置"
            
            CommonRoutes.Login.route -> "登录"
            CommonRoutes.Register.route -> "注册"
            CommonRoutes.RoleSelection.route -> "角色选择"
            CommonRoutes.Splash.route -> "启动页"
            
            else -> "未知页面"
        }
    }
}