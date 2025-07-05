package com.example.education.feature_student.quiz

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val difficulty: Int, // 1: Easy, 2: Medium, 3: Hard
    val tags: List<String>
) 