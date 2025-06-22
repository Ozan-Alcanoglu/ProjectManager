package com.ozan.kotlintodoproject.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ozan.kotlintodoproject.MainActivity
import com.ozan.kotlintodoproject.ProjectApplication
// R sınıfı kullanılmıyor
import com.ozan.kotlintodoproject.service.ProjectService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class HighPriorityWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val projectService: ProjectService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val projects = projectService.getHighPriorityProjects()
            if (projects.isNotEmpty()) {
                showNotification(projects)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Hata durumunda işi tekrar dene
            Result.retry()
        }
    }

    private fun showNotification(projects: List<com.ozan.kotlintodoproject.model.Project>) {
        val notificationManager = 
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channelId = ProjectApplication.HIGH_PRIORITY_CHANNEL_ID
        val channelName = "Yüksek Öncelikli Projeler"
        
        // Android 8.0 ve üzeri için bildirim kanalı oluştur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Yüksek öncelikli projeler için hatırlatmalar"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tıklandığında açılacak aktivite
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Bildirimi oluştur
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)  // Varsayılan bir ikon kullanıyoruz
            .setContentTitle("⏰ Yüksek Öncelikli Projeleriniz Var!")
            .setContentText("${projects.size} adet yüksek öncelikli projeniz var.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${projects.joinToString("\n") { "• ${it.title}" }}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Bildirimi göster
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}

