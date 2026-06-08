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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.classjava.app.config.AppwriteClient
import com.classjava.app.ui.auth.LoginScreen
import com.classjava.app.ui.auth.RegisterScreen
import com.classjava.app.ui.home.HomeScreen
import com.classjava.app.ui.home.ProfileScreen
import com.classjava.app.ui.home.SearchScreen
import com.classjava.app.ui.leaderboard.LeaderboardPreviewScreen
import com.classjava.app.ui.quiz.QuizPreviewScreen
import com.classjava.app.ui.theme.ClassJavaTheme
import com.classjava.app.viewmodel.AuthViewModel
import com.classjava.app.viewmodel.SessionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppwriteClient.initialize(applicationContext)
        setContent {
            ClassJavaTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val sessionState by authViewModel.sessionState.collectAsState()

    if (sessionState is SessionState.Checking) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF0F3D6F))
        }
        return
    }

    val startDestination = if (sessionState is SessionState.LoggedIn) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. Login
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

        // 2. Register
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

        // 3. Home
        composable("home") {
            HomeScreen(
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToSearch = { navController.navigate("search") },
                // Navigasi ke preview kuis, encode route pakai replace agar tidak bentrok
                onNavigateToQuizPreview = { route ->
                    navController.navigate("quiz_preview/${route.replace("/", "_")}")
                },
                onNavigateToLeaderboard = {
                    navController.navigate("leaderboard")
                },
                authViewModel = authViewModel
            )
        }

        // 4. Profile
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

        // 4.5 Leaderboard
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

        // 5. Search
        composable("search") {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onTopicSelected = { route ->
                    navController.navigate("quiz_preview/${route.replace("/", "_")}")
                }
            )
        }

        // 6. Quiz Preview — menerima route topik sebagai argumen
        composable("quiz_preview/{topicRoute}") { backStackEntry ->
            val encodedRoute = backStackEntry.arguments?.getString("topicRoute") ?: ""
            val topicRoute = encodedRoute.replace("_", "/") // Kembalikan ke format "quiz/arrays"
            QuizPreviewScreen(
                topicRoute = topicRoute,
                onNavigateBack = { navController.popBackStack() },
                onMulaiKuis = { /* TODO: navigasi ke halaman soal */ }
            )
        }
    }
}
