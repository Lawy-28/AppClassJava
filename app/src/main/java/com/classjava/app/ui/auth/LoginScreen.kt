package com.classjava.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.classjava.app.R
import com.classjava.app.viewmodel.AuthState
import com.classjava.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    // ViewModel diinject dari luar (atau dibuat otomatis oleh viewModel())
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    // Observe state dari ViewModel, bukan buat logika sendiri di sini
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF0F3D6F)
    val backgroundCard = Color(0xFFF5F7FA)
    val strokeColor = Color(0xFFCBD5E1)

    // Reaksi terhadap perubahan state dari ViewModel
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                authViewModel.resetState() // Reset agar tidak trigger ulang
                onLoginSuccess()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

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
                .size(130.dp)
                .padding(bottom = 8.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Class [Java]",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = primaryBlue
        )

        Text(
            text = "Mastering Programming & Coding Fundamentals",
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundCard, shape = RoundedCornerShape(16.dp))
                .border(1.dp, strokeColor, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
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

            Spacer(modifier = Modifier.height(32.dp))

            // Tampilkan loading indicator jika state sedang Loading
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = primaryBlue
                )
            } else {
                Button(
                    onClick = {
                        // View hanya meneruskan perintah ke ViewModel, tidak ada logika di sini
                        authViewModel.login(email, password)
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Masuk", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Belum punya akun? ", fontSize = 12.sp, color = Color.Gray)
                    TextButton(
                        onClick = onNavigateToRegister,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(20.dp)
                    ) {
                        Text(
                            "Daftar Akun Baru",
                            fontSize = 12.sp,
                            color = Color(0xFFE28743),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Komponen CustomInput dipindahkan ke file terpisah idealnya,
// tapi untuk sekarang tetap di sini karena dipakai RegisterScreen juga
@Composable
fun CustomInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(52.dp)
                    .background(Color(0xFF0F3D6F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    if (isPassword && onPasswordToggle != null) {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = onPasswordToggle) {
                            Icon(
                                imageVector = image,
                                contentDescription = null,
                                tint = Color(0xFF0F3D6F),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                textStyle = TextStyle(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {})
}