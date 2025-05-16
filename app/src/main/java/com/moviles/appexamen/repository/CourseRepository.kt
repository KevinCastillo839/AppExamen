package com.moviles.appexamen.repository

import android.content.Context
import com.moviles.appexamen.data.DatabaseBuilder
import com.moviles.appexamen.models.Course
import com.moviles.appexamen.models.CourseEntity

import kotlin.collections.map

class CourseRepository(private val context: Context) {

    private val courseDao = DatabaseBuilder.getInstance(context).courseDao()

    suspend fun getCourses(): List<Course> {
        return courseDao.getAllCourses().map { course ->
            course.toDomain()
        }
    }


    suspend fun insertCourse(courses: List<Course>) {
        courseDao.insertCourse(courses.map { course -> course.toEntity() })
    }


    suspend fun clearCourses() {
        courseDao.deleteAllCourses()
    }

    private fun CourseEntity.toDomain(): Course {
        return Course(
            id = this.id,
            name = this.name,
            schedule = this.schedule,
            professor = this.professor,
            description = this.description,
            imageUrl = this.imageUrl
        )
    }

    private fun Course.toEntity(): CourseEntity {
        return CourseEntity(
            id = this.id ?: 0, // Use a default value (for example, 0) if id is null
            name = this.name,
            schedule = this.schedule,
            professor = this.professor,
            description = this.description,
            imageUrl = this.imageUrl
        )
    }

}
