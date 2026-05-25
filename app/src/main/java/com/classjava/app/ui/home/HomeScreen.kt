package com.classjava.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.classjava.app.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogoutSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    var studentName by remember { mutableStateOf("Siswa") }

    // Ambil nama siswa yang sedang login dari Appwrite
    LaunchedEffect(Unit) {
        authRepository.getCurrentUser().onSuccess { user ->
            studentName = user.name
        }.onFailure {
            // Jika gagal, biarkan default "Siswa"
        }
    }

    // Warna tema sesuai desain Anda
    val primaryBlue = Color(0xFF0F3D6F)
    val backgroundCard = Color(0xFFF5F7FA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue),
                actions = {
                    // Menggunakan TextButton bertuliskan Keluar agar tidak error nyari icon extend
                    TextButton(onClick = {
                        coroutineScope.launch {
                            authRepository.logout().onSuccess {
                                onLogoutSuccess()
                            }.onFailure {
                                // Handle failure if needed
                            }
                        }
                    }) {
                        Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
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
            // Kotak Selamat Datang (Welcome Card)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = backgroundCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = primaryBlue,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Selamat Datang,",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = studentName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Box Informasi Tambahan
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundCard, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Materi Utama: Fundamentals Java",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status Kuis: Belum Dimulai",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE28743) // Aksen Oranye sesuai desain
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Utama Mulai Kuis (Menggunakan ArrowForward yang pasti ada di library default)
            Button(
                onClick = { /* Tempat rute halaman soal kuis */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(
                    text = "Mulai Kuis Java",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}