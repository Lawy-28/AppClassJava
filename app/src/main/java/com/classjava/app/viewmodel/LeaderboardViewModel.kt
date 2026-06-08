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

    init {
        fetchLeaderboard()
    }

    fun fetchLeaderboard() {
        viewModelScope.launch {
            _leaderboardState.value = LeaderboardState.Loading
            repository.getLeaderboardData()
                .onSuccess { data ->
                    val entries = data.map {
                        LeaderboardEntry(
                            username = it["username"] as? String ?: "Unknown",
                            score = (it["score"] as? Number)?.toInt() ?: 0,
                            profileId = it["profile_id"] as? String
                        )
                    }
                    _leaderboardState.value = LeaderboardState.Success(entries)
                }
                .onFailure {
                    _leaderboardState.value = LeaderboardState.Error(it.message ?: "Gagal mengambil data")
                }
        }
    }
}
