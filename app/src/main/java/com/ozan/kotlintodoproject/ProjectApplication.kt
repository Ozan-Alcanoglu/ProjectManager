package com.ozan.kotlintodoproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProjectApplication : Application() {
    companion object {
        const val HIGH_PRIORITY_CHANNEL_ID = "project_high_priority_channel"
        const val MEDIUM_PRIORITY_CHANNEL_ID = "project_medium_priority_channel"
        const val LOW_PRIORITY_CHANNEL_ID = "project_low_priority_channel"
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    }


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val highChannel = NotificationChannel(
                HIGH_PRIORITY_CHANNEL_ID,
                "Yüksek Öncelikli Bildirimler",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Acil ve önemli projeler için"
            }

            val mediumChannel = NotificationChannel(
                MEDIUM_PRIORITY_CHANNEL_ID,
                "Orta Öncelikli Bildirimler",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Orta öncelikli projeler için"
            }

            val lowChannel = NotificationChannel(
                LOW_PRIORITY_CHANNEL_ID,
                "Düşük Öncelikli Bildirimler",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Hatırlatma düzeyinde projeler için"
            }

            manager.createNotificationChannel(highChannel)
            manager.createNotificationChannel(mediumChannel)
            manager.createNotificationChannel(lowChannel)
        }
    }

}
