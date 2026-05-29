package com.classjava.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.classjava.app.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogoutSuccess: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    var studentName by remember { mutableStateOf("Username") }
    var studentEmail by remember { mutableStateOf("user@gmail.com") }

    val isPreview = LocalInspectionMode.current
    LaunchedEffect(Unit) {
        if (!isPreview) {
            authRepository.getCurrentUser().onSuccess { user ->
                studentName = user.name
                studentEmail = user.email
            }
        }
    }

    val primaryBlue = Color(0xFF0F3D6F)
    val backgroundCard = Color(0xFFE9EDF2)
    val accentOrange = Color(0xFFE28743)
    val logoutRed = Color(0xFF8B0000)

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // The actual bar
                BottomAppBar(
                    containerColor = primaryBlue,
                    modifier = Modifier.height(65.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home Icon (Inactive)
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onNavigateToHome,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home, 
                                    contentDescription = null, 
                                    tint = Color.White, 
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Space for the floating button
                        Spacer(modifier = Modifier.width(80.dp))

                        // Profile Icon (Active)
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 75.dp, height = 40.dp)
                                    .background(accentOrange, shape = RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person, 
                                    contentDescription = null, 
                                    tint = Color.White, 
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                // The Floating Circle (Leaderboard)
                Box(
                    modifier = Modifier
                        .offset(y = (-10).dp)
                        .size(80.dp)
                        .background(primaryBlue, shape = CircleShape)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onNavigateToLeaderboard,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profile Image Placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = studentName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = primaryBlue
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Username Row
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = primaryBlue,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = studentName, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Row
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = primaryBlue,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = studentEmail, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        authRepository.logout().onSuccess {
                            onLogoutSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, logoutRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = logoutRed)
            ) {
                Text(text = "Logout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onLogoutSuccess = {},
        onNavigateToHome = {},
        onNavigateToLeaderboard = {}
    )
}
