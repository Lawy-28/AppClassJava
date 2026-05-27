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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.classjava.app.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
) {
    val authRepository = remember { AuthRepository() }
    var studentName by remember { mutableStateOf("User!") }

    LaunchedEffect(Unit) {
        authRepository.getCurrentUser().onSuccess { user ->
            studentName = user.name + "!"
        }.onFailure {
            // Default "User!"
        }
    }

    val primaryBlue = Color(0xFF0F3D6F)
    val backgroundCard = Color(0xFFE9EDF2)
    val accentOrange = Color(0xFFE28743)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Class [Java]", 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White,
                        fontSize = 20.sp
                    ) 
                },
                windowInsets = TopAppBarDefaults.windowInsets,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue),
                actions = {
                    // Search Bar
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .width(110.dp)
                            .height(34.dp)
                            .background(Color.White, shape = RoundedCornerShape(17.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Search", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            )
        },
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
                        // Home Icon (Active)
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
                                    imageVector = Icons.Default.Home, 
                                    contentDescription = null, 
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Space for the floating button
                        Spacer(modifier = Modifier.width(80.dp))

                        // Profile Icon (Inactive)
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onNavigateToProfile,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person, 
                                    contentDescription = null, 
                                    tint = Color.White, 
                                    modifier = Modifier.size(38.dp)
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
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selamat Datang Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = backgroundCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color.LightGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Selamat Datang,",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            text = studentName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = backgroundCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Materi Utama : -",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status Kuis : Belum Dimulai",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentOrange
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
