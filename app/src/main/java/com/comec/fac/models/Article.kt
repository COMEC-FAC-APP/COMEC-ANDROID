package com.comec.fac.models

data class Article(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val date: String = "",
    val excerpt: String = "",
    val content: String = "",
    val emoji: String = "📋",
    val timestamp: Long = System.currentTimeMillis()
)
