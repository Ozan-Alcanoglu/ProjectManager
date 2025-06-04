package com.ozan.kotlintodoproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String
)
