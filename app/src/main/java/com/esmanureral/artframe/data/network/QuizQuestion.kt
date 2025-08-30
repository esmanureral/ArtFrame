package com.esmanureral.artframe.data.network

data class QuizQuestion(
    val artworkId: String = "",
    val imageUrl: String,
    val correctAnswer: String,
    val options: List<String>
)
