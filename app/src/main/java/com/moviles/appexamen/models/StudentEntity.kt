package com.moviles.appexamen.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")

data class StudentEntity(
        @PrimaryKey val id: Int,
        val name: String,
        val email: String,
        val phone: String,
        val courseId: Int

)

// Convert entity to model
fun StudentEntity.toStudent(): Student {
        return Student(id, name, email, phone, courseId)
}
