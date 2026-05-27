package com.classjava.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.classjava.app.config.AppwriteClient
import com.classjava.app.repository.AuthRepository
import com.classjava.app.ui.auth.LoginScreen
import com.classjava.app.ui.auth.RegisterScreen
import com.classjava.app.ui.home.HomeScreen
import com.classjava.app.ui.home.ProfileScreen
import com.classjava.app.ui.theme.ClassJavaTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi Appwrite Cloud
        AppwriteClient.initialize(applicationContext)

        setContent {
            ClassJavaTheme {
                // Langsung panggil AppNavigation tanpa padding Scaffold agar bar bisa full ke atas
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    // NavController untuk mengatur perpindahan halaman
    val navController = rememberNavController()
    val authRepository = remember { AuthRepository() }

    // State untuk menentukan halaman awal secara dinamis
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Cek status login saat aplikasi dibuka
    LaunchedEffect(Unit) {
        authRepository.getCurrentUser().onSuccess {
            startDestination = "home"
        }.onFailure {
            startDestination = "login"
        }
    }

    // Tampilkan Loading saat mengecek sesi
    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = Color(0xFF0F3D6F))
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!,
        modifier = modifier
    ) {
        // 1. Rute untuk Halaman Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Pindah ke Dashboard/Menu Utama Kuis setelah sukses login
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // Bersihkan history agar tidak bisa back ke login
                    }
                },
                onNavigateToRegister = {
                    // Pindah ke halaman register saat tombol "Daftar Akun Baru" diklik
                    navController.navigate("register")
                }
            )
        }

        // 3. Rute untuk Halaman Register
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Setelah sukses daftar, kembalikan siswa ke halaman login
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    // Kembali ke login jika tombol "Masuk" diklik
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onLogoutSuccess = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToLeaderboard = {
                    // Placeholder
                }
            )
        }
    }
}