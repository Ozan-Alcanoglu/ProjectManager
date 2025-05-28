package com.ozan.kotlinaiwork.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "project_items",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Branch::class,
            parentColumns = ["id"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Task(
    @PrimaryKey
    val id:String=UUID.randomUUID().toString(),
    val branchId: String? = null,

    val description: String? = null,

    val projectId: String, // Hangi projeye ait

    val parentId: String? = null, // Eğer null ise kök öğe

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
)
