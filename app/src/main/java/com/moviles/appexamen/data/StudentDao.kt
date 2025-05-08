package com.moviles.appexamen.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.appexamen.models.CourseEntity
import com.moviles.appexamen.models.StudentEntity

@Dao
interface StudentDao {

    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(events: List<StudentEntity>)

    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}
}