package com.classjava.app.ui.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.classjava.app.repository.AuthRepository
import com.classjava.app.utils.FileUtils
import com.classjava.app.viewmodel.AuthState
import com.classjava.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogoutSuccess: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    val authState by authViewModel.authState.collectAsState()
    val studentName by authViewModel.currentUserName.collectAsState()
    val studentEmail by authViewModel.currentUserEmail.collectAsState()
    val profileUrl by authViewModel.profilePictureUrl.collectAsState()
    val totalScore by authViewModel.currentUserScore.collectAsState()

    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            coroutineScope.launch {
                val file = FileUtils.uriToFile(context, it)
                if (file != null) {
                    // 1. Upload foto baru
                    val uploadResult = authRepository.uploadProfilePicture(file)
                    uploadResult.onSuccess { fileId ->
                        // 2. Ambil user ID saat ini
                        authRepository.getCurrentUser().onSuccess { user ->
                            // 3. Update database
                            authRepository.updateProfilePictureId(user.id, fileId).onSuccess {
                                // 4. Update UI State
                                val newUrl = authRepository.getProfilePictureUrl(fileId)
                                authViewModel.updateProfileUrl(newUrl)
                                Toast.makeText(context, "Foto profil diperbarui!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.onFailure {
                        Toast.makeText(context, "Gagal upload: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                isUploading = false
            }
        }
    }

    val primaryBlue = Color(0xFF0F3D6F)
    val backgroundCard = Color(0xFFE9EDF2)
    val accentOrange = Color(0xFFE28743)
    val logoutRed = Color(0xFF8B0000)

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authViewModel.resetState()
            onLogoutSuccess()
        }
    }

    Scaffold(
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

                        Spacer(modifier = Modifier.width(80.dp))

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

            // Avatar dengan tombol ganti foto (+)
            Box(
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Tampilan Foto Profil
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(color = primaryBlue)
                    } else if (profileUrl != null) {
                        AsyncImage(
                            model = profileUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                // Tombol Plus (+) untuk ganti foto
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Change Photo",
                        modifier = Modifier.size(22.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama dari ViewModel
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
                    .fillMaxWidth(),
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
                            // Nama dari ViewModel
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
                            // Email dari ViewModel
                            Text(text = studentEmail, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Score Row (Baru)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = accentOrange,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Total Skor: $totalScore Poin",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Logout — cukup panggil fungsi ViewModel
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = logoutRed)
            } else {
                OutlinedButton(
                    onClick = {
                        // View hanya memanggil fungsi ViewModel, tidak ada logika di sini
                        authViewModel.logout()
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