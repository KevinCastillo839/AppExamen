package com.moviles.appexamen.models

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Student(
    val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val courseId: Int?
)
