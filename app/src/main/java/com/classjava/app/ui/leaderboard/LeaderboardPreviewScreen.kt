package com.classjava.app.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.classjava.app.viewmodel.LeaderboardState
import com.classjava.app.viewmodel.LeaderboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardPreviewScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: LeaderboardViewModel = viewModel()
) {
    val primaryBlue = Color(0xFF0F3D6F)
    val accentOrange = Color(0xFFE28743)
    val cardBackground = Color(0xFFF1F5F9)
    val listCardBackground = Color(0xFFFFF7F2)

    val state by viewModel.leaderboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Leaderboard",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        },
        bottomBar = {
            BottomNavigationBar(primaryBlue, accentOrange, onNavigateToHome, onNavigateToProfile)
        },
        containerColor = Color.White
    ) { innerPadding ->
        when (val s = state) {
            is LeaderboardState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryBlue)
                }
            }
            is LeaderboardState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = Color.Red)
                }
            }
            is LeaderboardState.Success -> {
                val data = s.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Podium Card (Top 3)
                    PodiumCard(
                        backgroundColor = cardBackground,
                        accentColor = accentOrange,
                        topThree = data.take(3)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filter "Semua >"
                    Surface(
                        color = Color(0xFF5A7A96),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Semua >",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Top Leaderboard Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = listCardBackground),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Top Leaderboard",
                                fontWeight = FontWeight.Bold,
                                color = primaryBlue,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                data.forEachIndexed { index, entry ->
                                    LeaderboardItem(
                                        rank = index + 1,
                                        entry = entry,
                                        isHighlighted = index == 0,
                                        primaryBlue = primaryBlue
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PodiumCard(backgroundColor: Color, accentColor: Color, topThree: List<LeaderboardEntry>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place
                if (topThree.size >= 2) {
                    PodiumBar(
                        rank = "2",
                        entry = topThree[1],
                        height = 80.dp,
                        accentColor = accentColor
                    )
                } else {
                    Spacer(modifier = Modifier.width(60.dp))
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 1st Place
                if (topThree.isNotEmpty()) {
                    PodiumBar(
                        rank = "1",
                        entry = topThree[0],
                        height = 110.dp,
                        accentColor = accentColor
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 3rd Place
                if (topThree.size >= 3) {
                    PodiumBar(
                        rank = "3",
                        entry = topThree[2],
                        height = 60.dp,
                        accentColor = accentColor
                    )
                } else {
                    Spacer(modifier = Modifier.width(60.dp))
                }
            }
        }
    }
}

@Composable
fun PodiumBar(rank: String, entry: LeaderboardEntry, height: androidx.compose.ui.unit.Dp, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = entry.username,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (entry.profileId != null) {
                AsyncImage(
                    model = "https://sgp.cloud.appwrite.io/v1/storage/buckets/6a20e05100206689649a/files/${entry.profileId}/view?project=6a126b48002b3fb6cbe7",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Bar
        Box(
            modifier = Modifier
                .width(45.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, entry: LeaderboardEntry, isHighlighted: Boolean, primaryBlue: Color) {
    val backgroundColor = if (isHighlighted) Color(0xFF5A7A96) else Color.Transparent
    val textColor = if (isHighlighted) Color.White else primaryBlue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank.",
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 16.sp,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (entry.profileId != null) {
                AsyncImage(
                    model = "https://sgp.cloud.appwrite.io/v1/storage/buckets/6a20e05100206689649a/files/${entry.profileId}/view?project=6a126b48002b3fb6cbe7",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = entry.username,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = entry.score.toString(),
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 16.sp
        )
    }
}

@Composable
fun BottomNavigationBar(
    primaryBlue: Color,
    accentOrange: Color,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryBlue)
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, "Home", Color.White, onNavigateToHome)
            
            // Highlighted Leaderboard Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-10).dp)
                    .background(accentOrange, CircleShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Leaderboard,
                    contentDescription = "Leaderboard",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            BottomNavItem(Icons.Default.Person, "Profile", Color.White, onNavigateToProfile)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
    }
}

data class LeaderboardEntry(
    val username: String,
    val score: Int,
    val profileId: String? = null
)

@Preview(showBackground = true)
@Composable
fun LeaderboardPreview() {
    LeaderboardPreviewScreen()
}
