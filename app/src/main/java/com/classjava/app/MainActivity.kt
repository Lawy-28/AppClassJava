package com.classjava.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.classjava.app.config.AppwriteClient
import com.classjava.app.ui.auth.LoginScreen
import com.classjava.app.ui.auth.RegisterScreen
import com.classjava.app.ui.home.HomeScreen
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Memanggil Navigasi Utama Aplikasi dengan menerapkan padding dari Scaffold
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    // NavController untuk mengatur perpindahan halaman
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login", // Halaman pertama yang dibuka adalah Login
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

        // 2. Rute untuk Halaman Register
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

        // 3. Rute untuk Halaman Home (Dashboard Utama)
        composable("home") {
            HomeScreen(
                onLogoutSuccess = {
                    // Kembali ke login dan bersihkan rute home dari history setelah logout
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}