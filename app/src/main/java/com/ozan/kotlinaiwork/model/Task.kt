package com.ozan.kotlinaiwork.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Projeye ait görevleri temsil eden veri modeli
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = ["id"],
        childColumns = ["projectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("projectId")]
)
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val priority: Int = 1, // 0: Düşük, 1: Normal, 2: Yüksek
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
