package com.classjava.app.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TopicPreviewContent(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val codeExample: String
)

// Hanya 3 materi sesuai yang ada di HomeScreen
object TopicPreviewData {

    val allPreviews = mapOf(

        "quiz/inheritance" to TopicPreviewContent(
            title = "Inheritance",
            icon = Icons.Default.AccountTree,
            description = "Inheritance adalah konsep pemrograman berorientasi objek (OOP) " +
                    "di mana sebuah kelas dapat mewarisi atribut dan metode dari kelas lain. " +
                    "Kelas yang mewarisi disebut subclass, sedangkan kelas yang diwarisi " +
                    "disebut superclass. Inheritance memungkinkan penggunaan ulang kode " +
                    "dan membuat struktur program lebih terorganisir.",
            codeExample = """
// Superclass
class Hewan {
    String nama;

    void makan() {
        System.out.println(nama + " sedang makan");
    }
}

// Subclass mewarisi Hewan
class Kucing extends Hewan {

    void bersuara() {
        System.out.println(nama + " berkata: Meow!");
    }
}

public class Main {
    public static void main(String[] args) {
        Kucing k = new Kucing();
        k.nama = "Kitty";
        k.makan();     // Dari superclass
        k.bersuara();  // Dari subclass
    }
}
            """.trimIndent()
        ),

        "quiz/arrays" to TopicPreviewContent(
            title = "Arrays",
            icon = Icons.Default.List,
            description = "Array adalah struktur data yang digunakan untuk menyimpan " +
                    "banyak nilai dalam satu variabel dengan tipe data yang sama. " +
                    "Setiap elemen array memiliki indeks yang dimulai dari 0. " +
                    "Array sangat berguna ketika kita ingin menyimpan dan mengakses " +
                    "kumpulan data secara terstruktur.",
            codeExample = """
public class ContohArray {
    public static void main(String[] args) {

        // Deklarasi array dengan 5 elemen
        int[] angka = {10, 20, 30, 40, 50};

        // Akses elemen berdasarkan indeks
        System.out.println(angka[0]); // Output: 10
        System.out.println(angka[2]); // Output: 30

        // Tampilkan semua elemen
        for (int i = 0; i < angka.length; i++) {
            System.out.println("Indeks " + i
                + " = " + angka[i]);
        }
    }
}
            """.trimIndent()
        ),

        "quiz/looping" to TopicPreviewContent(
            title = "Looping",
            icon = Icons.Default.SyncAlt,
            description = "Looping (perulangan) adalah struktur kontrol yang digunakan " +
                    "untuk mengeksekusi blok kode secara berulang selama kondisi " +
                    "tertentu terpenuhi. Java memiliki tiga jenis loop utama yaitu " +
                    "for, while, dan do-while. Looping sangat berguna untuk memproses " +
                    "data dalam jumlah besar secara efisien.",
            codeExample = """
public class ContohLooping {
    public static void main(String[] args) {

        // For loop: cetak angka 1 sampai 5
        for (int i = 1; i <= 5; i++) {
            System.out.println("For: " + i);
        }

        // While loop
        int j = 1;
        while (j <= 3) {
            System.out.println("While: " + j);
            j++;
        }

        // Do-while loop
        int k = 1;
        do {
            System.out.println("Do-While: " + k);
            k++;
        } while (k <= 3);
    }
}
            """.trimIndent()
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPreviewScreen(
    topicRoute: String,
    onNavigateBack: () -> Unit,
    onMulaiKuis: (String) -> Unit
) {
    val primaryBlue = Color(0xFF0F3D6F)
    val accentOrange = Color(0xFFE28743)
    val codeBackground = Color(0xFF1E1E2E)

    val content = TopicPreviewData.allPreviews[topicRoute]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kuis",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                windowInsets = TopAppBarDefaults.windowInsets,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        },
        bottomBar = {
            if (content != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryBlue)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.White, shape = RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = content.icon,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = content.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { onMulaiKuis(topicRoute) },
                            colors = ButtonDefaults.buttonColors(containerColor = accentOrange),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Mulai",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->

        if (content == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Materi tidak ditemukan", color = Color.Gray)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header: ikon + judul
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(primaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = content.icon,
                        contentDescription = null,
                        tint = primaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = content.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pengertian
            Text(
                text = content.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Label contoh kode
            Text(
                text = "Contoh Kode",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = primaryBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Blok kode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(codeBackground, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    Text(
                        text = content.codeExample,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFCDD6F4),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizPreviewInheritancePreview() {
    QuizPreviewScreen(
        topicRoute = "quiz/inheritance",
        onNavigateBack = {},
        onMulaiKuis = {}
    )
}

@Preview(showBackground = true)
@Composable
fun QuizPreviewArraysPreview() {
    QuizPreviewScreen(
        topicRoute = "quiz/arrays",
        onNavigateBack = {},
        onMulaiKuis = {}
    )
}

@Preview(showBackground = true)
@Composable
fun QuizPreviewLoopingPreview() {
    QuizPreviewScreen(
        topicRoute = "quiz/looping",
        onNavigateBack = {},
        onMulaiKuis = {}
    )
}