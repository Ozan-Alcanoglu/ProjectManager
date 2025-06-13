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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ozan.kotlintodoproject.navigation.NavGraph
import com.ozan.kotlintodoproject.ui.theme.KotlinAıWorkTheme
import com.ozan.kotlintodoproject.util.HighPriorityWorker
import com.ozan.kotlintodoproject.util.LowPriorityWorker
import com.ozan.kotlintodoproject.util.MediumPriorityWorker
import com.ozan.kotlintodoproject.worker.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val highPriorityRequest = PeriodicWorkRequestBuilder<HighPriorityWorker>(
            1, TimeUnit.DAYS
        ).build()

        val mediumPriorityRequest = PeriodicWorkRequestBuilder<MediumPriorityWorker>(
            4, TimeUnit.DAYS
        ).build()

        val lowPriorityRequest = PeriodicWorkRequestBuilder<LowPriorityWorker>(
            7, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "high_priority_notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            highPriorityRequest
        )

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "medium_priority_notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            mediumPriorityRequest
        )

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "low_priority_notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            lowPriorityRequest
        )

        val testRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(testRequest)


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
