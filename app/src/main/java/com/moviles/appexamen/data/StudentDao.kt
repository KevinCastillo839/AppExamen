package com.moviles.appexamen.data

import androidx.room.*
import com.moviles.appexamen.models.Student
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

    @Query("SELECT * FROM students WHERE courseId = :courseId")
    suspend fun getStudentsByCourse(courseId: Int): List<StudentEntity>

    @Query("DELETE FROM students")
    suspend fun deleteStudent()

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Int): StudentEntity?
///PRUEBAAAAA//////////////
    @Dao
    interface StudentDao {
        @Query("SELECT * FROM students WHERE id = :id")
        suspend fun getStudentById(id: Int): StudentEntity?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(student: StudentEntity)
    }


}
