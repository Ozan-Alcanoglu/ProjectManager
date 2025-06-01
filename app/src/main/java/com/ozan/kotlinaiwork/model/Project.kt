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
    val priority: Int = 2,
    val status: String = "active",

) {

}
