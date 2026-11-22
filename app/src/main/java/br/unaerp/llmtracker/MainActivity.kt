package br.unaerp.llmtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.unaerp.llmtracker.ui.navegacao.AppNavegacao
import br.unaerp.llmtracker.ui.theme.LlmTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LlmTrackerTheme {
                AppNavegacao()
            }
        }
    }
}
