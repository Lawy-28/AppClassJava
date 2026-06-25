package com.classjava.app.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.classjava.app.config.AppwriteClient
import com.classjava.app.data.model.LeaderboardEntry
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
    val lightGrayBg = Color(0xFFE9EDF2)
    val listContainerBg = Color(0xFFFDF5F2)
    val itemBg = Color(0xFF5A7A96)

    val state by viewModel.leaderboardState.collectAsState()
    val currentTopic by viewModel.currentTopic.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val topics = listOf(
        "Semua", "Inheritance", "Arrays", "Looping", 
        "Methods", "Data Types", "Constructors", "Interface"
    )

    // Lifecycle & Refresh Data Otomatis
    LaunchedEffect(Unit) {
        viewModel.fetchLeaderboard()
    }

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                BottomAppBar(
                    containerColor = primaryBlue,
                    modifier = Modifier.height(65.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = onNavigateToHome, modifier = Modifier.size(48.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(80.dp))

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = onNavigateToProfile, modifier = Modifier.size(48.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color.White,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }
                    }
                }

                // Highlighted Leaderboard Icon (Orange Circle)
                Box(
                    modifier = Modifier
                        .offset(y = (-10).dp)
                        .size(80.dp)
                        .background(accentOrange, shape = CircleShape)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = "Leaderboard",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
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
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Komponen Top Podium
                    PodiumCard(
                        backgroundColor = lightGrayBg,
                        accentColor = accentOrange,
                        topThree = data.take(3)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown Filter Materi
                    Box {
                        Surface(
                            color = itemBg,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .wrapContentSize()
                                .clickable { expanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentTopic,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                            }
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            topics.forEach { topic ->
                                DropdownMenuItem(
                                    text = { Text(topic) },
                                    onClick = {
                                        viewModel.updateTopicFilter(topic)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // List Leaderboard
                    if (data.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada skor tercatat untuk topik ini", color = Color.Gray)
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            colors = CardDefaults.cardColors(containerColor = listContainerBg),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                Text(
                                    text = "Top Leaderboard",
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue,
                                    fontSize = 18.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    itemsIndexed(data) { index, entry ->
                                        LeaderboardItem(
                                            rank = index + 1,
                                            entry = entry,
                                            itemBackground = itemBg
                                        )
                                    }
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
            .height(220.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                PodiumColumn(
                    rankLabel = "2",
                    entry = topThree.getOrNull(1),
                    barHeight = 90.dp,
                    accentColor = accentColor
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                PodiumColumn(
                    rankLabel = "1",
                    entry = topThree.getOrNull(0),
                    barHeight = 130.dp,
                    accentColor = accentColor
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                PodiumColumn(
                    rankLabel = "3",
                    entry = topThree.getOrNull(2),
                    barHeight = 70.dp,
                    accentColor = accentColor
                )
            }
        }
    }
}

@Composable
fun PodiumColumn(rankLabel: String, entry: LeaderboardEntry?, barHeight: androidx.compose.ui.unit.Dp, accentColor: Color) {
    val projectID = AppwriteClient.PROJECT_ID
    val profileUrl = if (entry?.profilePicId?.isNotEmpty() == true) {
        "https://sgp.cloud.appwrite.io/v1/storage/buckets/${AppwriteClient.BUCKET_PROFILES}/files/${entry.profilePicId}/view?project=$projectID"
    } else {
        "https://sgp.cloud.appwrite.io/v1/avatars/initials?name=${entry?.username ?: "U"}&project=$projectID"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = entry?.username ?: "-",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = profileUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(barHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rankLabel,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, entry: LeaderboardEntry, itemBackground: Color) {
    val projectID = AppwriteClient.PROJECT_ID
    val profileUrl = if (entry.profilePicId.isNotEmpty()) {
        "https://sgp.cloud.appwrite.io/v1/storage/buckets/${AppwriteClient.BUCKET_PROFILES}/files/${entry.profilePicId}/view?project=$projectID"
    } else {
        "https://sgp.cloud.appwrite.io/v1/avatars/initials?name=${entry.username}&project=$projectID"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(itemBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank.",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.width(35.dp)
        )
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = profileUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = entry.username,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = entry.score.toString(),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardPreview() {
    LeaderboardPreviewScreen()
}
