package com.classjava.app.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.classjava.app.viewmodel.QuizViewModel

/**
 * Screen utama untuk pengerjaan soal kuis.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoalQuizScreen(
    onNavigateToLeaderboard: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val primaryBlue = Color(0xFF0F3D6F)
    val accentOrange = Color(0xFFE28743)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Kuis Inheritance", 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                // Keadaan Loading (Awal atau saat simpan skor)
                viewModel.isLoading || viewModel.isSavingScore -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = primaryBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (viewModel.isSavingScore) "Menyimpan Skor..." else "Memuat Soal...",
                            color = primaryBlue
                        )
                    }
                }

                // Keadaan Selesai (Summary)
                viewModel.isFinished -> {
                    SummaryResult(
                        score = viewModel.score, 
                        onBackToLeaderboard = onNavigateToLeaderboard,
                        accentColor = accentOrange
                    )
                }

                // Keadaan Menampilkan Soal
                viewModel.questions.isNotEmpty() -> {
                    QuizPlayContent(
                        viewModel = viewModel,
                        primaryColor = primaryBlue,
                        accentColor = accentOrange
                    )
                }

                // Keadaan Error
                viewModel.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.errorMessage!!, 
                            color = Color.Red, 
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onNavigateToLeaderboard() }) {
                            Text("Kembali")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Komponen konten saat kuis sedang berlangsung.
 */
@Composable
fun QuizPlayContent(
    viewModel: QuizViewModel,
    primaryColor: Color,
    accentColor: Color
) {
    val currentQuiz = viewModel.questions[viewModel.currentIndex]
    val totalSoal = viewModel.questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bar Progress & Skor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Soal ${viewModel.currentIndex + 1} / $totalSoal",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "Skor: ${viewModel.score}",
                color = primaryColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }

        LinearProgressIndicator(
            progress = (viewModel.currentIndex + 1).toFloat() / totalSoal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(10.dp),
            color = accentColor,
            trackColor = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Pertanyaan
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(12.dp)
        ) {
            val rawQuestion = currentQuiz.question.replace("\\n", "\n").replace("\\\n", "\n")
            
            // Logika deteksi kode Java
            val codeKeywords = listOf("class ", "void ", "public ", "static ", "int ", "String ")
            val hasCode = codeKeywords.any { rawQuestion.contains(it) }

            Column(modifier = Modifier.padding(16.dp)) {
                if (hasCode) {
                    // Cari baris pertama yang mengandung keyword kode
                    val lines = rawQuestion.split("\n")
                    val codeStartIndex = lines.indexOfFirst { line -> 
                        codeKeywords.any { kw -> line.contains(kw) } 
                    }

                    if (codeStartIndex != -1) {
                        // Bagian Deskripsi (sebelum kode)
                        val description = lines.subList(0, codeStartIndex).joinToString("\n").trim()
                        if (description.isNotEmpty()) {
                            Text(
                                text = description,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        // Bagian Blok Kode (dari index pertama kode sampai akhir)
                        val codeBlock = lines.subList(codeStartIndex, lines.size).joinToString("\n").trim()
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = codeBlock,
                                fontSize = 13.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 18.sp
                            )
                        }
                    } else {
                        // Fallback jika deteksi index gagal
                        Text(text = rawQuestion, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Teks Biasa
                    Text(
                        text = rawQuestion,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pilihan Jawaban (A, B, C, D)
        currentQuiz.options.forEachIndexed { index, option ->
            val label = when(index) {
                0 -> "A. "
                1 -> "B. "
                2 -> "C. "
                3 -> "D. "
                else -> ""
            }

            val isSelected = viewModel.selectedAnswer == option
            val isCorrect = option == currentQuiz.correctAnswer
            
            // Logika Warna Dinamis
            val containerColor = when {
                !viewModel.isAnswered -> Color.White
                isCorrect -> Color(0xFF4CAF50) // Hijau jika ini jawaban benar
                isSelected && !isCorrect -> Color(0xFFF44336) // Merah jika user salah pilih
                else -> Color.White
            }

            val contentColor = if (viewModel.isAnswered && (isCorrect || (isSelected && !isCorrect))) {
                Color.White 
            } else {
                Color.Black
            }

            val borderColor = if (isSelected && !viewModel.isAnswered) {
                accentColor 
            } else {
                Color.LightGray
            }

            OutlinedButton(
                onClick = { viewModel.submitAnswer(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                ),
                enabled = !viewModel.isAnswered
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label + option,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Navigasi
        if (viewModel.isAnswered) {
            Button(
                onClick = { viewModel.nextQuestion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (viewModel.currentIndex == totalSoal - 1) "Selesai & Simpan Skor" else "Soal Selanjutnya",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Komponen tampilan hasil akhir kuis.
 */
@Composable
fun SummaryResult(
    score: Int, 
    onBackToLeaderboard: () -> Unit,
    accentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kuis Selesai!", 
            fontSize = 32.sp, 
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F3D6F)
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Skor Akhir Anda", 
            fontSize = 18.sp,
            color = Color.Gray
        )
        
        Text(
            text = "$score", 
            fontSize = 80.sp, 
            fontWeight = FontWeight.Black, 
            color = accentColor
        )
        
        Text(
            text = "Poin", 
            fontSize = 20.sp, 
            fontWeight = FontWeight.Bold,
            color = accentColor
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onBackToLeaderboard,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F3D6F))
        ) {
            Text(
                text = "Lihat Leaderboard",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
