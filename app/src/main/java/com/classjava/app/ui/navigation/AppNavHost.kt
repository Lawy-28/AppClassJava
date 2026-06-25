package com.classjava.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.classjava.app.ui.auth.LoginScreen
import com.classjava.app.ui.auth.RegisterScreen
import com.classjava.app.ui.home.HomeScreen
import com.classjava.app.ui.home.ProfileScreen
import com.classjava.app.ui.home.SearchScreen
import com.classjava.app.ui.leaderboard.LeaderboardPreviewScreen
import com.classjava.app.ui.quiz.QuizPreviewScreen
import com.classjava.app.ui.quiz.SoalQuizScreen
import com.classjava.app.viewmodel.AuthViewModel
import com.classjava.app.viewmodel.SessionState

/**
 * NavHost Utama Aplikasi Class [Java].
 * Mengelola seluruh rute navigasi antar screen menggunakan Jetpack Compose Navigation.
 */
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val sessionState by authViewModel.sessionState.collectAsState()

    // Splash/Checking State
    if (sessionState is SessionState.Checking) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF0F3D6F))
        }
        return
    }

    // Tentukan halaman awal berdasarkan status login
    val startDestination = if (sessionState is SessionState.LoggedIn) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- 1. AUTH ROUTES ---
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                authViewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // --- 2. MAIN FEATURES (BOTTOM NAV) ---
        composable("home") {
            HomeScreen(
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToQuizPreview = { route ->
                    navController.navigate("quiz_preview/${route.replace("/", "_")}")
                },
                onNavigateToLeaderboard = {
                    navController.navigate("leaderboard")
                },
                authViewModel = authViewModel
            )
        }

        composable("leaderboard") {
            LeaderboardPreviewScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
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
                    navController.navigate("leaderboard")
                },
                authViewModel = authViewModel
            )
        }

        composable("search") {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onTopicSelected = { route ->
                    // Langsung ke kuis tanpa lewat preview
                    navController.navigate(route)
                }
            )
        }

        // --- 3. QUIZ SYSTEM ---
        
        // Halaman Preview Materi & Persiapan Kuis
        composable("quiz_preview/{topicRoute}") { backStackEntry ->
            val encodedRoute = backStackEntry.arguments?.getString("topicRoute") ?: ""
            val topicRoute = encodedRoute.replace("_", "/")
            QuizPreviewScreen(
                topicRoute = topicRoute,
                onNavigateBack = { navController.popBackStack() },
                onMulaiKuis = { route ->
                    // route biasanya berbentuk "quiz/inheritance", "quiz/arrays", dll
                    navController.navigate(route)
                }
            )
        }

        // Halaman Pengerjaan Soal Kuis (Dinamis berdasarkan topicId)
        composable("quiz/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: "inheritance"
            SoalQuizScreen(
                topicId = topicId,
                onNavigateToLeaderboard = {
                    navController.navigate("leaderboard") {
                        // Bersihkan stack agar saat back dari leaderboard tidak kembali ke kuis
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
    }
}
