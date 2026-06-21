package com.classjava.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classjava.app.config.AppwriteClient
import com.classjava.app.model.Quiz
import android.util.Log
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.launch

/**
 * ViewModel untuk menangani logika permainan kuis Inheritance.
 */
class QuizViewModel : ViewModel() {

    // State untuk daftar soal
    var questions by mutableStateOf<List<Quiz>>(emptyList())
    
    // State navigasi dan progres
    var currentIndex by mutableStateOf(0)
    var selectedAnswer by mutableStateOf<String?>(null)
    var isAnswered by mutableStateOf(false)
    
    // State skor dan status akhir
    var score by mutableStateOf(0)
    var isLoading by mutableStateOf(true)
    var isFinished by mutableStateOf(false)
    var isSavingScore by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchInheritanceQuizzes()
    }

    /**
     * Mengambil data kuis dari Appwrite dengan filter topic_id = inheritance.
     */
    private fun fetchInheritanceQuizzes() {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null
                
                val response = AppwriteClient.databases.listDocuments(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_QUIZZES,
                    queries = listOf(
                        Query.equal("topic_id", "inheritance")
                    )
                )

                // Map dokumen ke model Quiz, acak, dan ambil 10 soal
                val allQuizzes = response.documents.map { doc ->
                    Quiz.fromMap(doc.id, doc.data)
                }
                
                if (allQuizzes.isNotEmpty()) {
                    questions = allQuizzes.shuffled().take(10)
                } else {
                    errorMessage = "Tidak ada soal yang ditemukan untuk topik ini."
                }
                
                isLoading = false
            } catch (e: AppwriteException) {
                errorMessage = "Gagal memuat kuis: ${e.message}"
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan: ${e.message}"
                isLoading = false
            }
        }
    }

    /**
     * Memproses jawaban yang dipilih oleh user.
     */
    fun submitAnswer(answer: String) {
        if (isAnswered) return // Mencegah klik ganda
        
        selectedAnswer = answer
        isAnswered = true
        
        // Cek kebenaran jawaban dan update skor (100 poin per soal benar)
        if (answer == questions[currentIndex].correctAnswer) {
            score += 100
        }
    }

    /**
     * Berpindah ke soal berikutnya atau memicu penyimpanan skor jika sudah di soal terakhir.
     */
    fun nextQuestion() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            selectedAnswer = null
            isAnswered = false
        } else {
            submitFinalScore()
        }
    }

    /**
     * Menyimpan skor akhir user ke koleksi Leaderboard di Appwrite.
     * Menggunakan logika "High Score Upsert": hanya update jika skor baru lebih tinggi.
     */
    fun submitFinalScore() {
        viewModelScope.launch {
            try {
                isSavingScore = true
                Log.d("QUIZ_DEBUG", "Memulai proses simpan skor...")
                
                // 1. Dapatkan data user dari Appwrite Account Service
                val userAccount = AppwriteClient.account.get()
                val currentUserId = userAccount.id
                val userName = userAccount.name.ifBlank { userAccount.email }
                val newScore = score.toInt()
                
                Log.d("QUIZ_DEBUG", "User ID: $currentUserId, Name: $userName, Score: $newScore")

                // 2. Query pengecekan berdasarkan user_id
                val existingDocs = AppwriteClient.databases.listDocuments(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_LEADERBOARD,
                    queries = listOf(Query.equal("user_id", currentUserId))
                )

                if (existingDocs.documents.isEmpty()) {
                    // 3. JIKA BELUM ADA: Buat dokumen baru
                    Log.d("QUIZ_DEBUG", "Data user belum ada, membuat dokumen baru...")
                    AppwriteClient.databases.createDocument(
                        databaseId = AppwriteClient.DATABASE_ID,
                        collectionId = AppwriteClient.COLLECTION_LEADERBOARD,
                        documentId = ID.unique(),
                        data = mapOf(
                            "user_id" to currentUserId,
                            "student_name" to userName,
                            "topic_id" to "inheritance",
                            "score" to newScore
                        ),
                        permissions = listOf(Permission.read(Role.any()))
                    )
                    Log.d("QUIZ_DEBUG", "Berhasil membuat dokumen baru.")
                } else {
                    // 4. JIKA SUDAH ADA: Bandingkan skor
                    val doc = existingDocs.documents[0]
                    val oldScore = (doc.data["score"] as? Number)?.toInt() ?: 0
                    Log.d("QUIZ_DEBUG", "Data user ditemukan. Skor lama: $oldScore, Skor baru: $newScore")

                    if (newScore > oldScore) {
                        Log.d("QUIZ_DEBUG", "Skor baru lebih tinggi, melakukan update...")
                        AppwriteClient.databases.updateDocument(
                            databaseId = AppwriteClient.DATABASE_ID,
                            collectionId = AppwriteClient.COLLECTION_LEADERBOARD,
                            documentId = doc.id,
                            data = mapOf(
                                "score" to newScore
                            )
                        )
                        Log.d("QUIZ_DEBUG", "Berhasil update skor tertinggi.")
                    } else {
                        Log.d("QUIZ_DEBUG", "Skor baru tidak lebih tinggi, mengabaikan update.")
                    }
                }
                
                isFinished = true
                isSavingScore = false
            } catch (e: AppwriteException) {
                Log.e("QUIZ_DEBUG", "Error Appwrite: ${e.message}")
                errorMessage = "Gagal memproses skor: ${e.message}"
                isFinished = true
                isSavingScore = false
            } catch (e: Exception) {
                Log.e("QUIZ_DEBUG", "Error Sistem: ${e.message}")
                errorMessage = "Terjadi kesalahan: ${e.message}"
                isFinished = true
                isSavingScore = false
            }
        }
    }
}
