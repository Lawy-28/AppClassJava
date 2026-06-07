package com.classjava.app.ui.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.classjava.app.R
import com.classjava.app.repository.AuthRepository
import com.classjava.app.utils.FileUtils
import com.classjava.app.viewmodel.AuthState
import com.classjava.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // 1. State untuk menyimpan URI foto yang dipilih
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // 2. Launcher untuk membuka galeri HP
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val primaryBlue = Color(0xFF0F3D6F)
    val backgroundCard = Color(0xFFF5F7FA)
    val strokeColor = Color(0xFFCBD5E1)
    val accentOrange = Color(0xFFE28743)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_class_java),
            contentDescription = "Logo Class Java",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 8.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Class [Java]",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = primaryBlue
        )

        Text(
            text = "Mastering Programming & Coding Fundamentals",
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundCard, shape = RoundedCornerShape(16.dp))
                .border(1.dp, strokeColor, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Komponen Avatar (Tombol Pilih Foto)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp),
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
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }
                }

                // Tombol Plus (+)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            }

            // Input Fields
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Username", fontWeight = FontWeight.Bold, color = primaryBlue, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                CustomInput(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Username",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Email", fontWeight = FontWeight.Bold, color = primaryBlue, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                CustomInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Kata Sandi", fontWeight = FontWeight.Bold, color = primaryBlue, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                CustomInput(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Kata Sandi",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordToggle = { passwordVisible = !passwordVisible },
                    keyboardType = KeyboardType.Password
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = primaryBlue)
            } else {
                Button(
                    onClick = {
                        if (username.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        isLoading = true
                        coroutineScope.launch {
                            // 1. Registrasi Akun Baru
                            val registerResult = authRepository.register(email, password, username)
                            
                            registerResult.onSuccess { user ->
                                // 2. Login Otomatis (Wajib untuk mendapatkan Session agar bisa Upload/DB)
                                val loginResult = authRepository.login(email, password)
                                
                                loginResult.onSuccess {
                                    var uploadedFileId = ""
                                    
                                    // 3. Cek apakah user memilih foto, jika iya lakukan upload
                                    selectedImageUri?.let { uri ->
                                        val file = FileUtils.uriToFile(context, uri)
                                        if (file != null) {
                                            // Jalankan upload secara sekuensial
                                            val uploadResult = authRepository.uploadProfilePicture(file)
                                            uploadResult.onSuccess { fileId ->
                                                uploadedFileId = fileId
                                            }
                                        }
                                    }
                                    
                                    // 4. Langkah Terakhir: Simpan ke Database
                                    // uploadedFileId sudah terisi jika upload sukses, atau "" jika skip/gagal
                                    authRepository.saveUserData(
                                        userId = user.id,
                                        username = username,
                                        email = email,
                                        profileId = uploadedFileId
                                    ).onSuccess {
                                        isLoading = false
                                        // Update state di ViewModel agar HomeScreen langsung menampilkan foto
                                        authViewModel.loadCurrentUser()
                                        Toast.makeText(context, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess()
                                    }.onFailure {
                                        isLoading = false
                                        Toast.makeText(context, "Gagal simpan data: ${it.message}", Toast.LENGTH_LONG).show()
                                    }
                                    
                                }.onFailure {
                                    isLoading = false
                                    Toast.makeText(context, "Gagal login otomatis: ${it.message}", Toast.LENGTH_LONG).show()
                                }

                            }.onFailure {
                                isLoading = false
                                Toast.makeText(context, "Registrasi Gagal: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Daftar", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Sudah punya akun? ", fontSize = 12.sp, color = Color.Gray)
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(20.dp)
                    ) {
                        Text(
                            "Masuk",
                            fontSize = 12.sp,
                            color = accentOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
}