package com.ozan.kotlinaiwork.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID


@Entity(tableName = "projects")
data class Project(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val priority: Int = 2,
    val status: String = "active",
    val createdAt: Date = Date()

)


