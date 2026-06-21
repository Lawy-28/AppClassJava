package com.classjava.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classjava.app.repository.LeaderboardRepository
import com.classjava.app.ui.leaderboard.LeaderboardEntry
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

    fun fetchLeaderboard(topic: String? = null) {
        viewModelScope.launch {
            _leaderboardState.value = LeaderboardState.Loading
            
            // 1. Ambil data leaderboard (score & user_id)
            repository.getLeaderboardData(topic ?: _currentTopic.value)
                .onSuccess { lbData ->
                    val userIds = lbData.mapNotNull { it["user_id"] as? String }.distinct()
                    
                    // 2. Fetch data profile_id dari koleksi 'users' secara massal
                    repository.getUserProfiles(userIds)
                        .onSuccess { profileMap ->
                            // 3. Gabungkan data
                            val entries = lbData.map {
                                val userId = it["user_id"] as? String ?: ""
                                LeaderboardEntry(
                                    username = it["student_name"] as? String ?: "Unknown",
                                    score = (it["score"] as? Number)?.toInt() ?: 0,
                                    profilePicId = profileMap[userId] ?: ""
                                )
                            }
                            _leaderboardState.value = LeaderboardState.Success(entries)
                        }
                        .onFailure {
                            // Fallback jika fetch profile gagal, tampilkan tanpa foto asli
                            val entries = lbData.map {
                                LeaderboardEntry(
                                    username = it["student_name"] as? String ?: "Unknown",
                                    score = (it["score"] as? Number)?.toInt() ?: 0,
                                    profilePicId = ""
                                )
                            }
                            _leaderboardState.value = LeaderboardState.Success(entries)
                        }
                }
                .onFailure {
                    _leaderboardState.value = LeaderboardState.Error(it.message ?: "Gagal mengambil data")
                }
        }
    }

    fun updateTopicFilter(topic: String) {
        _currentTopic.value = topic
        fetchLeaderboard(topic)
    }
}
