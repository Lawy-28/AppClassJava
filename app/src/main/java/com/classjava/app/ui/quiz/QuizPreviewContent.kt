package com.classjava.app.ui.quiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.ui.graphics.vector.ImageVector

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