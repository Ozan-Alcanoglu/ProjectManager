package com.ozan.kotlinaiwork.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Hobi projesini temsil eden veri modeli
 */
@Entity(tableName = "projects")
data class Project(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val category: String? = null,
    val priority: Int = 2, // 0: Düşük, 1: Orta, 2: Yüksek
    val status: String = "active", // active, completed, on_hold, cancelled
    val startDate: Long = System.currentTimeMillis(),
    val targetDate: Long? = null,
    val imageUri: String? = null,
    val estimatedHours: Float = 0f,
    val spentHours: Float = 0f
) {
    val progress: Float
        get() = if (estimatedHours > 0) (spentHours / estimatedHours).coerceIn(0f, 1f) else 0f
}
