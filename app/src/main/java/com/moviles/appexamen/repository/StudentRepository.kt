package com.moviles.appexamen.repository

import android.content.Context
import com.moviles.appexamen.data.DatabaseBuilder
import com.moviles.appexamen.models.Student
import com.moviles.appexamen.data.StudentDao
import com.moviles.appexamen.models.StudentEntity
import com.moviles.appexamen.network.ApiService
import kotlinx.coroutines.flow.Flow

class StudentRepository(private val context: Context) {

    private val studentDao = DatabaseBuilder.getInstance(context).studentDao()

    // Get students from the local database
    suspend fun getStudentsByCourse(courseId: Int): List<Student> {
        return studentDao.getStudentsByCourse(courseId).map { student ->
            student.toDomain()
        }
    }

    //Insert new students into the local database
    suspend fun insertStudents(students: List<Student>, courseId: Int) {
        studentDao.addStudent(students.map { student ->
            student.copy(courseId = courseId).toEntity()
        })
    }


    // Clear all students from the local database
    suspend fun clearStudents() {
        studentDao.deleteStudent()
    }


    // Convert the database entity (StudentEntity) to the domain model (Student)
    private fun StudentEntity.toDomain(): Student {
        return Student(
            id = this.id,
            name = this.name,
            email = this.email,
            phone = this.phone,
            courseId = this.courseId
        )
    }

    // Convert the domain model (Student) to the database entity (StudentEntity)
    private fun Student.toEntity(): StudentEntity {
        return StudentEntity(
            id = this.id ?: 0, // We assign a default value if id is null
            name = this.name,
            email = this.email,
            phone = this.phone ?: "", // Assign an empty value if phone is null
            courseId = this.courseId?: 0,
        )
    }
    //METHOD FOR DETAILS
    suspend fun getStudentById(id: Int): Student? {
        val entity = studentDao.getStudentById(id)
        return entity?.let {
            Student(
                id = it.id,
                name = it.name,
                email = it.email,
                phone = it.phone,
                courseId = it.courseId
            )
        }
    }

}

