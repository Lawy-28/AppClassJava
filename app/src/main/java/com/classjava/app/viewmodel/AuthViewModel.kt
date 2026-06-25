package com.classjava.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classjava.app.repository.AuthRepository
import com.classjava.app.repository.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Sealed class untuk merepresentasikan semua kemungkinan state di layar Auth
sealed class AuthState {
    object Idle : AuthState()           // State awal, tidak ada aksi
    object Loading : AuthState()        // Sedang memproses (loading)
    object Success : AuthState()        // Aksi berhasil
    data class Error(val message: String) : AuthState() // Aksi gagal, simpan pesan error
}

// Sealed class untuk state saat cek sesi login awal
sealed class SessionState {
    object Checking : SessionState()    // Sedang mengecek sesi
    object LoggedIn : SessionState()    // User sudah login
    object LoggedOut : SessionState()   // User belum login
}

class AuthViewModel : ViewModel() {

    // Repository yang bertugas komunikasi dengan Appwrite
    private val authRepository = AuthRepository()
    private val leaderboardRepository = LeaderboardRepository()

    // --- State Management ---
    // StateFlow untuk state aksi (login/register/logout)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // StateFlow untuk state sesi awal aplikasi
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Checking)
    val sessionState: StateFlow<SessionState> = _sessionState

    // StateFlow untuk menyimpan nama user yang sedang login
    private val _currentUserName = MutableStateFlow("User")
    val currentUserName: StateFlow<String> = _currentUserName

    // StateFlow untuk menyimpan email user yang sedang login
    private val _currentUserEmail = MutableStateFlow("")
    val currentUserEmail: StateFlow<String> = _currentUserEmail

    // StateFlow untuk menyimpan URL foto profil
    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: StateFlow<String?> = _profilePictureUrl

    // StateFlow untuk menyimpan skor total user
    private val _currentUserScore = MutableStateFlow(0)
    val currentUserScore: StateFlow<Int> = _currentUserScore

    // Dipanggil sekali saat ViewModel pertama dibuat, untuk cek sesi login
    init {
        checkSession()
    }

    // --- Fungsi-Fungsi Logika (dipanggil dari View) ---

    // 1. Cek apakah user sudah login sebelumnya
    fun checkSession() {
        viewModelScope.launch {
            _sessionState.value = SessionState.Checking
            authRepository.getCurrentUser()
                .onSuccess { user ->
                    _currentUserName.value = user.name
                    _currentUserEmail.value = user.email
                    // Load data tambahan (profile_id & score)
                    loadUserData(user.id)
                    loadUserScore(user.id)
                    _sessionState.value = SessionState.LoggedIn
                }
                .onFailure {
                    _sessionState.value = SessionState.LoggedOut
                }
        }
    }

    // Load profile_id dari database
    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            authRepository.getUserDataFromDb(userId).onSuccess { data ->
                val profileId = data["profile_id"] as? String
                if (!profileId.isNullOrBlank()) {
                    _profilePictureUrl.value = authRepository.getProfilePictureUrl(profileId)
                } else {
                    _profilePictureUrl.value = null
                }
            }
        }
    }

    // 2. Proses Login
    fun login(email: String, password: String) {
        // Validasi input sebelum dikirim ke Repository
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Semua data wajib diisi!")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.login(email.trim(), password.trim())
                .onSuccess {
                    // Setelah login sukses, ambil data user
                    authRepository.getCurrentUser().onSuccess { user ->
                        _currentUserName.value = user.name
                        _currentUserEmail.value = user.email
                        loadUserData(user.id)
                    }
                    _authState.value = AuthState.Success
                }
                .onFailure {
                    _authState.value = AuthState.Error("Email atau kata sandi salah")
                }
        }
    }

    // 3. Proses Register (Sudah dihandle manual di RegisterScreen, tapi update state jika diperlukan)
    fun register(name: String, email: String, password: String) {
        // ... (Fungsi ini mungkin sudah jarang dipakai karena logika manual di View)
    }

    // 4. Proses Logout
    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.logout()
                .onSuccess {
                    _currentUserName.value = "User"
                    _currentUserEmail.value = ""
                    _profilePictureUrl.value = null
                    _authState.value = AuthState.Success
                }
                .onFailure {
                    _authState.value = AuthState.Error("Gagal logout, coba lagi")
                }
        }
    }

    // 5. Ambil data user yang sedang login (untuk HomeScreen & ProfileScreen)
    fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser()
                .onSuccess { user ->
                    _currentUserName.value = user.name
                    _currentUserEmail.value = user.email
                    loadUserData(user.id)
                    loadUserScore(user.id)
                }
        }
    }

    // 6. Update URL foto profil di State setelah upload manual di View
    fun updateProfileUrl(newUrl: String?) {
        _profilePictureUrl.value = newUrl
    }

    // Ambil skor terbaru dari database
    fun loadUserScore(userId: String) {
        viewModelScope.launch {
            leaderboardRepository.getUserTotalScore(userId).onSuccess { totalScore ->
                _currentUserScore.value = totalScore
            }
        }
    }

    // 7. Reset state ke Idle (dipanggil setelah UI merespons state sukses/error)
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}