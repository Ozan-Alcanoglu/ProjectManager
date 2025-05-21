package com.ozan.kotlinaiwork

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.ozan.kotlinaiwork.navigation.NavGraph
import com.ozan.kotlinaiwork.ui.theme.KotlinAıWorkTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            KotlinAıWorkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HobbyProjectApp()
                }
            }
        }
    }


}

@Composable
fun HobbyProjectApp() {
    val navController = rememberNavController()

    NavGraph(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    KotlinAıWorkTheme {
        HobbyProjectApp()
    }
}
