package com.classjava.app.repository

import com.classjava.app.config.AppwriteClient
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Session
import io.appwrite.models.User as AppwriteUser
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.models.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class AuthRepository {

    private val accountService by lazy { AppwriteClient.account }
    private val storageService by lazy { AppwriteClient.storage }
    private val databaseService by lazy { AppwriteClient.databases }

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

    // 3. Upload Foto Profil ke Appwrite Storage
    suspend fun uploadProfilePicture(file: java.io.File): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = storageService.createFile(
                    bucketId = AppwriteClient.BUCKET_PROFILES,
                    fileId = ID.unique(),
                    file = io.appwrite.models.InputFile.fromFile(file),
                    permissions = listOf(
                        Permission.read(Role.any()), // Siapa saja bisa lihat
                        Permission.write(Role.users()) // Hanya user terautentikasi bisa tulis
                    )
                )
                Result.success(response.id)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }

    // 4. Simpan Data User ke Databases
    suspend fun saveUserData(userId: String, username: String, email: String, profileId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                databaseService.createDocument(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_USERS,
                    documentId = userId,
                    data = mapOf(
                        "username" to username,
                        "email" to email,
                        "profile_id" to profileId
                    )
                )
                Result.success(Unit)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }

    // 5. Ambil Data User tambahan (termasuk profile_id) dari Database
    suspend fun getUserDataFromDb(userId: String): Result<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val document = databaseService.getDocument(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_USERS,
                    documentId = userId
                )
                Result.success(document.data)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }

    // 6. Dapatkan URL Foto Profil asli
    fun getProfilePictureUrl(fileId: String): String {
        return "https://sgp.cloud.appwrite.io/v1/storage/buckets/${AppwriteClient.BUCKET_PROFILES}/files/$fileId/view?project=6a126b48002b3fb6cbe7"
    }

    // 7. Update profile_id di Database
    suspend fun updateProfilePictureId(userId: String, newFileId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                databaseService.updateDocument(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_USERS,
                    documentId = userId,
                    data = mapOf("profile_id" to newFileId)
                )
                Result.success(Unit)
            } catch (e: AppwriteException) {
                Result.failure(e)
            }
        }
    }

    // 8. Hapus file lama di Storage (Opsional, untuk kebersihan)
    suspend fun deleteFile(fileId: String) {
        withContext(Dispatchers.IO) {
            try {
                storageService.deleteFile(
                    bucketId = AppwriteClient.BUCKET_PROFILES,
                    fileId = fileId
                )
            } catch (_: Exception) {}
        }
    }

    // 9. Cek Status Login
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

    // 10. Fitur Keluar Aplikasi (Logout)
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