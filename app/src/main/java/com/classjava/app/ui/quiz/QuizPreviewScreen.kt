package com.classjava.app.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPreviewScreen(
    topicRoute: String,
    onNavigateBack: () -> Unit,
    onMulaiKuis: (String) -> Unit
) {
    val primaryBlue = Color(0xFF0F3D6F)
    val accentOrange = Color(0xFFE28743)
    val codeBackground = Color(0xFF1E1E2E)

    // Ambil konten dari QuizPreviewContent.kt
    val content = TopicPreviewData.allPreviews[topicRoute]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kuis",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                windowInsets = TopAppBarDefaults.windowInsets,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        },
        bottomBar = {
            if (content != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryBlue)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.White, shape = RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = content.icon,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = content.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { onMulaiKuis(topicRoute) },
                            colors = ButtonDefaults.buttonColors(containerColor = accentOrange),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Mulai",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->

        if (content == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Materi tidak ditemukan", color = Color.Gray)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header: ikon + judul
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(primaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = content.icon,
                        contentDescription = null,
                        tint = primaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = content.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pengertian — diambil dari QuizPreviewContent.kt
            Text(
                text = content.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Contoh Kode",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = primaryBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Blok kode — diambil dari QuizPreviewContent.kt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(codeBackground, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    Text(
                        text = content.codeExample,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFCDD6F4),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizPreviewInheritancePreview() {
    QuizPreviewScreen(topicRoute = "quiz/inheritance", onNavigateBack = {}, onMulaiKuis = {})
}

@Preview(showBackground = true)
@Composable
fun QuizPreviewArraysPreview() {
    QuizPreviewScreen(topicRoute = "quiz/arrays", onNavigateBack = {}, onMulaiKuis = {})
}

@Preview(showBackground = true)
@Composable
fun QuizPreviewLoopingPreview() {
    QuizPreviewScreen(topicRoute = "quiz/looping", onNavigateBack = {}, onMulaiKuis = {})
}