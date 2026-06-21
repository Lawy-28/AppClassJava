package com.classjava.app.model

/**
 * Model data untuk kuis yang dipetakan dari dokumen Appwrite.
 */
data class Quiz(
    val id: String,
    val topicId: String,
    val question: String,
    val correctAnswer: String,
    val explanation: String,
    val options: List<String>
) {
    companion object {
        /**
         * Fungsi helper untuk mengonversi data Map dari Appwrite menjadi objek Quiz.
         * Atribut 'options' diasumsikan disimpan sebagai String yang dipisahkan koma (e.g. "A,B,C,D").
         */
        fun fromMap(id: String, map: Map<String, Any>): Quiz {
            val optionsRaw = map["options"] as? String ?: ""
            return Quiz(
                id = id,
                topicId = map["topic_id"] as? String ?: "",
                question = map["question"] as? String ?: "",
                correctAnswer = map["correct_answer"] as? String ?: "",
                explanation = map["explanation"] as? String ?: "",
                options = optionsRaw.split(",").map { it.trim() }
            )
        }
    }
}
