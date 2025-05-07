package com.moviles.appexamen.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.appexamen.models.CourseEntity

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(events: List<CourseEntity>)

    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()
}