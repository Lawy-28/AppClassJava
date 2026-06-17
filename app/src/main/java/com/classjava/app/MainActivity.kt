package com.classjava.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.classjava.app.config.AppwriteClient
import com.classjava.app.ui.navigation.AppNavHost
import com.classjava.app.ui.theme.ClassJavaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppwriteClient.initialize(applicationContext)
        setContent {
            ClassJavaTheme {
                AppNavHost()
            }
        }
    }
}
