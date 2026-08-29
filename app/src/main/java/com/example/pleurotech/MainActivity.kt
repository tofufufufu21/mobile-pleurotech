package com.example.pleurotech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.pleurotech.data.PleuroTechRepository
import com.example.pleurotech.ui.screens.PleuroTechApp
import com.example.pleurotech.ui.theme.PleurotechTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val repository = remember { PleuroTechRepository.create(context) }
            PleuroTechApp(repository = repository)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PleuroTechPreview() {
    PleurotechTheme {
        PleuroTechApp(repository = PleuroTechRepository.seeded())
    }
}
