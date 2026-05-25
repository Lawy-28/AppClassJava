package com.classjava.app.config

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases

object AppwriteClient {
    // 1. Deklarasi properti SDK Appwrite yang akan diakses oleh semua Repository
    private lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases

    // 2. Kumpulan ID Penting (Asli dari proyek Appwrite Anda)
    const val DATABASE_ID = "6a126bd2003a8cf7db6f"
    const val COLLECTION_TOPICS = "topics"
    const val COLLECTION_QUIZZES = "quizzes"
    const val COLLECTION_LEADERBOARD = "leaderboard"

    // 3. Fungsi inisialisasi yang wajib dipanggil sekali saat aplikasi pertama kali dibuka
    fun initialize(context: Context) {
        client = Client(context)
            .setEndpoint("https://sgp.cloud.appwrite.io/v1")
            .setProject("6a126b48002b3fb6cbe7") // Project ID Anda

        account = Account(client)
        databases = Databases(client)
    }
}