package com.classjava.app.repository

import com.classjava.app.config.AppwriteClient
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LeaderboardRepository {

    private val databaseService by lazy { AppwriteClient.databases }

    suspend fun getLeaderboardData(topicId: String? = null): Result<List<Map<String, Any>>> {
        return withContext(Dispatchers.IO) {
            try {
                val queries = mutableListOf(
                    Query.orderDesc("score"),
                    Query.limit(20)
                )
                
                if (!topicId.isNullOrBlank() && topicId.lowercase() != "semua") {
                    queries.add(Query.equal("topic_id", topicId.lowercase()))
                }

                val response = databaseService.listDocuments(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_LEADERBOARD,
                    queries = queries
                )
                Result.success(response.documents.map { it.data })
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }

    /**
     * Mengambil data profile_id untuk daftar user_id yang diberikan.
     * Digunakan untuk mencocokkan foto profil asli.
     */
    suspend fun getUserProfiles(userIds: List<String>): Result<Map<String, String>> {
        if (userIds.isEmpty()) return Result.success(emptyMap())
        return withContext(Dispatchers.IO) {
            try {
                val response = databaseService.listDocuments(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_USERS,
                    queries = listOf(
                        Query.equal("\$id", userIds)
                    )
                )
                
                val profileMap = response.documents.associate { doc ->
                    doc.id to (doc.data["profile_id"] as? String ?: "")
                }
                Result.success(profileMap)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }
}
