package com.classjava.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.classjava.app.R
import com.classjava.app.viewmodel.AuthState
import com.classjava.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    // ViewModel dipakai bersama dengan LoginScreen (satu instance)
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    // Observe state dari ViewModel
    val authState by authViewModel.authState.collectAsState()

    var username by remember { mutableStateOf("") }
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
                Toast.makeText(context, "Pendaftaran Berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                authViewModel.resetState()
                onRegisterSuccess()
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

            Spacer(modifier = Modifier.height(32.dp))

            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = primaryBlue
                )
            } else {
                Button(
                    onClick = {
                        // View hanya teruskan perintah ke ViewModel
                        authViewModel.register(username, email, password)
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
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
                            color = Color(0xFFE28743),
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