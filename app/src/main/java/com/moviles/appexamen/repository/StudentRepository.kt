package com.moviles.appexamen.repository

import android.content.Context
import com.moviles.appexamen.data.DatabaseBuilder
import com.moviles.appexamen.models.Student
import com.moviles.appexamen.data.StudentDao
import com.moviles.appexamen.models.StudentEntity
import com.moviles.appexamen.network.ApiService

class StudentRepository(private val context: Context) {

    private val studentDao = DatabaseBuilder.getInstance(context).studentDao()

    // Obtener estudiantes desde la base de datos local
    suspend fun getStudentsByCourse(courseId: Int): List<Student> {
        return studentDao.getStudentsByCourse(courseId).map { student ->
            student.toDomain()
        }
    }

    // Insertar nuevos estudiantes en la base de datos local
    suspend fun insertStudents(students: List<Student>, courseId: Int) {
        studentDao.addStudent(students.map { student ->
            student.copy(courseId = courseId).toEntity()
        })
    }


    // Limpiar todos los estudiantes de la base de datos local
    suspend fun clearStudents() {
        studentDao.deleteStudent()
    }

    // Sincronizar los estudiantes entre la base de datos local y la API


    // Convertir la entidad de la base de datos (StudentEntity) al modelo de dominio (Student)
    private fun StudentEntity.toDomain(): Student {
        return Student(
            id = this.id,
            name = this.name,
            email = this.email,
            phone = this.phone,
            courseId = this.courseId
        )
    }

    // Convertir el modelo de dominio (Student) a la entidad de la base de datos (StudentEntity)
    private fun Student.toEntity(): StudentEntity {
        return StudentEntity(
            id = this.id ?: 0, // Asignamos un valor predeterminado si `id` es nulo
            name = this.name,
            email = this.email,
            phone = this.phone ?: "", // Asignar un valor vacío si `phone` es nulo
            courseId = this.courseId?: 0,
        )
    }

}
