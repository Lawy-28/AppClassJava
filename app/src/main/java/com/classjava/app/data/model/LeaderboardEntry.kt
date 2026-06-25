package com.classjava.app.data.model

data class LeaderboardEntry(
    val userId: String = "",
    val username: String,
    val score: Int,
    val profilePicId: String = ""
)
