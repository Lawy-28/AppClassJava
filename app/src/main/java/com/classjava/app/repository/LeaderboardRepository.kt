package com.classjava.app.repository

import com.classjava.app.config.AppwriteClient
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LeaderboardRepository {

    private val databaseService by lazy { AppwriteClient.databases }

    suspend fun getLeaderboardData(): Result<List<Map<String, Any>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = databaseService.listDocuments(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_LEADERBOARD,
                    queries = listOf(
                        Query.orderDesc("score"),
                        Query.limit(20) // Limit to top 20
                    )
                )
                Result.success(response.documents.map { it.data })
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }
}
