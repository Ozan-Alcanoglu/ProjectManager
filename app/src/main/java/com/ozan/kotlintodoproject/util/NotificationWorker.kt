package com.ozan.kotlintodoproject.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ozan.kotlintodoproject.ProjectApplication
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
            val highPriorityProjects = projectService.getHighPriorityProjects()

            if (highPriorityProjects.isNotEmpty()) {
                highPriorityProjects.forEach { project ->
                    sendNotification(
                        title = "⚠️ Önemli Proje: ${project.title}",
                        message = "Bu proje yüksek öncelikli. Hatırlatma: ${project.description!!.take(50)}"
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }


    private fun sendNotification(title: String, message: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = manager.getNotificationChannel(ProjectApplication.NOTIFICATION_CHANNEL_ID)
            if (channel == null) {
                val newChannel = NotificationChannel(
                    ProjectApplication.NOTIFICATION_CHANNEL_ID,
                    "Project Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Channel for project notifications"
                }
                manager.createNotificationChannel(newChannel)
            }
        }

        val notification = NotificationCompat.Builder(applicationContext, ProjectApplication.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
