package com.moviles.appexamen.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moviles.appexamen.models.CourseEntity

@Database(entities = [CourseEntity::class], version = 2, exportSchema = false)
abstract class CourseDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
}
