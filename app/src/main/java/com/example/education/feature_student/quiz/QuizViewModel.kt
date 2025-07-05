package com.example.education.feature_student.quiz

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val selectedAnswers: List<Int?> = emptyList(),
    val isSubmitted: Boolean = false,
    val showExplanations: Boolean = false,
    val score: Int = 0
) {
    val totalQuestions: Int get() = questions.size
    val answeredQuestions: Int get() = selectedAnswers.count { it != null }
    val progress: Float get() = if (totalQuestions > 0) answeredQuestions.toFloat() / totalQuestions else 0f
}

@HiltViewModel
class QuizViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        val questions = listOf(
            QuizQuestion("q1", "在Linux中，哪个命令用于列出目录内容?", listOf("ls", "dir", "list", "show"), 0, "ls (list) 是Unix/Linux系统中最常用的命令之一，用于显示文件和目录列表。", 1, listOf("CLI", "Basics")),
            QuizQuestion("q2", "哪个文件系统是Linux现代发行版默认的根文件系统？", listOf("FAT32", "NTFS", "Ext4", "HFS+"), 2, "Ext4 (Fourth extended filesystem) 是Ext3的后继者，包含了多项性能和可靠性改进，是当前大多数Linux发行版默认的文件系统。", 2, listOf("Filesystem", "Kernel")),
            QuizQuestion("q3", "在Shell脚本中，'#!/bin/bash' 的作用是什么？", listOf("注释", "声明变量", "Shebang，指定解释器", "导入库"), 2, "这行被称为 Shebang 或 Hashbang。它告诉系统使用哪个解释器来执行此脚本，这里指定的是bash。", 1, listOf("CLI", "Scripting")),
            QuizQuestion("q4", "哪个命令可以用来查看Linux内核版本？", listOf("kernel -v", "uname -r", "version", "sysinfo"), 1, "uname (Unix Name) 命令用于打印系统信息，-r (kernel-release) 参数专门用来显示内核的发行版本号。", 1, listOf("Kernel", "CLI")),
            QuizQuestion("q5", "交叉编译工具链（Cross-Compiler Toolchain）的主要作用是？", listOf("在PC上直接运行ARM程序", "在ARM板上编译PC程序", "在PC上生成能在ARM板上运行的程序", "自动调试代码"), 2, "交叉编译的核心思想是在一个平台（如x86 PC）上，编译出能够在另一个不同架构平台（如ARM嵌入式板）上运行的代码。", 3, listOf("Toolchain", "Compiler")),
            QuizQuestion("q6", "使用哪个命令可以实时查看内核消息？", listOf("dmesg -w", "logcat", "tail /var/log/kern.log", "journalctl -fk"), 0, "dmesg 命令用于打印内核的环形缓冲区信息，-w 或 --follow 参数可以使其持续等待新消息并打印，非常适合实时调试。", 2, listOf("Kernel", "Debug")),
            QuizQuestion("q7", "Linux中，`kill -9 PID` 命令代表什么？", listOf("优雅地终止进程", "强制杀死进程", "向进程发送暂停信号", "重启进程"), 1, "信号9 (SIGKILL) 是一个特殊的、不可被捕获或忽略的信号，它会立即终止目标进程，可能导致数据丢失。应作为最后手段使用。", 2, listOf("Process", "CLI")),
            QuizQuestion("q8", "在Linux设备驱动中，`cdev` 结构体的作用是什么？", listOf("代表一个字符设备", "管理内存区域", "处理中断请求", "创建内核线程"), 0, "struct cdev 是内核中用于表示字符设备的核心数据结构。驱动程序通过初始化和注册它，来将自己的操作与VFS层连接起来。", 3, listOf("Driver", "Kernel")),
            QuizQuestion("q9", "哪个命令用于查找文件系统中特定名称的文件？", listOf("grep", "search", "find", "locate"), 2, "`find` 命令是一个非常强大的文件搜索工具，它能根据名称、大小、修改时间等多种条件在目录树中递归搜索。", 1, listOf("CLI", "Filesystem")),
            QuizQuestion("q10", "U-Boot 在嵌入式系统启动流程中扮演什么角色？", listOf("操作系统内核", "文件系统", "引导加载程序 (Bootloader)", "应用程序"), 2, "U-Boot (Universal Boot Loader) 是一个开源的引导加载程序，主要任务是初始化硬件、加载内核镜像到内存，并启动内核。", 3, listOf("Bootloader", "Embedded")),
            QuizQuestion("q11", "什么是写时复制 (Copy-on-Write)？", listOf("一种文件备份策略", "一种延迟内存分配的技术", "一种数据压缩算法", "一种网络传输协议"), 1, "写时复制（COW）是一种优化策略。当多个进程共享同一数据时，如果其中一个要修改数据，系统才会为它复制一份新的副本。在此之前，所有进程都只读共享同一份数据。", 2, listOf("Memory", "Kernel")),
            QuizQuestion("q12", "在Linux中，`chmod 755 a.sh` 命令做了什么？", listOf("设置文件所有者可读/写/执行，同组和其他用户可读/执行", "删除 a.sh 文件", "使所有用户都有完全权限", "只有所有者可以执行"), 0, "755权限分解为：所有者(7=rwx)，所属组(5=r-x)，其他用户(5=r-x)。这是脚本和可执行文件常用的权限设置。", 2, listOf("Permissions", "CLI")),
            QuizQuestion("q13", "嵌入式系统中，看门狗定时器 (Watchdog Timer) 的主要用途是？", listOf("定时唤醒系统", "精确延时", "从系统挂起或死锁中恢复", "测量代码执行时间"), 2, "看门狗是一个硬件计时器，如果系统在特定时间内未能\"喂狗\"（重置计时器），它会强制复位系统。这可以防止系统因软件故障而永久卡死。", 3, listOf("Embedded", "Hardware")),
            QuizQuestion("q14", "`make menuconfig` 在编译Linux内核时起什么作用？", listOf("直接开始编译", "清理旧的编译文件", "生成默认配置文件", "提供一个基于文本菜单的内核配置界面"), 3, "它允许开发者通过一个友好的交互式菜单来启用或禁用内核的各种功能、驱动和子系统，最终生成 .config 文件。", 2, listOf("Kernel", "Build")),
            QuizQuestion("q15", "Linux中的 `inode` 是什么？", listOf("一个网络节点", "一个指向文件的指针", "存储文件元数据的数据结构", "一个可执行命令"), 2, "inode（索引节点）是Linux文件系统中用于存储文件元信息（如大小、权限、所有者、时间戳和数据块位置）的数据结构。文件名实际上是指向inode的链接。", 3, listOf("Filesystem", "Kernel"))
        )
        _uiState.value = QuizUiState(
            questions = questions,
            selectedAnswers = List(questions.size) { null }
        )
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        if (_uiState.value.isSubmitted) return

        _uiState.update { currentState ->
            val newAnswers = currentState.selectedAnswers.toMutableList()
            newAnswers[questionIndex] = optionIndex
            currentState.copy(selectedAnswers = newAnswers)
        }
    }

    fun submitQuiz() {
        if (_uiState.value.isSubmitted) return

        val score = calculateScore()
        _uiState.update {
            it.copy(
                isSubmitted = true,
                score = score,
                showExplanations = true // 提交后自动显示解析
            )
        }
        // TODO Backend integration point: Submit score to the server
    }

    fun resetQuiz() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedAnswers = List(currentState.questions.size) { null },
                isSubmitted = false,
                score = 0,
                showExplanations = false
            )
        }
        // TODO Backend integration point: Log quiz reset event
    }

    fun clearSelection() {
        if (_uiState.value.isSubmitted) return
        _uiState.update { currentState ->
            currentState.copy(
                selectedAnswers = List(currentState.questions.size) { null }
            )
        }
    }

    fun toggleExplanations() {
        _uiState.update { it.copy(showExplanations = !it.showExplanations) }
    }

    private fun calculateScore(): Int {
        var score = 0
        val state = _uiState.value
        state.questions.forEachIndexed { index, q ->
            if (state.selectedAnswers[index] == q.correctAnswerIndex) {
                score++
            }
        }
        return score
    }
} 