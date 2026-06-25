package com.classjava.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classjava.app.repository.LeaderboardRepository
import com.classjava.app.data.model.LeaderboardEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LeaderboardState {
    object Loading : LeaderboardState()
    data class Success(val data: List<LeaderboardEntry>) : LeaderboardState()
    data class Error(val message: String) : LeaderboardState()
}

class LeaderboardViewModel : ViewModel() {
    private val repository = LeaderboardRepository()

    private val _leaderboardState = MutableStateFlow<LeaderboardState>(LeaderboardState.Loading)
    val leaderboardState: StateFlow<LeaderboardState> = _leaderboardState

    private val _currentTopic = MutableStateFlow("Semua")
    val currentTopic: StateFlow<String> = _currentTopic

    init {
        fetchLeaderboard()
    }

    /**
     * Mengambil data leaderboard dan melakukan agregasi skor jika filter "Semua" aktif.
     */
    fun fetchLeaderboard(topic: String? = null) {
        val uiTopicLabel = topic ?: _currentTopic.value
        val dbTopicId = mapTopicToDbId(uiTopicLabel) // Pemetaan ke ID database yang benar
        
        viewModelScope.launch {
            _leaderboardState.value = LeaderboardState.Loading
            
            // 1. Ambil data leaderboard menggunakan ID topik yang sudah dipetakan
            repository.getLeaderboardData(dbTopicId)
                .onSuccess { lbData ->
                    val userIds = lbData.mapNotNull { it["user_id"] as? String }.distinct()
                    
                    // 2. Fetch data profile_id dari koleksi 'users' secara massal
                    repository.getUserProfiles(userIds)
                        .onSuccess { profileMap ->
                            // 3. Gabungkan data mentah ke objek LeaderboardEntry
                            val rawEntries = lbData.map {
                                val userId = it["user_id"] as? String ?: ""
                                LeaderboardEntry(
                                    userId = userId,
                                    username = it["student_name"] as? String ?: "Unknown",
                                    score = (it["score"] as? Number)?.toInt() ?: 0,
                                    profilePicId = profileMap[userId] ?: ""
                                )
                            }

                            // 4. LOGIKA AGREGASI: Jika filter "Semua", jumlahkan skor kumulatif per user
                            val finalData = if (uiTopicLabel == "Semua") {
                                rawEntries.groupBy { it.userId }
                                    .map { (uid, userEntries) ->
                                        val first = userEntries.first()
                                        LeaderboardEntry(
                                            userId = uid,
                                            username = first.username,
                                            score = userEntries.sumOf { it.score }, // Jumlahkan total skor
                                            profilePicId = first.profilePicId
                                        )
                                    }
                                    .sortedByDescending { it.score } // Urutkan dari total tertinggi
                            } else {
                                // Jika filter per topik, cukup tampilkan High Score
                                rawEntries.sortedByDescending { it.score }
                            }

                            _leaderboardState.value = LeaderboardState.Success(finalData)
                        }
                        .onFailure {
                            _leaderboardState.value = LeaderboardState.Error("Gagal memuat profil user")
                        }
                }
                .onFailure {
                    _leaderboardState.value = LeaderboardState.Error(it.message ?: "Gagal mengambil data")
                }
        }
    }

    /**
     * Memetakan teks UI ke ID topik yang ada di database Appwrite.
     * Contoh: "Data Types" -> "datatypes"
     */
    private fun mapTopicToDbId(uiTopic: String): String {
        return when (uiTopic) {
            "Data Types" -> "datatypes"
            "Constructors" -> "constructors"
            "Interface" -> "interface"
            "Inheritance" -> "inheritance"
            "Arrays" -> "arrays"
            "Looping" -> "looping"
            "Methods" -> "methods"
            else -> uiTopic.lowercase()
        }
    }

    /**
     * Mengubah filter topik dan memicu penarikan data ulang.
     */
    fun updateTopicFilter(topic: String) {
        _currentTopic.value = topic
        fetchLeaderboard(topic)
    }
}
