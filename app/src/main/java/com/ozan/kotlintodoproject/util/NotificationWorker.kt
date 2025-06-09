import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ozan.kotlintodoproject.model.Project
import com.ozan.kotlintodoproject.service.ProjectService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope

class NotificationWorker(
    @ApplicationContext private val context: Context,
    workerParams: WorkerParameters,
    private val service: ProjectService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val projects = service.getAllProjects() // Tüm projeleri çek

            projects.forEach { project ->
                val priority = project.priority
                val shouldSendNotification = when (priority) {
                    2 -> true  // Yüksek öncelik - her gün
                    1 -> false // Orta öncelik - 3 günde 1
                    0 -> false // Düşük öncelik - haftalık
                    else -> false
                }

                if (shouldSendNotification) {
                    sendNotification(project)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun sendNotification(project: Project) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "project_notifications"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Projeler", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Proje Hatırlatma")
            .setContentText("${project.title} projesi için hatırlatma zamanı.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(project.id, notification)
    }
}
