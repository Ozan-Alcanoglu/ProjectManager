package com.ozan.kotlintodoproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ozan.kotlintodoproject.navigation.NavGraph
import com.ozan.kotlintodoproject.ui.theme.KotlinAıWorkTheme
import com.ozan.kotlintodoproject.worker.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val request = PeriodicWorkRequestBuilder<NotificationWorker>(
            15,
            TimeUnit.MINUTES,
            5,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "project_notification_worker",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )



        setContent {
            KotlinAıWorkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProjectApp()
                }
            }
        }
    }


}

@Composable
fun ProjectApp() {
    val navController = rememberNavController()

    NavGraph(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    KotlinAıWorkTheme {
        ProjectApp()
    }
}
