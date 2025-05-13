package com.moviles.appexamen.data

import androidx.room.*
import com.moviles.appexamen.models.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudent(students: List<StudentEntity>)

    @Update
    suspend fun update(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)

    @Query("SELECT * FROM STUDENTS WHERE courseId = :courseId")
    suspend fun getStudentsByCourse(courseId: Int): List<StudentEntity>

    @Query("DELETE FROM STUDENTS")
    suspend fun deleteStudent()

    @Query("SELECT * FROM STUDENTS WHERE id = :id")
    suspend fun getStudentById(id: Int): StudentEntity
}
