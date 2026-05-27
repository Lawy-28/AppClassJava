package com.classjava.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.classjava.app.R
import com.classjava.app.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var isLoading by remember { mutableStateOf(value = false) }

    val primaryBlue = Color(0xFF0F3D6F)
    val backgroundCard = Color(0xFFF5F7FA)
    val strokeColor = Color(0xFFCBD5E1)

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
                .background(
                    backgroundCard,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    1.dp,
                    strokeColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {

            // EMAIL
            Text(
                "Email",
                fontWeight = FontWeight.Bold,
                color = primaryBlue,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),

                placeholder = {
                    Text(
                        "Masukkan Email",
                        color = Color.Gray
                    )
                },

                textStyle = TextStyle(
                    color = Color.Black
                ),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    unfocusedBorderColor = strokeColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,

                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,

                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),

                shape = RoundedCornerShape(24.dp),
                singleLine = true,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PASSWORD
            Text(
                "Kata Sandi",
                fontWeight = FontWeight.Bold,
                color = primaryBlue,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),

                placeholder = {
                    Text(
                        "Masukkan Password",
                        color = Color.Gray
                    )
                },

                textStyle = TextStyle(
                    color = Color.Black
                ),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    unfocusedBorderColor = strokeColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,

                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,

                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),

                shape = RoundedCornerShape(24.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description, tint = primaryBlue)
                    }
                },
                singleLine = true,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ),
                    color = primaryBlue
                )

            } else {

                Button(
                    onClick = {

                        if (
                            email.isNotEmpty() &&
                            password.isNotEmpty()
                        ) {

                            isLoading = true

                            coroutineScope.launch {

                                val result =
                                    authRepository.login(
                                        email.trim(),
                                        password.trim()
                                    )

                                isLoading = false

                                result.onSuccess {

                                    Toast.makeText(
                                        context,
                                        "Login Berhasil!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    onLoginSuccess()

                                }.onFailure {
                                    Toast.makeText(
                                        context,
                                        "Kata Sandi atau Email yang anda masukkan salah",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        } else {

                            Toast.makeText(
                                context,
                                "Semua data wajib diisi!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },

                    modifier = Modifier
                        .width(180.dp)
                        .height(48.dp)
                        .align(
                            Alignment.CenterHorizontally
                        ),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    ),

                    shape = RoundedCornerShape(24.dp)
                ) {

                    Text(
                        "Masuk",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.Center
                ) {

                    Text(
                        "Belum punya akun? ",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    TextButton(
                        onClick = onNavigateToRegister,
                        contentPadding =
                            PaddingValues(0.dp),

                        modifier =
                            Modifier.height(20.dp)
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