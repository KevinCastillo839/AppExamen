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
//PRUEBAAAA
// Convertir entidad a modelo
fun StudentEntity.toStudent(): Student {
        return Student(id, name, email, phone, courseId)
}
/*
// Convertir modelo a entidad
fun Student.toEntity(): StudentEntity {
        return StudentEntity(id, name, email, phone, courseId)
}
*/