package com.moviles.appexamen.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val schedule: String,
    val professor: String,
    val description: String,
    val imageUrl: String?
)

