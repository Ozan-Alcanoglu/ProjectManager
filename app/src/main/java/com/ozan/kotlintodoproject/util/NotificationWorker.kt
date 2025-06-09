package com.ozan.kotlintodoproject.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ozan.kotlintodoproject.service.ProjectService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val projectService: ProjectService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val allProjects = projectService.getAll()

            allProjects.forEach { project ->
                val shouldNotify = when (project.priority) {
                    2 -> true
                    1 -> {
                        val now = System.currentTimeMillis()
                        now % TimeUnit.DAYS.toMillis(3) < TimeUnit.MINUTES.toMillis(15)
                    }
                    0 -> {
                        val now = System.currentTimeMillis()
                        now % TimeUnit.SECONDS.toMillis(10) < TimeUnit.SECONDS.toMillis(5)
                    }
                    else -> false
                }

                if (shouldNotify) {
                    sendNotification("Projelerim", "${project.title} projesi için bildirim zamanı!")
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val channelId = "project_channel"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Project Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
