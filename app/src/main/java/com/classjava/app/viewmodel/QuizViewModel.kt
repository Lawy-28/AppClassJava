package com.classjava.app.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classjava.app.config.AppwriteClient
import com.classjava.app.model.Quiz
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.launch

/**
 * ViewModel untuk menangani logika kuis secara dinamis berdasarkan topik.
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
    var isLoading by mutableStateOf(false)
    var isFinished by mutableStateOf(false)
    var isSavingScore by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    
    // State untuk penjelasan soal yang aktif
    var selectedExplanation by mutableStateOf<String?>(null)
    
    // Menyimpan ID topik yang sedang aktif dikerjakan
    private var currentTopicId: String = ""

    init {
        // Init kosong, pemanggilan dilakukan secara dinamis via loadQuizzesByTopic
    }

    /**
     * Memuat daftar kuis berdasarkan topik tertentu dan mereset semua state ke nilai awal.
     * @param topicId ID topik kuis (misal: inheritance, arrays, looping)
     */
    fun loadQuizzesByTopic(topicId: String) {
        currentTopicId = topicId
        
        // Reset seluruh state kuis agar bersih untuk pengerjaan baru
        questions = emptyList()
        currentIndex = 0
        selectedAnswer = null
        isAnswered = false
        score = 0
        isFinished = false
        errorMessage = null
        selectedExplanation = null
        
        viewModelScope.launch {
            try {
                isLoading = true
                Log.d("QUIZ_DEBUG", "Memuat soal untuk topik: $topicId")
                
                val response = AppwriteClient.databases.listDocuments(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_QUIZZES,
                    queries = listOf(
                        Query.equal("topic_id", topicId)
                    )
                )

                // Map dokumen ke model Quiz, acak, dan ambil maksimal 10 soal
                val allQuizzes = response.documents.map { doc ->
                    Quiz.fromMap(doc.id, doc.data)
                }
                
                if (allQuizzes.isNotEmpty()) {
                    questions = allQuizzes.shuffled().take(10)
                } else {
                    errorMessage = "Tidak ada soal yang ditemukan untuk topik: $topicId"
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
        if (isAnswered) return // Mencegah klik ganda pada soal yang sama
        
        selectedAnswer = answer
        isAnswered = true
        
        // Mengisi penjelasan dari soal yang sedang aktif
        selectedExplanation = questions[currentIndex].explanation
        
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
            selectedExplanation = null // Reset penjelasan untuk soal berikutnya
        } else {
            submitFinalScore()
        }
    }

    /**
     * Menyimpan skor akhir user ke koleksi Leaderboard di Appwrite.
     * Menggunakan logika "High Score Upsert" berdasarkan user_id dan topic_id.
     */
    private fun submitFinalScore() {
        viewModelScope.launch {
            try {
                isSavingScore = true
                Log.d("QUIZ_DEBUG", "Menyimpan skor kuis topik: $currentTopicId")
                
                val userAccount = AppwriteClient.account.get()
                val currentUserId = userAccount.id
                val userName = userAccount.name.ifBlank { userAccount.email }
                val newScore = score.toInt()

                // 1. Cek apakah user sudah punya rekor di topik ini
                val existingDocs = AppwriteClient.databases.listDocuments(
                    databaseId = AppwriteClient.DATABASE_ID,
                    collectionId = AppwriteClient.COLLECTION_LEADERBOARD,
                    queries = listOf(
                        Query.equal("user_id", currentUserId),
                        Query.equal("topic_id", currentTopicId)
                    )
                )

                if (existingDocs.documents.isEmpty()) {
                    // 2. JIKA BELUM ADA: Buat dokumen baru dengan hak akses publik
                    AppwriteClient.databases.createDocument(
                        databaseId = AppwriteClient.DATABASE_ID,
                        collectionId = AppwriteClient.COLLECTION_LEADERBOARD,
                        documentId = ID.unique(),
                        data = mapOf(
                            "user_id" to currentUserId,
                            "student_name" to userName,
                            "topic_id" to currentTopicId,
                            "score" to newScore
                        ),
                        permissions = listOf(Permission.read(Role.any()))
                    )
                } else {
                    // 3. JIKA SUDAH ADA: Update jika skor baru lebih tinggi (rekor baru)
                    val doc = existingDocs.documents[0]
                    val oldScore = (doc.data["score"] as? Number)?.toInt() ?: 0

                    if (newScore > oldScore) {
                        AppwriteClient.databases.updateDocument(
                            databaseId = AppwriteClient.DATABASE_ID,
                            collectionId = AppwriteClient.COLLECTION_LEADERBOARD,
                            documentId = doc.id,
                            data = mapOf("score" to newScore)
                        )
                    }
                }
                
                isFinished = true
                isSavingScore = false
            } catch (e: AppwriteException) {
                Log.e("QUIZ_DEBUG", "Error simpan skor: ${e.message}")
                errorMessage = "Gagal menyimpan skor: ${e.message}"
                isFinished = true
                isSavingScore = false
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan: ${e.message}"
                isFinished = true
                isSavingScore = false
            }
        }
    }
}
