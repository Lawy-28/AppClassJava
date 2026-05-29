package com.classjava.app.repository

import com.classjava.app.config.AppwriteClient
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
// Menggunakan alias AppwriteUser agar tidak bentrok dengan Account Service
import io.appwrite.models.User as AppwriteUser

class AuthRepository {

    private val accountService by lazy { AppwriteClient.account }

    // 1. Fitur Daftar Akun Baru (Register)
    suspend fun register(email: String, password: String, name: String): Result<AppwriteUser<Map<String, Any>>> {
        return withContext(Dispatchers.IO) {
            try {
                // Generate ID unik otomatis maksimal 20 karakter untuk user baru
                val userId = UUID.randomUUID().toString().replace("-", "").substring(0, 20)
                val user = accountService.create(
                    userId = userId,
                    email = email,
                    password = password,
                    name = name
                )
                Result.success(user)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }

    // 2. Fitur Masuk Aplikasi (Login)
    suspend fun login(email: String, password: String): Result<Session> {
        return withContext(Dispatchers.IO) {
            try {
                // Bersihkan sesi lama jika masih nyangkut agar bisa login baru
                try {
                    accountService.deleteSession(sessionId = "current")
                } catch (_: Exception) { /* Abaikan */ }

                // Membuat sesi login menggunakan email & password
                val session = accountService.createEmailPasswordSession(
                    email = email,
                    password = password,
                )
                Result.success(session)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }

    // 3. Cek Status Login (Biar user tidak perlu login ulang tiap buka aplikasi)
    suspend fun getCurrentUser(): Result<AppwriteUser<Map<String, Any>>> {
        return withContext(Dispatchers.IO) {
            try {
                val user = accountService.get()
                Result.success(user)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }

    // 4. Fitur Keluar Aplikasi (Logout)
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Menghapus sesi login saat ini dari server
                accountService.deleteSession(sessionId = "current")
                Result.success(Unit)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }
}